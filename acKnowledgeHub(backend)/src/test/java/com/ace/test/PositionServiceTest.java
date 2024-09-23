package com.ace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.ace.entity.Position;
import com.ace.repository.PositionRepository;
import com.ace.service.PositionService;

@SpringBootTest
public class PositionServiceTest {

	@Mock
	private PositionRepository positionRepository;
	
	@InjectMocks
	private PositionService positionService;
	
	@Test
 void testFindById() {
		Integer id = 1;
		Position position = new Position();
		position.setId(id);
		position.setName("Manager");
		when(positionRepository.findById(id)).thenReturn(Optional.of(position));
		
		Optional<Position> result = positionService.findById(id);
		
		assertEquals("Manager", result.get().getName());
		verify(positionRepository).findById(id);
	}
	
	@Test
	void TestgetPositionsList() {
		Position position1 = new Position();
		position1.setId(1);
		position1.setName("Manager");
		Position position2  = new Position();
		position2.setId(2);
		position2.setName("HR");
		
		List<Position> positionList = Arrays.asList(position1,position2);
		when(positionRepository.findAll()).thenReturn(positionList);
		
		List<Position> result = positionService.getPositionList();
		assertEquals(2, result.size());
		verify(positionRepository).findAll();
		
	}
	
	@Test
	void testAddPosition() {
		Position position = new Position();
		position.setName("Manager");
		when(positionRepository.save(position)).thenReturn(position);
		
		Position  result = positionService.addPosition(position);
		
		assertNotNull(result);
		assertEquals("Manager", result.getName());
	}
	
	@Test
	void testFindByName() {
		String name = "Human Resource(main)";
		Position position = new Position();
		position.setId(1);
		position.setName(name);
		when(positionRepository.findByHRName(name)).thenReturn(position);
		
		Position result = positionService.findByName(name);
		
		assertEquals("Human Resource(main)", result.getName());
		verify(positionRepository).findByHRName(name);
	}
	
}
