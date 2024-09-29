package com.ace.controller;

import com.ace.entity.Company;
import com.ace.entity.Position;
import com.ace.service.PositionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/position")
public class PositionController {
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/sys/list")
    public List<Position> getAllPosition(){
        return positionService.getPositionList();
    }

    @PostMapping("/sys/addPosition")
    public ResponseEntity<String> addPosition(@RequestBody Position position){
        Position existingPosition = positionService.findByName(position.getName());
        if (existingPosition == null) {
            positionService.addPosition(position);
            return ResponseEntity.ok("Adding position is successful.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Position already exists.");
        }
    }
    @GetMapping("/all/{id}")
    public Optional<Position> getPositionById(@PathVariable("id")Integer id){
        return positionService.findById(id);
    }

    @PutMapping("/all/{id}")
    public ResponseEntity<String> updatePosition(@PathVariable("id")Integer id, @RequestBody Position position){
        Position existingPosition = positionService.findByName(position.getName());
        if (existingPosition == null) {
            positionService.updatePosition(id,position);
            return ResponseEntity.ok("Updating position is successful.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Position already exists.");
        }
    }

}
