package com.ace.utility.core.coreRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.List;
import java.util.function.Function;

public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?>entityInformation,EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public <DTO> List<DTO> search(Function<CriteriaBuilder, CriteriaQuery<DTO>> queryFunction) {
        var cq = queryFunction.apply(entityManager.getCriteriaBuilder());
        var query = entityManager.createQuery(cq);
        return query.getResultList();
    }

}
