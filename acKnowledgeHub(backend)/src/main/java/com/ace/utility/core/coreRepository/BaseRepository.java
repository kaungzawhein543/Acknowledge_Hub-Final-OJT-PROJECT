package com.ace.utility.core.coreRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;

public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    <DTO> List<DTO> search(Function<CriteriaBuilder, CriteriaQuery<DTO>> queryFunction);
}
