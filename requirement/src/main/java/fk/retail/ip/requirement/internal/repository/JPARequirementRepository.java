package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Slf4j
public class JPARequirementRepository extends SimpleJpaGenericRepository<Requirement, Long> implements RequirementRepository {

    @Inject
    public JPARequirementRepository(Provider<EntityManager> entityManagerProvider) {
        super(entityManagerProvider);
    }

    @Override
    public List<Requirement> findRequirementByIds(List<Long> requirementIds) {
        //todo: now we find requirements by projecion_id(at fsn level. need to think abt it)
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findRequirementByIds", Requirement.class);
        query.setParameter("ids", requirementIds);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    @Override
    public List<Requirement> findActiveRequirementForState(List<Long> requirementIds, String state) {
        TypedQuery<Requirement> query = getEntityManager().
                createNamedQuery("findActiveRequirementForState", Requirement.class);
        query.setParameter("ids", requirementIds);
        query.setParameter("state", state);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    @Override
    public List<Requirement> findAllCurrentRequirements(String state) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findCurrentRequirementsByState", Requirement.class);
        query.setParameter("state", state);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    @Override
    public List<Requirement> findEnabledRequirementsByStateFsn(String state, Collection<String> fsns) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("findEnabledRequirementsByStateFsn", Requirement.class);
        query.setParameter("state", state);
        query.setParameter("fsns", fsns);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }




    public List<Requirement> findRequirements(List<Long> projectionIds, String requirementState, List<String> fsns , int pageNumber, int pageSize) {
        TypedQuery<Requirement> query = getCriteriaQuery(projectionIds, requirementState, fsns);
        query.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
        return query.getResultList();
    }

    public List<Requirement> findRequirements(List<Long> projectionIds, String requirementState, List<String> fsns) {
        TypedQuery<Requirement> query = getCriteriaQuery(projectionIds, requirementState, fsns);
        return query.getResultList();
    }

    @Override
    public List<Requirement> find(Collection<String> fsns, boolean enabled) {
        TypedQuery<Requirement> query = getEntityManager().createNamedQuery("Requirement.fetchEnabledByFsns", Requirement.class);
        query.setParameter("fsns", fsns);
        query.setParameter("enabled", enabled);
        List<Requirement> requirements = query.getResultList();
        return requirements;
    }

    private TypedQuery<Requirement> getCriteriaQuery(List<Long> projectionIds, String requirementState, List<String> fsns) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Requirement> criteriaQuery = criteriaBuilder.createQuery(Requirement.class);
        Root<Requirement> requirementRoot = criteriaQuery.from(Requirement.class);
        requirementRoot.fetch("requirementSnapshot");
        CriteriaQuery<Requirement> select = criteriaQuery.select(requirementRoot);
        List<Predicate> predicates = Lists.newArrayList();
        Predicate predicate = criteriaBuilder.equal(requirementRoot.get("current"), 1);
        predicates.add(predicate);
        predicate = criteriaBuilder.equal(requirementRoot.get("state"), requirementState);
        predicates.add(predicate);
        if (projectionIds != null && !projectionIds.isEmpty()) {
            predicate = criteriaBuilder.isTrue(requirementRoot.get("projectionId").in(projectionIds));
            predicates.add(predicate);
        }

        if (fsns != null && !fsns.isEmpty()) {
            predicate = criteriaBuilder.isTrue(requirementRoot.get("fsn").in(fsns));
            predicates.add(predicate);
        }

        select.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        TypedQuery<Requirement> query = entityManager.createQuery(select);
        return query;

    }

    //TODO: legacy code
    public List<Requirement> findByProjectionIds(List<Long> projectionIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("projectionIds", projectionIds);
        return fetchRequirements("findRequirementByProjectionIds", params);
    }


    @Override
    public void updateProjections(List<Requirement> requirements, Map<Long, String> groupToTargetState) {
        EntityManager entityManager = getEntityManager();
        String defaultState = groupToTargetState.get(Constants.DEFAULT_TRANSITION_GROUP);
        List<Long> projectionIds = requirements.stream().map(Requirement::getProjectionId).collect(Collectors.toList());
        int projectionSize = projectionIds.size();
        String projectionIdString = "(";
        for (long projectionId: projectionIds.subList(0, projectionSize - 1)) {
            projectionIdString += "\"" + projectionId + "\",";
        }
        projectionIdString += "\"" + projectionIds.get(projectionSize - 1) + "\"";
        projectionIdString += ")";
        String query = "UPDATE projections set current_state =  CASE ";
        for (Map.Entry<Long, String> entry : groupToTargetState.entrySet()) {
            query += " WHEN group_id = " + entry.getKey() + " THEN " + "\"" + entry.getValue() + "\"";
        }
        query +=" ELSE " + "\"" + defaultState + "\"" + " END where id in " + projectionIdString;

        Query q = entityManager.createNativeQuery(query);
        q.executeUpdate();
    }

    private List<Requirement> fetchRequirements(String query, Map<String, Object> params) {
        List<Requirement> requirements = Lists.newArrayList();
        int pageNo = 0;
        Page<Requirement> page;
        do {
            page = fetchRequirements(query, params, pageNo);
            requirements.addAll(page.getContent());
            pageNo += 1;
        } while (page.isHasMore());

        return requirements;
    }

    private Page<Requirement> fetchRequirements(String query, Map<String, Object> params, int pageNumber) {
        PageRequest pageRequest = getPageRequest(pageNumber, PAGE_SIZE);
        log.info("Fetching {} records for page: {}", PAGE_SIZE, pageNumber);
        return findAllByNamedQuery(query, params, pageRequest);
    }

    private PageRequest getPageRequest(int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest
                .builder()
                .pageNumber(pageNo)
                .pageSize(pageSize)
                .build();

        return pageRequest;
    }


    public List<Long> findProjectionIds(List<String> fsns, String state) {
        TypedQuery<Long> query = getEntityManager().createNamedQuery("Requirement.getProjectionIds",Long.class);
        query.setParameter("fsns", fsns);
        query.setParameter("state", state);
        return query.getResultList();
    }

}
