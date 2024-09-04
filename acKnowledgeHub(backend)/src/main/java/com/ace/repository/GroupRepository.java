package com.ace.repository;

import com.ace.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Query("SELECT g FROM Group g")
    List<Group> findAllGroups();

    @Query("SELECT g FROM Group g WHERE g.id IN :ids")
    List<Group> findGroupsByIds(List<Integer> ids);
}
