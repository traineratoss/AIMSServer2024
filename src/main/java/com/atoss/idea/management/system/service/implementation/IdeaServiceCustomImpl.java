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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class IdeaServiceCustomImpl implements IdeaRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public IdeaPageDTO findIdeasByParameters(String title,
                                             String text,
                                             List<Status> statuses,
                                             List<String> categories,
                                             List<String> users,
                                             String selectedDateFrom,
                                             String selectedDateTo,
                                             String sortDirection,
                                             String username,
                                             Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Idea> criteriaQuery = cb.createQuery(Idea.class);
        Root<Idea> root = criteriaQuery.from(Idea.class);

        List<Predicate> predicatesList = new ArrayList<>();

        if (username != null) {
            predicatesList.add(cb.equal(root.join("user").get("username"), username));
        }

        if (title != null) {
            predicatesList.add(cb.like(root.get("title"), "%" + title + "%"));
        }

        if (text != null) {
            predicatesList.add(cb.like(root.get("text"), "%" + text + "%"));
        }

        if (statuses != null && !statuses.isEmpty()) {
            predicatesList.add(root.get("status").in(statuses));
        }

        if (users != null && !users.isEmpty() && username == null) {
            predicatesList.add(root.join("user").get("username").in(users));
        }

        if (categories != null && !categories.isEmpty()) {
            predicatesList.add(root.join("categoryList").get("text").in(categories));
        }

        predicatesList.addAll(filterByDate(selectedDateFrom, selectedDateTo, root, cb));

        List<Order> orders = new ArrayList<>();

        if (Objects.equals(sortDirection, "ASC")) {
            orders.add(cb.asc(root.get("creationDate")));
        } else {
            orders.add(cb.desc(root.get("creationDate")));
        }

        criteriaQuery.orderBy(orders);
        criteriaQuery.where(predicatesList.toArray(new Predicate[0]));
        TypedQuery<Idea> query = entityManager.createQuery(criteriaQuery);

        int total = query.getResultList().size();

        List<Idea> allIdeas = query.getResultList();

        if (pageable != null) {

            int firstIndex = pageable.getPageNumber() * pageable.getPageSize();

            List<Idea> pagedIdeas = new ArrayList<>();
            for (int i = 0; i < pageable.getPageSize(); i++) {
                if (firstIndex < allIdeas.size()) {
                    pagedIdeas.add(allIdeas.get(firstIndex));
                    firstIndex++;
                }
            }
            IdeaPageDTO ideaPageDTO = new IdeaPageDTO();
            ideaPageDTO.setPagedIdeas(new PageImpl<>(pagedIdeas, pageable, total));
            ideaPageDTO.setTotal(total);
            return ideaPageDTO;
        }

        return new IdeaPageDTO(total, new PageImpl<>(allIdeas, Pageable.unpaged(), total));
    }

    @Override
    public List<Predicate> filterByDate(String selectedDateFrom, String selectedDateTo, Root<Idea> root, CriteriaBuilder cb) {

        List<Predicate> predicatesList = new ArrayList<>();

        if (selectedDateFrom != null && selectedDateTo == null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = simpleDateFormat.parse(selectedDateFrom);
                predicatesList.add(cb.greaterThanOrEqualTo(root.get("creationDate"), fromDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (selectedDateFrom == null && selectedDateTo != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date toDate = simpleDateFormat.parse(selectedDateTo);
                predicatesList.add(cb.lessThanOrEqualTo(root.get("creationDate"), toDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (selectedDateFrom != null && selectedDateTo != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = simpleDateFormat.parse(selectedDateFrom);
                Date toDate = simpleDateFormat.parse(selectedDateTo);
                predicatesList.add(cb.between(root.get("creationDate"), fromDate, toDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return predicatesList;
    }


}
