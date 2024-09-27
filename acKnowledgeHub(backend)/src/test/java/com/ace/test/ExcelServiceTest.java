package com.ace.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.ace.entity.Staff;
import com.ace.repository.CompanyRepository;
import com.ace.repository.DepartmentRepository;
import com.ace.repository.GroupRepository;
import com.ace.repository.PositionRepository;
import com.ace.repository.StaffRepository;
import com.ace.service.EmailService;
import com.ace.service.ExcelService;

@ExtendWith(MockitoExtension.class)
public class ExcelServiceTest {
	@Mock
	private StaffRepository staffRepository;

	@Mock
	private PositionRepository positionRepository;

	@Mock
	private CompanyRepository companyRepository;

	@Mock
	private DepartmentRepository departmentRepository;

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private JavaMailSender javaMailSender;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private ExcelService excelService;

	@Test
	void processExcelFile_emptyFile() throws Exception {
		Workbook workbook = new XSSFWorkbook();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] emptyExcelFile = bos.toByteArray();
		bos.close();

		MultipartFile multipartFile = new MockMultipartFile("file", "empty.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", emptyExcelFile);

		String result = excelService.processExcelFile(multipartFile);

		assertEquals("Error processing file.", result);
	}

	@Test
	void processExcelFile_errorDuringProcessing() throws Exception {
		Workbook workbook = createWorkbookWithValidData();
		byte[] excelBytes = writeWorkbookToByteArray(workbook);

		MultipartFile multipartFile = new MockMultipartFile("file", "valid_excel.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

		when(staffRepository.save(any(Staff.class))).thenThrow(new RuntimeException("Database error"));

		String result = excelService.processExcelFile(multipartFile);

		assertEquals("Error processing file.", result);
	}

	@Test
	void processExcelFile_success() throws Exception {
		Workbook workbook = createWorkbookWithValidData();
		byte[] excelBytes = writeWorkbookToByteArray(workbook);

		MultipartFile multipartFile = new MockMultipartFile("file", "test.xlsx",
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelBytes);

		String result = excelService.processExcelFile(multipartFile);

		assertEquals("File processed and data saved successfully.", result);
	}

	private Workbook createWorkbookWithValidData() {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Test Sheet");
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("Staff ID");
		headerRow.createCell(1).setCellValue("Staff Name");
		headerRow.createCell(2).setCellValue("Staff Email");
		headerRow.createCell(3).setCellValue("Position");
		headerRow.createCell(4).setCellValue("Company");
		headerRow.createCell(5).setCellValue("Department");

		Row dataRow = sheet.createRow(1);
		dataRow.createCell(0).setCellValue("ST001");
		dataRow.createCell(1).setCellValue("John Doe");
		dataRow.createCell(2).setCellValue("john.doe@example.com");
		dataRow.createCell(3).setCellValue("Manager");
		dataRow.createCell(4).setCellValue("Ace Corp");
		dataRow.createCell(5).setCellValue("HR");

		return workbook;
	}

	private byte[] writeWorkbookToByteArray(Workbook workbook) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		bos.close();
		return excelBytes;
	}
}
