package com.ace.service;

import com.ace.repository.PositionRepository;
import com.ace.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    @Autowired
    private PositionRepository positionRepository;

    public List<Position> getPositionsByDepartmentId(Integer departmentId) {
        return positionRepository.findByDepartmentId(departmentId);
    }

}
