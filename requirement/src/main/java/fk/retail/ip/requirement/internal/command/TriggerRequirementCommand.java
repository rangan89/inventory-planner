package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.restbus.client.entity.Message;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fk.retail.ip.requirement.config.TriggerRequirementConfiguration;
import fk.retail.ip.requirement.internal.Constants;
import fk.retail.ip.requirement.internal.entities.GroupFsn;
import fk.retail.ip.requirement.internal.entities.Policy;
import fk.retail.ip.requirement.internal.enums.PolicyType;
import fk.retail.ip.requirement.internal.repository.GroupFsnRepository;
import fk.retail.ip.requirement.internal.repository.PolicyRepository;
import fk.retail.ip.requirement.model.CalculateRequirementRequest;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.restbus.sender.RestbusMessageSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TriggerRequirementCommand {

    private Set<Long> groupIds = Sets.newHashSet();
    private Set<String> fsns = Sets.newHashSet();
    private final GroupFsnRepository groupFsnRepository;
    private final PolicyRepository policyRepository;
    private final RestbusMessageSender restbusMessageSender;
    private final TriggerRequirementConfiguration triggerRequirementConfiguration;
    private final ObjectMapper mapper;
    private Map<String, Date> cronValueMap = Maps.newHashMap();

    @Inject
    public TriggerRequirementCommand(GroupFsnRepository groupFsnRepository,
                                     PolicyRepository policyRepository,
                                     RestbusMessageSender restbusMessageSender,
                                     TriggerRequirementConfiguration triggerRequirementConfiguration,
                                     ObjectMapper mapper) {
        this.groupFsnRepository = groupFsnRepository;
        this.policyRepository = policyRepository;
        this.restbusMessageSender = restbusMessageSender;
        this.triggerRequirementConfiguration = triggerRequirementConfiguration;
        this.mapper = mapper;
    }

    public List<String> execute() {
        if (!CollectionUtils.isEmpty(fsns) || !CollectionUtils.isEmpty(groupIds)) {
            Set<GroupFsn> projectionFsns = Sets.newHashSet();
            projectionFsns.addAll(groupFsnRepository.findByGroupIds(groupIds));
            projectionFsns.addAll(groupFsnRepository.findByFsns(fsns));
            return triggerRequirements(projectionFsns);
        }
        int pageSize = triggerRequirementConfiguration.getFetchDataBatchSize();
        int pageNo = 0;
        long totalCount;
        List<String> successList = Lists.newArrayList();
        do {
            Page<GroupFsn> page = groupFsnRepository.findAll(
                    PageRequest.builder().pageNumber(pageNo).pageSize(pageSize).build());
            Set<GroupFsn> fsns =
                    page.getContent().stream().filter(groupFsn -> groupFsn.getGroup().isEnabled())
                            .collect(Collectors.toSet());
            List<String> success = triggerRequirements(fsns);
            successList.addAll(success);
            totalCount = page.getTotalCount();
        } while (++pageNo * pageSize < totalCount);
        return successList;
    }

    private List<String> triggerRequirements(Set<GroupFsn> groupFsns) {
        Set<Long> groupIds =
                groupFsns.stream().map(groupFsn -> groupFsn.getGroup().getId())
                        .collect(Collectors.toSet());
        Map<Long, Policy> groupLevelPlanningCyclePolicies =
                policyRepository.fetchByGroup(groupIds, PolicyType.PLANNING_CYCLE).stream().collect(
                        Collectors.toMap(policy -> policy.getGroup().getId(), policy -> policy));
        Set<String> fsns =
                groupFsns.stream().map(GroupFsn::getFsn).collect(Collectors.toSet());
        Map<String, Policy> fsnLevelPlanningCyclePolicies =
                policyRepository.fetch(fsns, PolicyType.PLANNING_CYCLE).stream().collect(
                        Collectors.toMap(policy -> policy.getFsn(), policy -> policy,
                                         (policy1, policy2) -> policy1.getCreatedAt()
                                                                       .after(policy2.getCreatedAt())
                                                               ? policy1 : policy2
                        ));
        Map<String, Policy> skipListPolicies =
                policyRepository.fetch(fsns, PolicyType.SKIP_LIST).stream().collect(
                        Collectors.toMap(policy -> policy.getFsn(), policy -> policy));
        List<GroupFsn> toBePlanned = groupFsns.stream()
                .filter(groupFsn -> !isSkipped(groupFsn, skipListPolicies) && isPlannedToday(
                        groupFsn, fsnLevelPlanningCyclePolicies,
                        groupLevelPlanningCyclePolicies))
                .collect(Collectors.toList());

        List<String> successList = Lists.newArrayList();
        Lists.partition(toBePlanned, triggerRequirementConfiguration.getCreateReqBatchSize())
                .forEach(partitionedGroupFsns -> {
                    Message message = getMessageInstance();
                    List<String> partitionedFsns =
                            partitionedGroupFsns.stream().map(GroupFsn::getFsn).collect(
                                    Collectors.toList());
                    CalculateRequirementRequest createProjectionRequest =
                            createRequestInstance(partitionedFsns);
                    try {
                        message.setPayload(mapper.writeValueAsString(createProjectionRequest));
                        restbusMessageSender.send(message);
                        successList.addAll(partitionedFsns);
                    } catch (JsonProcessingException e) {
                        log.error("Unable to serialize request object ", e);
                        log.debug("Failed fsns: {}", partitionedFsns);
                    }
                });
        return successList;
    }

    private CalculateRequirementRequest createRequestInstance(List<String> projectionFsns) {
        CalculateRequirementRequest calculateRequirementRequest = new CalculateRequirementRequest();
        calculateRequirementRequest.getFsns().addAll(projectionFsns);
        return calculateRequirementRequest;
    }

    private Message getMessageInstance() {
        Message message = new Message();
        message.setExchangeName(triggerRequirementConfiguration.getProjectionQueueName());
        message.setExchangeType("queue");
        message.setHttpMethod("POST");
        message.setHttpUri(triggerRequirementConfiguration.getUrl());
        message.setAppId("fk-rp-populator");
        return message;
    }

    public TriggerRequirementCommand withGroupIds(Set<Long> groupIds) {
        if (groupIds != null) {
            this.groupIds = groupIds;
        }
        return this;
    }

    public TriggerRequirementCommand withFsns(Set<String> fsns) {
        if (fsns != null) {
            this.fsns = fsns;
        }
        return this;
    }

    private boolean isPlannedToday(GroupFsn groupFsn, Map<String, Policy> fsnLevelPolicies,
                                   Map<Long, Policy> groupLevelPolicies) {
        Policy policy = fsnLevelPolicies.get(groupFsn.getFsn());
        //if fsn level policy is missing, search group level
        if (policy == null) {
            policy = groupLevelPolicies.get(groupFsn.getGroup().getId());
        }
        //if policy not found then fsn is not planned
        if (policy == null) {
            log.warn(groupFsn + " : " + String
                    .format(Constants.VALID_POLICY_NOT_FOUND, PolicyType.PLANNING_CYCLE));
            return false;
        } else {
            try {
                Map<String, String> value;
                try {
                    Map<String, Map<String, String>> placementPolicyWhMap =
                            mapper.readValue(policy.getValue(),
                                             new TypeReference<Map<String, Map<String, String>>>() {
                                             });
                    value = placementPolicyWhMap.entrySet().iterator().next().getValue();
                } catch (JsonMappingException e) {
                    value = mapper.readValue(policy.getValue(),
                                             new TypeReference<Map<String, String>>() {
                                             });
                }
                String dayOfMonth = value.get("day_of_month");
                String month = value.get("month_of_year");
                String dayOfWeek = value.get("day_of_week");
                String cronExpression =
                        String.format("* * * %s %s %s", dayOfMonth, month, dayOfWeek);
                Date nextExecutionDate = cronValueMap.get(cronExpression);
                if (nextExecutionDate == null) {
                    CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
                    nextExecutionDate = generator.next(getDate());
                    cronValueMap.put(cronExpression, nextExecutionDate);
                }
                SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
                return format.format(nextExecutionDate).equals(format.format(getDate()));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                return false;
            }
        }
    }

    private boolean isSkipped(GroupFsn groupFsn, Map<String, Policy> fsnLevelPolicies) {
        Policy policy = fsnLevelPolicies.get(groupFsn.getFsn());
        if (policy == null) {
            return false;
        }
        try {
            Map<String, String> policyValue =
                    mapper.readValue(policy.getValue(), new TypeReference<Map<String, String>>() {
                    });
            Date skipUpto = new SimpleDateFormat("d/M/yyyy").parse(policyValue.get("skip_upto"));
            return getDate().before(skipUpto);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    public Date getDate() {
        return new Date();
    }
}
