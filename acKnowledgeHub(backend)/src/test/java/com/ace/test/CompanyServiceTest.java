package com.ace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.ace.entity.Category;
import com.ace.entity.Company;
import com.ace.entity.Group;
import com.ace.repository.CompanyRepository;
import com.ace.repository.GroupRepository;
import com.ace.service.CompanyService;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class CompanyServiceTest {

	@Mock
	private CompanyRepository companyRepository;
	
	@Mock
    private GroupRepository groupRepository;

	@InjectMocks
	private CompanyService companyService;

	@Test
	void getAllCompaniesTest() {
		Company com1 = new Company();
		com1.setId(1);
		com1.setName("ACE");

		Company com2 = new Company();
		com2.setId(2);
		com2.setName("DAT");

		List<Company> companyList = Arrays.asList(com1, com2);
		when(companyRepository.findAll()).thenReturn(companyList);

		List<Company> result = companyService.getAllCompanies();
		assertEquals(2, result.size());
		assertEquals("ACE", result.get(0).getName());
		assertEquals("DAT", result.get(1).getName());
		verify(companyRepository).findAll();
	}

	@Test
	void getCompanyByIdTest() {
		int companyId = 1;
		Company com1 = new Company();
		com1.setId(companyId);
		com1.setName("ACE");

		when(companyRepository.findById(companyId)).thenReturn(Optional.of(com1));

		Company result = companyService.getCompanyById(companyId);

		assertTrue(result!= null);
		assertEquals("ACE", result.getName());
		verify(companyRepository).findById(companyId);

	}
	
	@Test
    public void testGetCompanyById_CompanyNotFound() {
        int companyId = 1;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.getCompanyById(companyId);
        });

        assertEquals("Company not found", exception.getMessage());
    }
	
	@Test
	void saveCompanyTest() {
		Company company = new Company();
		company.setName("ACE");
		when(groupRepository.findByName(company.getName())).thenReturn(null);
		Group group = new Group();
		group.setName(company.getName());
		when(companyRepository.save(company)).thenReturn(company);
		Company result = companyService.saveCompany(company);
		
		assertNotNull(result);
		assertEquals("ACE", company.getName());
		verify(groupRepository).findByName(company.getName());
        verify(groupRepository).save(any(Group.class)); 
        verify(companyRepository).save(company);
	}
	
	@Test
	void updateCompanyTest() {
		int companyId = 1;
		Company company = new Company();
		company.setId(companyId);
		company.setName("ACE");
		when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
		
		Company company2 = new Company();
		company2.setId(companyId);
		company2.setName("DAT");
		
		when(companyRepository.save(company2)).thenReturn(company2);
		
		Company result = companyService.updateCompany(companyId, company2);
		assertEquals("DAT", result.getName());
		verify(companyRepository).findById(companyId);
		verify(companyRepository).save(company);
	}
	
	 @Test
	    void updateCompany_CompanyNotFound() {
	        int companyId = 1;
	        Company updatedCompany = new Company();
	        updatedCompany.setName("New Name");

	        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

	        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	            companyService.updateCompany(companyId, updatedCompany);
	        });

	        assertEquals("Company not found", exception.getMessage());
	        verify(companyRepository).findById(companyId);
	        verify(companyRepository, never()).save(any(Company.class));
	    }
	 
	 @Test
	    void deleteCompany_shouldDeleteCompanyWhenExists() {
	        int companyId = 1;

	        when(companyRepository.existsById(companyId)).thenReturn(true);

	        companyService.deleteCompany(companyId);

	        verify(companyRepository).existsById(companyId);
	        verify(companyRepository).deleteById(companyId);
	    }

	    @Test
	    void deleteCompany_shouldThrowExceptionWhenCompanyNotFound() {
	        int companyId = 1;

	        when(companyRepository.existsById(companyId)).thenReturn(false);

	        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	            companyService.deleteCompany(companyId);
	        });

	        assertEquals("Company not found", exception.getMessage());

	        verify(companyRepository).existsById(companyId);
	        verify(companyRepository, never()).deleteById(companyId);
	    }
}
