package com.ace.repository;


import com.ace.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position,Integer> {
    Optional<Position> findByName(String name);
    @Query("SELECT DISTINCT p FROM Position p " +
            "JOIN Staff s ON p.id = s.position.id " +
            "JOIN Department d ON s.department.id = d.id " +
            "WHERE d.id = :departmentId")
    List<Position> findByDepartmentId(@Param("departmentId") Integer departmentId);

    @Query("select p from Position p where p.name= ?1")
    Position findByHRName(String name);
}
