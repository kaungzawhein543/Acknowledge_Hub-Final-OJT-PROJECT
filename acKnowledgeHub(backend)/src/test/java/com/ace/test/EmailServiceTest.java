package com.ace.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;

import com.ace.service.EmailService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
	@Mock
	private JavaMailSender javaMailSender;

	@InjectMocks
	private EmailService emailService;

	@Test
	void testSendOTPEmail() {
		String toEmail = "test@example.com";
		String subject = "OTP Subject";
		String messageBody = "Your OTP is 123456";

		emailService.sendOTPEmail(toEmail, subject, messageBody);

		verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
	}

	@Test
	void testSendFileEmail() throws MessagingException, UnsupportedEncodingException {
		String toEmail = "test@example.com";
		String subject = "File Subject";
		MultipartFile file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn("test-file.txt");

		MimeMessage mimeMessage = new MimeMessage((Session) null);
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendFileEmail(toEmail, subject, file, "file-name");

		verify(javaMailSender).send(mimeMessage);

		// Assert email details
		assertEquals(toEmail, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
		assertEquals(subject, mimeMessage.getSubject());
	}

	@Test
	void testSendTelegramChannelInvitation() throws Exception {
		String recipientEmail = "test@example.com";
		String TELEGRAM_CHANNEL_LINK = "https://t.me/AcKnowledgeHubBot";

		// Mock the MimeMessage
		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		// Act
		emailService.sendTelegramChannelInvitation(recipientEmail);

		// Assert
		verify(mimeMessage).setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
		verify(mimeMessage).setSubject("Join Our Telegram Channel!");

		// Verify the content type and content indirectly through the send method
		verify(javaMailSender).send(mimeMessage);

		ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
		verify(javaMailSender).send(messageCaptor.capture());

		MimeMessage sentMessage = messageCaptor.getValue();
		assertNotNull(sentMessage);

	}

	@Test
	void testStoreOTP() throws NoSuchFieldException, IllegalAccessException {
		String email = "test@example.com";
		String otp = "123456";
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

		EmailService.storeOTP(email, otp, expiryTime);

		Field otpStoreField = EmailService.class.getDeclaredField("otpStore");
		otpStoreField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, EmailService.OTPDetails> otpStore = (Map<String, EmailService.OTPDetails>) otpStoreField.get(null);

		EmailService.OTPDetails otpDetails = otpStore.get(email);

		// Use reflection to access private fields
		Field otpField = EmailService.OTPDetails.class.getDeclaredField("otp");
		otpField.setAccessible(true);
		String storedOtp = (String) otpField.get(otpDetails);

		Field expiryTimeField = EmailService.OTPDetails.class.getDeclaredField("expiryTime");
		expiryTimeField.setAccessible(true);
		LocalDateTime storedExpiryTime = (LocalDateTime) expiryTimeField.get(otpDetails);

		assertNotNull(otpDetails);
		assertEquals(otp, storedOtp);
		assertTrue(LocalDateTime.now().isBefore(storedExpiryTime));
	}

	@Test
	void testVerifyOTP() {
		String email = "test@example.com";
		String otp = "123456";
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

		EmailService.storeOTP(email, otp, expiryTime);

		int result = EmailService.verifyOTP(email, otp);
		assertEquals(1, result);

		// Verify OTP again to check that it has been removed
		result = EmailService.verifyOTP(email, otp);
		assertEquals(0, result);
	}
}
