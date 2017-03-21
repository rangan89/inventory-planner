package fk.retail.ip.requirement.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import fk.retail.ip.requirement.internal.entities.AbstractEntity;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.enums.RequirementApprovalState;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.json.JSONObject;

/**
 *
 * @author Pragalathan M<pragalathan.m@flipkart.com>
 */
public class ApprovalService<E extends AbstractEntity> {

    private JSONObject actions;

    @Inject
    public ApprovalService(@Named("actionConfiguration") String actionConfiguration) {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(actionConfiguration))) {
            actions = new JSONObject(CharStreams.toString(reader));
        } catch (Exception ex) {
            throw new RuntimeException("Error in reading config file", ex);
        }
    }

    public String getTargetState(String action) {
        return actions.getJSONObject(action).getString("nextState");
    }

    public void changeState(List<E> items,
            String userId,
            String action,
            Function<E, String> getter,
            StageChangeAction<E>... actionListeners) {
        JSONObject stateMachine = actions.getJSONObject(action);
        String fromState = stateMachine.getString("currentState");
        String toState = stateMachine.getString("nextState");
        boolean forward = stateMachine.getBoolean("isForward");
        validate(items, fromState, getter);
        for (StageChangeAction<E> consumer : actionListeners) {
            consumer.execute(userId, fromState, toState, forward, items);
        }
    }

    private void validate(List<E> items, String fromState, Function<E, String> getter) {
        for (E item : items) {
            String currentState = getter.apply(item);
            if (!currentState.equals(fromState)) {
                throw new IllegalStateException("Entity[id=" + item.getId() + "] is not in " + fromState + " state");
            }
        }
    }

    public static interface StageChangeAction<E extends AbstractEntity> {

        void execute(String userId,
                String fromState,
                String toState,
                boolean forward,
                List<E> entity);
    }

    public static class CopyOnStateChangeAction implements StageChangeAction<Requirement> {

        private RequirementRepository repository;

        public CopyOnStateChangeAction(RequirementRepository requirementRepository) {
            this.repository = requirementRepository;
        }

        @Override
        public void execute(String userId, String fromState, String toState, boolean forward, List<Requirement> entities) {
            Map<String, List<Requirement>> fsnToRequirements = entities.stream().collect(Collectors.groupingBy(Requirement::getFsn));
            List<Requirement> toEntities = repository.findEnabledRequirementsByStateFsn(toState, fsnToRequirements.keySet());
            Table<String, String, Requirement> cdoStateEntityMap = getCDOEntityMap(toState,  fsnToRequirements.keySet());
            boolean isIPCReviewState = RequirementApprovalState.IPC_REVIEW.toString().equals(toState);
            fsnToRequirements.keySet().stream().forEach((fsn) -> {
                fsnToRequirements.get(fsn).stream().forEach((entity) -> {
                    Optional<Requirement> toStateEntity = toEntities.stream().filter(e -> e.getWarehouse().equals(entity.getWarehouse()) && e.getFsn().equals(entity.getFsn())).findFirst();
                    if (forward) {
                        if (toStateEntity.isPresent()) {
                            toStateEntity.get().setQuantity(entity.getQuantity());
                            if(isIPCReviewState) {
                                toStateEntity.get().setQuantity(cdoStateEntityMap.get(entity.getFsn(), entity.getWarehouse()).getQuantity());
                            }
                            toStateEntity.get().setSupplier(entity.getSupplier());
                            toStateEntity.get().setApp(entity.getApp());
                            toStateEntity.get().setSla(entity.getSla());
                            toStateEntity.get().setPreviousStateId(entity.getId());
                            toStateEntity.get().setCreatedBy(userId);
                            toStateEntity.get().setCurrent(true);
                            entity.setCurrent(false);
                        } else {
                            Requirement newEntity = new Requirement(entity);
                            if(isIPCReviewState) {
                                newEntity.setQuantity(cdoStateEntityMap.get(entity.getFsn(), entity.getWarehouse()).getQuantity());
                            }
                            newEntity.setState(toState);
                            newEntity.setCreatedBy(userId);
                            newEntity.setPreviousStateId(entity.getId());
                            newEntity.setCurrent(true);
                            repository.persist(newEntity);
                            entity.setCurrent(false);
                        }
                    } else {
                        toStateEntity.ifPresent(e -> { // this will always be present
                            e.setCurrent(true);
                            entity.setCurrent(false);
                        });
                    }
                });
            });
        }

        private Table<String,String,Requirement> getCDOEntityMap(String toState, Set<String> fsns) {
            Table<String, String, Requirement> cdoStateRequirementMap = HashBasedTable.create();
            boolean isIPCReviewState = RequirementApprovalState.IPC_REVIEW.toString().equals(toState);
            if (isIPCReviewState) {
                String cdoState = RequirementApprovalState.CDO_REVIEW.toString();
                List<Requirement> cdoReviewEntities = repository.findEnabledRequirementsByStateFsn(cdoState, fsns);
                cdoReviewEntities.forEach((entity) -> {
                    cdoStateRequirementMap.put(entity.getFsn(), entity.getWarehouse(), entity);
                });
            }
            return cdoStateRequirementMap ;
        }

        private Table<String,String,Requirement> getToStateEntity(String toState, Set<String> fsns) {
            Table<String, String, Requirement> toStateRequirementMap = HashBasedTable.create();
            List<Requirement> toStateEntities = repository.findEnabledRequirementsByStateFsn(toState, fsns);
            toStateEntities.forEach((entity) -> {
                toStateRequirementMap.put(entity.getFsn(), entity.getWarehouse(), entity);
            });

            return toStateRequirementMap ;
        }
    }
}
