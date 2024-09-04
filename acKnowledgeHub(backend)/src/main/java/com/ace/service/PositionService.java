package com.ace.service;

import com.ace.repository.PositionRepository;
import com.ace.entity.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class PositionService {
    @Autowired
    private PositionRepository positionRepository;

    public Optional<Position> findById(int id ){
        return positionRepository.findById(id);
    }

    public List<Position> getPositionsByDepartmentId(Integer departmentId) {
        return positionRepository.findByDepartmentId(departmentId);
    }

    public List<Position> getPositionList(){
        return positionRepository.findAll();
    }

    public Position addPosition(Position position){
        return positionRepository.save(position);
    }
}
