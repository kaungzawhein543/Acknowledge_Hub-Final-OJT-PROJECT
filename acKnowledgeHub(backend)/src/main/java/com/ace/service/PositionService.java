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
    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public Optional<Position> findById(int id ){
        return positionRepository.findById(id);
    }

//    public List<Position> getPositionsByDepartmentId(Integer departmentId) {
//        return positionRepository.findByDepartmentId(departmentId);
//    }

    public List<Position> getPositionList(){
        return positionRepository.findAllPositionsOrderByName();
    }

    public Position addPosition(Position position){
        return positionRepository.save(position);
    }

    public Position findByName(String name){
        return positionRepository.findByHRName(name);
    }

    public void updatePosition(Integer id, Position position){
         Optional<Position> existingPosition = positionRepository.findById(id);
         if(existingPosition.isPresent()){
             existingPosition.get().setName(position.getName());

         }
    }


}
