package com.ace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.ace.entity.Company;
import com.ace.entity.Department;
import com.ace.entity.Group;
import com.ace.repository.CompanyRepository;
import com.ace.repository.DepartmentRepository;
import com.ace.repository.GroupRepository;
import com.ace.service.DepartmentService;

@SpringBootTest
public class DepartmentServiceTest {
	@Mock
	private DepartmentRepository departmentRepository;

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private CompanyRepository companyRepository;

	@InjectMocks
	private DepartmentService departmentService;

	@Test
	void getAllDepartmentsTest() {
		Company company1 = new Company();
		company1.setName("ACE");
		when(companyRepository.save(company1)).thenReturn(company1);

		Department department1 = new Department();
		department1.setName("Banking");
		department1.setCompany(company1);

		Company company2 = new Company();
		company2.setName("DAT");
		when(companyRepository.save(company2)).thenReturn(company2);

		Department department2 = new Department();
		department2.setName("Offshore");
		department2.setCompany(company2);
		List<Department> departmentList = Arrays.asList(department1, department2);
		when(departmentRepository.findAll()).thenReturn(departmentList);

		List<Department> result = departmentService.getAllDepartments();

		assertEquals(2, result.size());
		assertEquals("ACE", result.get(0).getCompany().getName());
		assertEquals("Banking", result.get(0).getName());
		assertEquals("DAT", result.get(1).getCompany().getName());
		assertEquals("Offshore", result.get(1).getName());
		verify(departmentRepository).findAll();
	}

	@Test
	void getDepartmentsByCompanyIdTest() {
		int companyId = 1;
		Company company1 = new Company();
		company1.setId(companyId);
		company1.setName("ACE");
		when(companyRepository.save(company1)).thenReturn(company1);

		Department department1 = new Department();
		department1.setName("Banking");
		department1.setCompany(company1);

		Department department2 = new Department();
		department2.setName("Offshore");
		department2.setCompany(company1);

		List<Department> departmentList = Arrays.asList(department1, department2);
		when(departmentRepository.findByCompanyId(companyId)).thenReturn(departmentList);

		List<Department> result = departmentService.getDepartmentsByCompanyId(companyId);

		assertEquals(2, result.size());
		assertEquals("Banking", result.get(0).getName());
		assertEquals("Offshore", result.get(1).getName());
		verify(departmentRepository).findByCompanyId(companyId);
	}

	@Test
	void findByIdTest() {
		int departmentId = 1;
		Department department = new Department();
		department.setId(departmentId);
		department.setName("Banking");
		when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

		Department result = departmentService.getDepartmentById(departmentId);

		//assertTrue();
		assertEquals("Banking", result.getName());
		verify(departmentRepository).findById(departmentId);
	}

	@Test
	void findByIdTest_notFound() {
		int departmentId = 1;
		when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			departmentService.getDepartmentById(departmentId);
		});

		assertEquals("Department not found", exception.getMessage());
		verify(departmentRepository).findById(departmentId);
	}

	@Test
	void saveDepartmentTest() {
		String name = "Banking";
		Group group = new Group();
		group.setName("group1");
		Department department = new Department();
		when(groupRepository.findByName("group1")).thenReturn(group);
		department.setName(name);
		when(departmentRepository.save(department)).thenReturn(department);

		Department result = departmentService.saveDepartment(department);

		assertNotNull(result);
		verify(departmentRepository).save(department);

	}

	@Test
	void updateDepartmentTest() {
		int departmentId = 1;
		Department oldDepartment = new Department();
		oldDepartment.setId(departmentId);
		oldDepartment.setName("ACE");
		when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(oldDepartment));

		Department newDepartment = new Department();
		newDepartment.setId(departmentId);
		newDepartment.setName("DAT");
		when(departmentRepository.save(newDepartment)).thenReturn(newDepartment);

		Department result = departmentService.updateDepartment(departmentId, newDepartment);
		assertEquals("DAT", result.getName());
		verify(departmentRepository).findById(departmentId);
		verify(departmentRepository).save(newDepartment);

	}
	
	@Test
	void updateDepartment_notFound() {
		int departmentId = 1;
		when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
		
		Department newDepartment = new Department();
		newDepartment.setName("DAT");
		when(departmentRepository.save(newDepartment)).thenReturn(newDepartment);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			departmentService.updateDepartment(departmentId,newDepartment);
		});

		assertEquals("Department not found", exception.getMessage());
		verify(departmentRepository).findById(departmentId);
	}

	@Test
	void deleteDepartmentTest() {
		int departmentId = 1;
		when(departmentRepository.existsById(departmentId)).thenReturn(true);
		departmentService.deleteDepartment(departmentId);
		verify(departmentRepository).deleteById(departmentId);
		verify(departmentRepository).existsById(departmentId);
	}
	
	
	
	@Test
	void deleteDepartmentTest_notFound() {
		int departmentId = 1;
		when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			departmentService.deleteDepartment(departmentId);
		});

		assertEquals("Department not found", exception.getMessage());
		verify(departmentRepository).findById(departmentId);
	}
}
