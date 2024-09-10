package com.ace.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class EmailService {
    private static final Map<String, OTPDetails> otpStore = new HashMap<>();


    private final JavaMailSender javaMailSender;
    private final String TELEGRAM_CHANNEL_LINK = "https://t.me/AcKnowledgeHubBot";

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOTPEmail(String toEmail, String subject, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(messageBody);
        javaMailSender.send(message);
    }

    public void sendFileEmail(String toEmail, String subject, MultipartFile file,String fileName) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(subject);

            String downloadUrl = "http://localhost:8080/api/v1/announcement/download?publicId=" + fileName + "&userEmail=" + URLEncoder.encode(toEmail, "UTF-8");
            String htmlContent = "<html>"
                    + "<body style='font-family: Arial, sans-serif; color: #333; background-color: #ffffff; padding: 20px; margin: 0;'>"
                    + "<div style='text-align: center;'>"
                    + "<h2 style='color: #007bff;'>Important Announcement</h2>"
                    + "<p style='font-size: 16px; margin: 20px 0;'>Please find the attached document below:</p>"
                    + "<div style='background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; display: inline-block;'>"
                    + "<p style='font-size: 16px; margin: 0;'>"
                    + "📄 <a style='text-decoration:none;color:black;' href='" + downloadUrl + "'><strong>" + file.getOriginalFilename() + "</strong></a>"
                    + "</p>"
                    + "</div>"
                    + "<p style='font-size: 16px; margin: 20px 0;'>Click on the attachment to view or download the document.</p>"
                    + "<p style='font-size: 14px; color: #888;'>If you have any questions, feel free to reach out to us.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";
            mimeMessageHelper.setText(htmlContent,true);
//            mimeMessageHelper.addAttachment(file.getOriginalFilename(), file);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class OTPDetails {
        String otp;
        LocalDateTime expiryTime;

        public OTPDetails(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    public static void storeOTP(String email, String otp, LocalDateTime expiryTime) {
        otpStore.put(email, new OTPDetails(otp, expiryTime));
    }

    public static int verifyOTP(String email, String otp) {
        OTPDetails otpDetails = otpStore.get(email);
        if (otpDetails != null && otpDetails.otp.equals(otp) && LocalDateTime.now().isBefore(otpDetails.expiryTime)) {
            otpStore.remove(email);
            return 1;
        }
        return 0;
    }
    public void sendTelegramChannelInvitation(String recipientEmail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipientEmail);
            helper.setSubject("Join Our Telegram Channel!");
            helper.setText(
                    "Hello,\n\nWe invite you to join our Telegram channel for the latest updates and notifications.\n\n" +
                            "Click the link below to join our channel:\n" + TELEGRAM_CHANNEL_LINK +
                            "\n\nThank you!",
                    true
            );

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email to: " + recipientEmail);
        }
    }

}
