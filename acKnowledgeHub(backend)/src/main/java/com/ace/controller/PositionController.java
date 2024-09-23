package com.ace.controller;

import com.ace.entity.Position;
import com.ace.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Position> addPosition(@RequestBody Position position){
        Position responsePosition =  positionService.addPosition(position);
        return ResponseEntity.ok(responsePosition);
    }
}
