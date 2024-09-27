package com.ace.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.*;

import com.ace.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ace.entity.Company;
import com.ace.entity.Department;
import com.ace.entity.Group;
import com.ace.entity.Position;
import com.ace.entity.Staff;
import com.ace.enums.Role;
import com.ace.repository.GroupRepository;
import com.ace.repository.StaffRepository;
import com.ace.service.StaffService;

public class StaffServiceTest {
	@Mock
	private StaffRepository staffRepository;

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private StaffService staffService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetStaffs() {
		// Given
		Staff staff = new Staff();
		staff.setId(1); // Ensure the Staff has an ID
		staff.setCompanyStaffId("123");
		staff.setName("John Doe");
		staff.setEmail("john.doe@example.com");
		staff.setPassword("password"); // Assuming the password is needed for this example
		staff.setRole(Role.USER); // Assuming Role is an enum and USER is a valid value
		staff.setPosition(new Position()); // Create a Position object if necessary
		staff.setDepartment(new Department()); // Create a Department object if necessary
		staff.setCompany(new Company()); // Create a Company object if necessary

		List<Staff> staffList = Arrays.asList(staff);
		Page<Staff> staffPage = new PageImpl<>(staffList);

		// Mock the repository response
		when(staffRepository.findAll(any(Pageable.class))).thenReturn(staffPage);

		// Mock the mapping to return a proper StaffDTO instance
		when(modelMapper.map(any(Staff.class), eq(StaffDTO.class)))
				.thenReturn(new StaffDTO(staff.getId(), staff.getCompanyStaffId(), staff.getName(), staff.getEmail(),
						staff.getPosition() != null ? staff.getPosition().getName() : "", staff.getDepartment(),
						staff.getCompany(), staff.getRole()));

		// When
		Page<StaffDTO> result = staffService.getStaffs(0, 1);

		// Then
		assertEquals(1, result.getContent().size());
		assertEquals("123", result.getContent().get(0).getStaffId());
		verify(staffRepository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	void testAuthenticateSuccess() {
		// Given
		String staffId = "staff123";
		String password = "password";
		Staff staff = new Staff();
		staff.setCompanyStaffId(staffId);
		staff.setPassword(passwordEncoder.encode(password));

		when(staffRepository.findByCompanyStaffId(staffId)).thenReturn(staff);
		when(passwordEncoder.matches(password, staff.getPassword())).thenReturn(true);

		// When
		Staff result = staffService.authenticate(staffId, password);

		// Then
		assertNotNull(result);
		assertEquals(staffId, result.getCompanyStaffId());
		verify(staffRepository, times(1)).findByCompanyStaffId(staffId);
	}

	@Test
	void testAuthenticateFailure() {
		// Given
		String staffId = "test123";
		String wrongPassword = "wrongpassword";
		Staff staff = new Staff();
		staff.setCompanyStaffId(staffId);
		staff.setPassword("encodedPassword");

		when(staffRepository.findByCompanyStaffId(staffId)).thenReturn(staff);
		when(passwordEncoder.matches(wrongPassword, staff.getPassword())).thenReturn(false);

		// When
		Staff result = staffService.authenticate(staffId, wrongPassword);

		// Then
		assertNull(result);
		verify(staffRepository, times(1)).findByCompanyStaffId(staffId);
		verify(passwordEncoder, times(1)).matches(wrongPassword, staff.getPassword());
	}

	@Test
	void testChangePasswordSuccess() {
		// Given
		String staffId = "staff123";
		String oldPassword = "oldPassword";
		String newPassword = "newPassword";

		Staff staff = new Staff();
		staff.setCompanyStaffId(staffId);
		staff.setPassword(passwordEncoder.encode(oldPassword));

		when(staffRepository.findByCompanyStaffId(staffId)).thenReturn(staff);
		when(passwordEncoder.matches(oldPassword, staff.getPassword())).thenReturn(true);

		// When
		boolean result = staffService.changePassword(staffId, oldPassword, newPassword);

		// Then
		assertTrue(result);
		assertNotEquals(oldPassword, staff.getPassword()); // ensure password is updated
		verify(staffRepository, times(1)).save(staff);
	}

	@Test
	void testChangePasswordFailure() {
		String staffId = "test123";
		String wrongOldPassword = "wrongOldPassword";
		String newPassword = "newPassword";

		Staff staff = new Staff();
		staff.setCompanyStaffId(staffId);
		staff.setPassword(passwordEncoder.encode("correctOldPassword"));

		when(staffRepository.findByCompanyStaffId(staffId)).thenReturn(staff);
		when(passwordEncoder.matches(wrongOldPassword, staff.getPassword())).thenReturn(false);

		boolean result = staffService.changePassword(staffId, wrongOldPassword, newPassword);

		assertFalse(result);
		verify(staffRepository, times(1)).findByCompanyStaffId(staffId);
		verify(passwordEncoder, times(1)).matches(wrongOldPassword, staff.getPassword());
		verify(staffRepository, never()).save(any(Staff.class));
	}

	@Test
	void testFindById() {
		// Given
		Integer id = 1;
		Staff staff = new Staff();
		staff.setId(id);
		when(staffRepository.findById(id)).thenReturn(Optional.of(staff));

		// When
		Staff result = staffService.findById(id);

		// Then
		assertNotNull(result);
		assertEquals(id, result.getId());
		verify(staffRepository, times(1)).findById(id);
	}

	@Test
	void testFindById_NotFound() {
		when(staffRepository.findByCompanyStaffId("nonExistentStaffId")).thenReturn(null);

		Staff result = staffService.findById("nonExistentStaffId");

		assertNotNull(result);
		assertNull(result.getCompanyStaffId());
		verify(staffRepository, times(1)).findByCompanyStaffId("nonExistentStaffId");
	}

	@Test
	void testGetActiveStaffList() {
		// Given
		ActiveStaffResponseDTO activeStaff = new ActiveStaffResponseDTO(1, // id
				"staff123", // companyStaffId
				"John Doe", // name
				"john@example.com", // email
				Role.USER, // role (ensure you have an instance of Role)
				"Developer", // position
				"IT", // department
				"Example Corp" // company
		);
		List<ActiveStaffResponseDTO> activeStaffList = Arrays.asList(activeStaff);
		when(staffRepository.getActiveStaffList()).thenReturn(activeStaffList);

		// When
		List<ActiveStaffResponseDTO> result = staffService.getActiveStaffList();

		// Then
		assertEquals(1, result.size());
		verify(staffRepository, times(1)).getActiveStaffList();
	}

	@Test
	void testGetStaffByStaffId() {
		// Given
		String staffId = "staff123";
		Staff staff = new Staff();
		staff.setCompanyStaffId(staffId);
		when(staffRepository.findByCompanyStaffId(staffId)).thenReturn(staff);

		// When
		Staff result = staffService.getStaffByStaffId(staffId);

		// Then
		assertNotNull(result);
		assertEquals(staffId, result.getCompanyStaffId());
		verify(staffRepository, times(1)).findByCompanyStaffId(staffId);
	}

	@Test
	void testFindByEmail() {
		// Given
		String email = "john.doe@example.com";
		Staff staff = new Staff();
		staff.setEmail(email);
		when(staffRepository.findByEmail(email)).thenReturn(staff);

		// When
		Staff result = staffService.findByEmail(email);

		// Then
		assertNotNull(result);
		assertEquals(email, result.getEmail());
		verify(staffRepository, times(1)).findByEmail(email);
	}

	@Test
	void testUpdatePassword() {
		// Given
		PasswordResponseDTO dto = new PasswordResponseDTO();
		dto.setEmail("john.doe@example.com");
		dto.setPassword("newPassword");

		Staff staff = new Staff();
		staff.setEmail(dto.getEmail());
		staff.setPassword("oldPassword");

		when(staffRepository.findByEmail(dto.getEmail())).thenReturn(staff);

		// When
		staffService.updatePassword(dto);

		// Then
		assertNotEquals("oldPassword", staff.getPassword());
		verify(staffRepository, times(1)).save(staff);
	}

	@Test
	void testAddStaff() {
		// Given
		Staff staff = new Staff();
		staff.setCompany(new Company());
		staff.setDepartment(new Department());

		when(groupRepository.findByName(anyString())).thenReturn(new Group());

		// When
		staffService.addStaff(staff);

		// Then
		verify(staffRepository, times(1)).save(staff);
		verify(groupRepository, times(1)).save(any(Group.class));
	}

	@Test
	void testFindByChatId() {
		// Given
		String chatId = "chat123";
		Staff staff = new Staff();
		staff.setChatId(chatId);
		when(staffRepository.findByChatId(chatId)).thenReturn(Optional.of(staff));

		// When
		Optional<Staff> result = staffService.findByChatId(chatId);

		// Then
		assertTrue(result.isPresent());
		assertEquals(chatId, result.get().getChatId());
		verify(staffRepository, times(1)).findByChatId(chatId);
	}
	@Test
	void testSearchStaffs() {
		// Given
		String searchTerm = "John";
		Staff staff = new Staff();
		staff.setId(1);
		staff.setCompanyStaffId("123");
		staff.setName("John Doe");
		List<Staff> staffList = Arrays.asList(staff);
		Page<Staff> staffPage = new PageImpl<>(staffList);

		when(staffRepository.searchByTerm(eq(searchTerm), any(Pageable.class))).thenReturn(staffPage);
		when(modelMapper.map(any(Staff.class), eq(StaffDTO.class)))
				.thenReturn(new StaffDTO(staff.getId(), staff.getCompanyStaffId(), staff.getName(), staff.getEmail(),
						staff.getPosition() != null ? staff.getPosition().getName() : "", staff.getDepartment(),
						staff.getCompany(), staff.getRole()));

		// When
		Page<StaffDTO> result = staffService.searchStaffs(searchTerm, 0, 1);

		// Then
		assertEquals(1, result.getContent().size());
		assertEquals("123", result.getContent().get(0).getStaffId());
		verify(staffRepository, times(1)).searchByTerm(eq(searchTerm), any(Pageable.class));
	}
	@Test
	void testLoadUserByUsername() {
		Staff staff = new Staff();
		staff.setCompanyStaffId("123");
		staff.setPassword("encodedPassword");

		when(staffRepository.findByCompanyStaffId(anyString())).thenReturn(staff);

		var userDetails = staffService.loadUserByUsername("123");

		assertNotNull(userDetails);
		assertEquals("123", userDetails.getUsername());
		verify(staffRepository, times(1)).findByCompanyStaffId(anyString());
	}
	@Test
	void testGetNotedStaffList() {
		NotedResponseDTO notedResponseDTO = new NotedResponseDTO(
				"staffId123",          // staffId
				"John Doe",            // name
				"Engineering",         // departmentName
				"TechCorp",            // companyName
				"Developer",           // positionName
				new Timestamp(System.currentTimeMillis()), // notedAt
				"john.doe@example.com" // email
		);
		notedResponseDTO.setStaffId("1");
		List<NotedResponseDTO> notedResponseList = List.of(notedResponseDTO);

		when(staffRepository.getNotedStaffByAnnouncement(anyInt())).thenReturn(notedResponseList);

		List<NotedResponseDTO> result = staffService.getNotedStaffList(1);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("1", result.get(0).getStaffId());
		verify(staffRepository, times(1)).getNotedStaffByAnnouncement(anyInt());
	}
	@Test
	void testGetUnNotedStaffList() {
		UnNotedResponseDTO unNotedResponseDTO = new UnNotedResponseDTO(
				"staffId123",          // staffId
				"John Doe",            // name
				"Engineering",         // departmentName
				"TechCorp",            // companyName
				"Developer",           // positionName
				"john.doe@example.com" // email
		);
		unNotedResponseDTO.setStaffId("1");
		List<UnNotedResponseDTO> unNotedResponseList = List.of(unNotedResponseDTO);

		when(staffRepository.getUnNotedStaffByAnnouncementWithEach(anyInt())).thenReturn(unNotedResponseList);

		List<UnNotedResponseDTO> result = staffService.getUnNotedStaffList(1);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("1", result.get(0).getStaffId());
		verify(staffRepository, times(1)).getUnNotedStaffByAnnouncementWithEach(anyInt());
	}

	@Test
	void testGetUnNotedStaffListWithGroup() {
		// Create the UnNotedResponseDTO instance with necessary details
		UnNotedResponseDTO unNotedResponseDTO = new UnNotedResponseDTO(
				"staffId123",          // staffId
				"John Doe",            // name
				"Engineering",         // departmentName
				"TechCorp",            // companyName
				"Developer",           // positionName
				"john.doe@example.com" // email
		);

		// Modify the staffId using the setter if needed
		unNotedResponseDTO.setStaffId("1");

		// Mock repository response
		List<UnNotedResponseDTO> unNotedResponseList = List.of(unNotedResponseDTO);
		when(staffRepository.getUnNotedStaffByAnnouncementWithGroup(anyInt())).thenReturn(unNotedResponseList);

		// Invoke the service method
		List<UnNotedResponseDTO> result = staffService.getUnNotedStaffListWithGroup(1);

		// Assertions
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("1", result.get(0).getStaffId());
		assertEquals("John Doe", result.get(0).getName());
		assertEquals("Engineering", result.get(0).getDepartmentName());
		assertEquals("TechCorp", result.get(0).getCompanyName());
		assertEquals("Developer", result.get(0).getPositionName());
		assertEquals("john.doe@example.com", result.get(0).getEmail());

		// Verify interaction with the repository
		verify(staffRepository, times(1)).getUnNotedStaffByAnnouncementWithGroup(anyInt());
	}

	@Test
	void testGetHRMainStaff() {
		// Create and set the Position object
		Position hrManagerPosition = new Position();
		hrManagerPosition.setId(1);
		hrManagerPosition.setName("HR Manager");

		Staff hrMainStaff = new Staff();
		hrMainStaff.setId(1);
		hrMainStaff.setName("John Doe");
		hrMainStaff.setPosition(hrManagerPosition);

		when(staffRepository.findByPosition("HR Manager")).thenReturn(hrMainStaff);

		Staff result = staffService.getHRMainStaff("HR Manager");

		assertNotNull(result);
		assertEquals(1, result.getId());
		assertEquals("John Doe", result.getName());
		assertEquals(hrManagerPosition, result.getPosition()); // Compare the Position object

		verify(staffRepository, times(1)).findByPosition("HR Manager");
	}

	@Test
	void testSave() {
		Staff staff = new Staff();
		staff.setId(1);
		staff.setName("John Doe");
		staff.setEmail("john.doe@example.com");

		staffService.save(staff);

		verify(staffRepository, times(1)).save(staff);
	}
	@Test
	void testGetStaffList() {
		StaffResponseDTO staffResponseDTO = new StaffResponseDTO(
				1,                          // id
				"staffId2",                 // companyStaffId
				"Alice Johnson",            // name
				"alice.johnson@example.com",// email
				Role.USER,              // role
				"Engineer",                 // position
				"Engineering",              // department
				"TechCorp",                 // company
				"Active"                    // status
		);

		List<StaffResponseDTO> staffResponseList = List.of(staffResponseDTO);

		when(staffRepository.getStaffList()).thenReturn(staffResponseList);

		List<StaffResponseDTO> result = staffService.getStaffList();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("staffId2", result.get(0).getCompanyStaffId());
		verify(staffRepository, times(1)).getStaffList();
	}

	@Test
	void testGetHRStaffList() {
		StaffResponseDTO hrStaff = new StaffResponseDTO(
				1,                          // id
				"staffId1",                 // companyStaffId
				"Jane Smith",               // name
				"jane.smith@example.com",   // email
				Role.USER,                   // role
				"Manager",                  // position
				"HR",                       // department
				"TechCorp",                 // company
				"Active"                    // status
		);

		List<StaffResponseDTO> hrStaffList = List.of(hrStaff);

		when(staffRepository.getHRStaffList()).thenReturn(hrStaffList);

		List<StaffResponseDTO> result = staffService.getHRStaffList();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("staffId1", result.get(0).getCompanyStaffId());
		verify(staffRepository, times(1)).getHRStaffList();
	}

	@Test
	void testSaveChatId() {
		Staff staff = new Staff();
		staff.setEmail("john.doe@example.com");
		staff.setChatId(null);

		when(staffRepository.findByEmail("john.doe@example.com")).thenReturn(staff);

		staffService.saveChatId("newChatId123", "john.doe@example.com");

		assertEquals("newChatId123", staff.getChatId());
		verify(staffRepository, times(1)).findByEmail("john.doe@example.com");
		verify(staffRepository, times(1)).save(staff);
	}

}
