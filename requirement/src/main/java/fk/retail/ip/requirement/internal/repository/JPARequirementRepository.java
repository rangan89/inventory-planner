package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.entities.RequirementSnapshot;
import fk.sp.common.extensions.jpa.Page;
import fk.sp.common.extensions.jpa.PageRequest;
import fk.sp.common.extensions.jpa.SimpleJpaGenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

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




    public List<Requirement> findRequirements(List<Long> projectionIds, String requirementState, Map<String, Object> filters, int pageNumber, int pageSize) {
        TypedQuery<Requirement> query = getCriteriaQuery(projectionIds, requirementState, filters);
        query.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
        return query.getResultList();
    }

    public List<Requirement> findRequirements(List<Long> projectionIds, String requirementState, Map<String, Object> filters) {
        TypedQuery<Requirement> query = getCriteriaQuery(projectionIds, requirementState, filters);
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

    private TypedQuery<Requirement> getCriteriaQuery(List<Long> projectionIds, String requirementState, Map<String, Object> filters) {
         EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Requirement> criteriaQuery = criteriaBuilder.createQuery(Requirement.class);
        Root<Requirement> requirementRoot = criteriaQuery.from(Requirement.class);
        Join<Requirement, RequirementSnapshot> requirementSnapshot = requirementRoot.join("requirementSnapshot");
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
        List<String> fsns = (List<String>) filters.get("fsns");

        if (fsns != null && !fsns.isEmpty()) {
            predicate = criteriaBuilder.isTrue(requirementRoot.get("fsn").in(fsns));
            predicates.add(predicate);
        }
        if (filters.containsKey("international") && !filters.get("international").toString().isEmpty()) {
            predicate = criteriaBuilder.equal(requirementRoot.get("international"), Integer.parseInt(filters.get("international").toString()));
            predicates.add(predicate);
        }
        if (filters.containsKey("price_from") && !filters.get("price_from").toString().isEmpty()) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(requirementRoot.get("app"), Integer.parseInt(filters.get("price_from").toString()));
            predicates.add(predicate);
        }
        if (filters.containsKey("price_to") && !filters.get("price_to").toString().isEmpty()) {
            predicate = criteriaBuilder.lessThanOrEqualTo(requirementRoot.get("app"), Integer.parseInt(filters.get("price_to").toString()));
            predicates.add(predicate);
        }
        List<String> group = (List<String>) filters.get("group");
        if (group != null && !group.isEmpty()) {
            predicate = criteriaBuilder.isTrue(requirementSnapshot.get("group").get("name").in(group));
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

    //TODO: legacy code
    @Override
    public int updateProjection(Collection<Long> projectionIds, String toState) {
        return getEntityManager()
                .createNativeQuery("UPDATE projections SET current_state=:state WHERE id IN (:ids)")
                .setParameter("ids", projectionIds)
                .setParameter("state", toState)
                .executeUpdate();
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

}
