package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.IdeaRepositoryCustom;
import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class IdeaRepositoryCustomImpl implements IdeaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public IdeaPageDTO findIdeasByParameters(String title,
                                             String text,
                                             List<Status> statuses,
                                             List<String> categories,
                                             List<String> users,
                                             String sortDirection,
                                             Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Idea> criteriaQuery = cb.createQuery(Idea.class);
        Root<Idea> root = criteriaQuery.from(Idea.class);

        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(cb.like(root.get("title"), "%" + title + "%"));
        }

        if (text != null) {
            predicates.add(cb.like(root.get("text"), "%" + text + "%"));
        }

        if (statuses != null && !statuses.isEmpty()) {
            predicates.add(root.get("status").in(statuses));
        }

        if (users != null && !users.isEmpty()) {
            predicates.add(root.join("user").get("username").in(users));
        }

        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.join("categoryList").get("text").in(categories));
        }

        //sorting by date

        List<Order> orders = new ArrayList<>();

        if (Objects.equals(sortDirection, "ASC")) {
            orders.add(cb.asc(root.get("date")));
        } else {
            orders.add(cb.desc(root.get("date")));
        }

        criteriaQuery.orderBy(orders);
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Idea> query = entityManager.createQuery(criteriaQuery);

        //getting total and paginating

        int total = query.getResultList().size();

        List<Idea> allIdeas = query.getResultList();

        if (pageable != null) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            int firstIndex = pageNumber * pageSize;

            List<Idea> pagedIdeas = new ArrayList<>();
            for (int i = 0; i < pageSize; i++) {
                if (firstIndex < allIdeas.size()) {
                    pagedIdeas.add(allIdeas.get(firstIndex));
                    firstIndex = firstIndex + 1;
                }
            }
            IdeaPageDTO ideaPageDTO = new IdeaPageDTO();
            ideaPageDTO.setPagedIdeas(new PageImpl<>(pagedIdeas, pageable, total));
            ideaPageDTO.setTotal(total);
            return ideaPageDTO;
        }

        return null;
    }
}
