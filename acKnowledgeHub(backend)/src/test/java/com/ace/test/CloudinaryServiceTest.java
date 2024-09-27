package com.ace.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.ace.service.CloudinaryService;
import com.cloudinary.Api;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceTest {
	@Mock
	Cloudinary mockCloudinary;

	@Mock
	Uploader mockUploader;

	@Mock
	Url mockUrl;

	@Mock
	private CloudinaryService cloudinaryService;

	private ApiResponse mockApiResponse;

	private MultipartFile mockFile;

	@BeforeEach
	public void setup() {
		mockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", "test content".getBytes());
		lenient().when(mockCloudinary.uploader()).thenReturn(mockUploader);

		// Using a spy on the abstract class
		cloudinaryService = spy(new CloudinaryService(mockCloudinary) {
			@Override
			protected InputStream openStream(String urlString) throws IOException {
				return new ByteArrayInputStream("PDF content".getBytes());
			}
		});
	}

	@Test
	public void testUploadFile_Success() throws IOException {
		// Prepare the expected result
		Map<String, Object> uploadResult = new HashMap<>();
		uploadResult.put("url", "http://cloudinary.com/test.pdf");

		when(mockUploader.upload(any(byte[].class), any())).thenReturn(uploadResult);

		CompletableFuture<Map<String, Object>> result = cloudinaryService.uploadFile(mockFile, "test");

		// Verify the result
		assertEquals(uploadResult, result.join());
		verify(mockUploader).upload(any(byte[].class), any());
	}

	@Test
	public void testDeleteFile_Success() throws IOException {
		cloudinaryService.deleteFile("testPublicId");

		verify(mockUploader).destroy("AcknowledgeHub/testPublicId", ObjectUtils.emptyMap());
	}

	@Test
	public void testGetFile_Success() throws Exception {
		// Prepare the expected result
		Map<String, Object> mockResult = new HashMap<>();
		mockResult.put("public_id", "testPublicId");

		// Mock the ApiResponse
		ApiResponse mockApiResponse = mock(ApiResponse.class);
		when(mockApiResponse.get("public_id")).thenReturn("testPublicId");

		Api mockApi = mock(Api.class);
		when(mockCloudinary.api()).thenReturn(mockApi);
		when(mockApi.resource(anyString(), any())).thenReturn(mockApiResponse);

		// Call the method
		Map<String, Object> result = cloudinaryService.getFile("testPublicId");

		assertEquals("testPublicId", result.get("public_id"));
		verify(mockApi).resource("AcknowledgeHub/testPublicId", ObjectUtils.emptyMap());
	}

	@Test
	public void testGetFile_ExceptionHandled() throws Exception {
		String publicId = "testPublicId";

		Api mockApi = mock(Api.class);

		when(mockCloudinary.api()).thenReturn(mockApi);

		when(mockApi.resource("AcknowledgeHub/testPublicId", ObjectUtils.emptyMap()))
				.thenThrow(new RuntimeException("Mocked exception"));

		Map<String, Object> result = cloudinaryService.getFile(publicId);

		// Assert
		assertNull(result, "Expected result to be null when an exception is thrown.");
	}

	@Test
	public void testDownloadPdf_Success() throws IOException, InterruptedException {
		// Given
		String publicId = "somePublicId";
		String expectedUrl = "http://res.cloudinary.com/demo/image/upload/v1/" + publicId + ".pdf";
		byte[] expectedContent = "PDF content".getBytes();

		// Mock the Cloudinary URL generation
		when(mockCloudinary.url()).thenReturn(mockUrl);
		when(mockUrl.generate(publicId + ".pdf")).thenReturn(expectedUrl);

		// Mock the InputStream for downloading the content
		InputStream mockInputStream = new ByteArrayInputStream(expectedContent);

		// Override the openStream method to return the mocked InputStream
		cloudinaryService = new CloudinaryService(mockCloudinary) {
			@Override
			protected InputStream openStream(String urlString) throws IOException {
				// Ensure the URL passed is what we expect
				assertEquals(expectedUrl, urlString);
				return mockInputStream;
			}
		};

		// When
		byte[] result = cloudinaryService.downloadPdf(publicId);

		// Then
		assertNotNull(result);
		assertArrayEquals(expectedContent, result);
	}

	@Test
	void testFindLatestFileByBaseName_WithVersionedFiles() throws Exception {
		String baseName = "testFile";
		String publicId1 = "AcknowledgeHub/images/testFile_V1.jpg";
		String publicId2 = "AcknowledgeHub/images/testFile_V2.jpg";

		Map<String, Object> mockResource1 = new HashMap<>();
		mockResource1.put("public_id", publicId1);
		Map<String, Object> mockResource2 = new HashMap<>();
		mockResource2.put("public_id", publicId2);

		List<Map<String, Object>> resources = List.of(mockResource1, mockResource2);

		ApiResponse mockApiResponse = mock(ApiResponse.class);
		when(mockApiResponse.get("resources")).thenReturn(resources);

		Api mockApi = mock(Api.class);
		when(mockCloudinary.api()).thenReturn(mockApi);
		when(mockApi.resources(anyMap())).thenReturn(mockApiResponse);

		// Call the method under test
		String result = cloudinaryService.findLatestFileByBaseName(baseName);

		assertEquals(publicId2, result, "Expected the latest versioned file to be V2");
		verify(mockCloudinary.api()).resources(anyMap());
	}

	private int invokeExtractVersionNumber(String fileName) throws Exception {
		Method method = CloudinaryService.class.getDeclaredMethod("extractVersionNumber", String.class);
		method.setAccessible(true); // Make the private method accessible
		return (int) method.invoke(cloudinaryService, fileName);
	}

	@Test
	void testExtractVersionNumber_ValidVersion() throws Exception {
		assertEquals(1, invokeExtractVersionNumber("testFile_V1.jpg"));
		assertEquals(2, invokeExtractVersionNumber("testFile_V2.jpg"));
		assertEquals(10, invokeExtractVersionNumber("testFile_V10.png"));
	}

	@Test
	void testExtractVersionNumber_NoVersion() throws Exception {
		assertEquals(0, invokeExtractVersionNumber("testFile.jpg"));
		assertEquals(0, invokeExtractVersionNumber("testFile.png"));
	}

	@Test
	void testExtractVersionNumber_MalformedVersion() throws Exception {
		assertEquals(0, invokeExtractVersionNumber("testFile_Vx.jpg"));
		assertEquals(0, invokeExtractVersionNumber("testFile_V.jpg"));
	}
}
