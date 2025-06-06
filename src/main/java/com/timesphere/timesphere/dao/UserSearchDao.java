package com.timesphere.timesphere.dao;

import com.timesphere.timesphere.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserSearchDao {

    private final EntityManager em;

    public List<User> findAllBySimpleQuery(
            String email,
            String firstname,
            String lastname,
            String username
    ) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        // select * from user
        // root <=> user
        Root<User> root = criteriaQuery.from(User.class);

        // prepare WHERE clause
        // WHERE firstname like '%admin%'
        Predicate firstnamePredicate = criteriaBuilder
                .like(root.get("firstname"), "%" + firstname + "%");
        Predicate lastnamePredicate = criteriaBuilder
                .like(root.get("lastname"), "%" + lastname + "%");
        Predicate emailPredicate = criteriaBuilder
                .like(root.get("email"), "%" + email + "%");
        Predicate usernamePredicate = criteriaBuilder
                .like(root.get("username"), "%" + username + "%");

        Predicate firstnameOrLastnamePredicate = criteriaBuilder.or(
                firstnamePredicate,
                lastnamePredicate
        );

        // final query -> select * from user where firstname like '%admin%'
        // or lastname like '%admin%' and email like '%email%'
        var andEmailPredicate = criteriaBuilder.and(firstnameOrLastnamePredicate, emailPredicate);
        criteriaQuery.where(andEmailPredicate);
        TypedQuery<User> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public List<User> findAllByCriteria(
            SearchRequest request
    ) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        List<Predicate> predicates = new ArrayList<>();

        // select from user
        Root<User> root = criteriaQuery.from(User.class);
        if (request.getFirstname() != null){
            Predicate firstnamePredicate = criteriaBuilder
                    .like(root.get("firstname"), "%" + request.getFirstname() +"%");
            predicates.add(firstnamePredicate);
        }
        if (request.getLastname() != null){
            Predicate lastnamePredicate = criteriaBuilder
                    .like(root.get("lastname"), "%" + request.getLastname() +"%");
            predicates.add(lastnamePredicate);
        }
        if (request.getEmail() != null){
            Predicate emailPredicate = criteriaBuilder
                    .like(root.get("email"), "%" + request.getEmail() +"%");
            predicates.add(emailPredicate);
        }

        criteriaQuery.where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );

        TypedQuery<User> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
