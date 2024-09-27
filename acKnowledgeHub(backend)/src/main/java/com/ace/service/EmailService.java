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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class EmailService {
    private static final Map<String, OTPDetails> otpStore = new HashMap<>();


    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendOTPEmail(String toEmail, String otpCode) {
        // Construct the HTML message body
        String messageBody = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>OTP Verification</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                "table { max-width: 600px; margin: 40px auto; padding: 0; background-color: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #dddddd; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2); }" +
                "thead { background-color: #1a4a80; }" +
                "th { padding: 20px; color: white; }" +
                "h1 { font-size: 28px; margin: 0; text-align: center; }" +
                "td { padding: 30px; color: #333333; text-align: center; }" +
                "h2 { font-size: 22px; margin-bottom: 15px; }" +
                "p { font-size: 16px; line-height: 1.5; color: #555555; margin: 10px 0; }" +
                ".footer { background-color: #f9f9f9; text-align: center; padding: 20px; font-size: 12px; color: #888888; border-top: 1px solid #dddddd; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<table>" +
                "<thead>" +
                "<tr><th><h1>Your OTP Code</h1></th></tr>" +
                "</thead>" +
                "<tbody>" +
                "<tr><td><h2>Hello,</h2>" +
                "<p>Your one-time password (OTP) is:</p>" +
                "<h2><strong>" + otpCode + "</strong></h2>" +
                "<p>Please use this code to complete your verification.</p>" +
                "<p>Thank you!</p>" +
                "</td></tr>" +
                "</tbody>" +
                "<tfoot>" +
                "<tr><td class=\"footer\"><p>If you did not request this email, please ignore it.</p><p>© 2024 ACE Data Systems</p></td></tr>" +
                "</tfoot>" +
                "</table>" +
                "</body>" +
                "</html>";

        // Create a MimeMessage
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart
            helper.setTo(toEmail);
            helper.setSubject("Verification Code");
            helper.setText(messageBody, true); // true indicates the text is HTML
            javaMailSender.send(message);
        } catch (Exception e) {
            // Handle exceptions (log them or throw a custom exception)
            e.printStackTrace();
        }
    }

    @Async
    public void sendFileEmail(String toEmail, String subject, MultipartFile file,String fileName  ,Integer announcementId ) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setSubject(subject);

            String downloadUrl = "http://localhost:8080/api/v1/announcement/all/download?publicId=" + fileName + "&userEmail=" + URLEncoder.encode(toEmail, "UTF-8");
            String backendApiUrl = "http://localhost:8080/api/v1/announcement/all/note?announcementId=" + announcementId + "&userEmail=" + URLEncoder.encode(toEmail, "UTF-8");
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
                    + "<a href='" + backendApiUrl + "' style='display:inline-block;background-color:#28a745;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;'>Noted</a>"
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

    @Async
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
            helper.setSubject("Invitation to Our Telegram Bot");

            // Enhanced HTML content with updated styling
            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html lang='en'>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "<title>Join Our Telegram Bot</title>" +
                            "<style>" +
                            "body {" +
                            "font-family: Arial, sans-serif;" +
                            "margin: 0;" +
                            "padding: 0;" +
                            "background-color: #f4f4f4;" +
                            "}" +
                            "table {" +
                            "max-width: 600px;" +
                            "margin: 20px auto;" +
                            "padding: 0;" +
                            "background-color: #ffffff;" +
                            "border-radius: 8px;" +
                            "overflow: hidden;" +
                            "border: 1px solid #dddddd;" +
                            "box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);" +
                            "}" +
                            "thead {" +
                            "background-color: #1a4a80;" +
                            "}" +
                            "th {" +
                            "padding: 15px;" +
                            "color: white;" +
                            "}" +
                            "h1 {" +
                            "font-size: 24px;" +
                            "margin: 0;" +
                            "text-align: center;" +
                            "}" +
                            "td {" +
                            "padding: 20px;" +
                            "color: #333333;" +
                            "text-align: center;" +
                            "}" +
                            "h2 {" +
                            "font-size: 20px;" +
                            "margin-bottom: 15px;" +
                            "}" +
                            "p {" +
                            "font-size: 16px;" +
                            "line-height: 1.5;" +
                            "color: #555555;" +
                            "margin: 10px 0;" +
                            "}" +
                            ".button {" +
                            "margin: 20px 0;" +
                            "}" +
                            ".button a {" +
                            "padding: 12px 25px;" +
                            "background-color: #1a4a80;" +
                            "color: white;" +
                            "text-decoration: none;" +
                            "font-size: 16px;" +
                            "font-weight: bold;" +
                            "border-radius: 5px;" +
                            "display: inline-block;" +
                            "transition: background-color 0.3s ease;" +
                            "}" +
                            ".button a:hover {" +
                            "background-color: #16355d;" +
                            "}" +
                            ".footer {" +
                            "background-color: #f9f9f9;" +
                            "text-align: center;" +
                            "padding: 15px;" +
                            "font-size: 12px;" +
                            "color: #888888;" +
                            "border-top: 1px solid #dddddd;" +
                            "}" +
                            ".header-image {" +
                            "width: 60%;" +
                            "border-bottom: 2px solid #1a4a80;" +
                            "}" +
                            ".highlight {" +
                            "background-color: #e0f7fa;" +
                            "padding: 10px;" +
                            "border-radius: 5px;" +
                            "margin-bottom: 20px;" +
                            "border: 1px solid #b2ebf2;" +
                            "}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<table>" +
                            "<thead>" +
                            "<tr>" +
                            "<th>" +
                            "<h1>Invitation to Our Telegram Bot</h1>" +
                            "</th>" +
                            "</tr>" +
                            "</thead>" +
                            "<tbody>" +
                            "<tr>" +
                            "<td>" +
                            "<img src='https://mailsend-email-assets.mailtrap.io/1uuaenp04mjtfgrz9utosyclm3v6.png' alt='Invitation Header' class='header-image'>" +
                            "<h2>Hello,</h2>" +
                            "<p>We are pleased to invite you to join our Telegram bot, designed to keep you updated with the latest announcements and important information from us.</p>" +
                            "<p>Joining our Telegram bot is the best way to stay informed and never miss out on any critical updates.</p>" +
                            "<div class='highlight'>" +
                            "<p><strong>Here’s What You’ll Get:</strong></p>" +
                            "<ul>" +
                            "<li>Timely announcements.</li>" +
                            "<li>Exclusive updates.</li>" +
                            "<li>Important information directly to your Telegram.</li>" +
                            "</ul>" +
                            "</div>" +
                            "<p>Click the button below to join our Telegram bot and stay updated with all our important news.</p>" +
                            "<div class='button'>" +
                            "<a href='https://t.me/AcKnowledgeHubBot'>Join Our Telegram Bot</a>" +
                            "</div>" +
                            "<p>Thank you for your attention!</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tbody>" +
                            "<tfoot>" +
                            "<tr>" +
                            "<td class='footer'>" +
                            "<p>If you did not request this email or have any questions, please contact us at info@acedatasystems.com</p>" +
                            "<p>© 2024 All rights reserved by ACE Data Systems Ltd.</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tfoot>" +
                            "</table>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email to: " + recipientEmail);
        }
    }

    public void sendAnnouncementApprovalNotification(String recipientEmail, String announcementTitle) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Announcement Approval Notification");

            // Enhanced HTML content with updated styling
            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html lang='en'>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "<title>Announcement Approval Notification</title>" +
                            "<style>" +
                            "body {" +
                            "font-family: Arial, sans-serif;" +
                            "margin: 0;" +
                            "padding: 0;" +
                            "background-color: #f9f9f9;" +
                            "}" +
                            "table {" +
                            "max-width: 600px;" +
                            "margin: 20px auto;" +
                            "padding: 0;" +
                            "background-color: #ffffff;" +
                            "border-radius: 8px;" +
                            "overflow: hidden;" +
                            "border: 1px solid #dddddd;" +
                            "box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);" +
                            "}" +
                            "thead {" +
                            "background-color: #1a4a80;" + // Original color
                            "}" +
                            "th {" +
                            "padding: 15px;" +
                            "color: white;" +
                            "}" +
                            "h1 {" +
                            "font-size: 24px;" +
                            "margin: 0;" +
                            "text-align: center;" +
                            "}" +
                            "td {" +
                            "padding: 20px;" +
                            "color: #333333;" +
                            "text-align: center;" +
                            "}" +
                            "h2 {" +
                            "font-size: 20px;" +
                            "margin-bottom: 15px;" +
                            "}" +
                            "p {" +
                            "font-size: 16px;" +
                            "line-height: 1.5;" +
                            "color: #555555;" +
                            "margin: 10px 0;" +
                            "}" +
                            ".footer {" +
                            "background-color: #f9f9f9;" +
                            "text-align: center;" +
                            "padding: 15px;" +
                            "font-size: 12px;" +
                            "color: #888888;" +
                            "border-top: 1px solid #dddddd;" +
                            "}" +
                            ".highlight {" +
                            "background-color: #e0f7fa;" + // Original highlight color
                            "padding: 10px;" +
                            "border-radius: 5px;" +
                            "margin-bottom: 20px;" +
                            "border: 1px solid #b2ebf2;" +
                            "}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<table>" +
                            "<thead>" +
                            "<tr>" +
                            "<th>" +
                            "<h1>Announcement Approved!</h1>" +
                            "</th>" +
                            "</tr>" +
                            "</thead>" +
                            "<tbody>" +
                            "<tr>" +
                            "<td>" +
                            "<h2>Congratulations!</h2>" +
                            "<p>Your announcement titled <strong>" + announcementTitle + "</strong> has been approved.</p>" +
                            "<p>Thank you for your contribution!</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tbody>" +
                            "<tfoot>" +
                            "<tr>" +
                            "<td class='footer'>" +
                            "<p>If you have any questions, please contact HR at hr@example.com</p>" +
                            "<p>© 2024 All rights reserved by ACE Data Systems.</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tfoot>" +
                            "</table>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email to: " + recipientEmail);
        }
    }

    public void sendAnnouncementRejectionNotification(String recipientEmail, String announcementTitle, String rejectionReason) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Announcement Rejection Notification");

            // Enhanced HTML content with updated styling
            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html lang='en'>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "<title>Announcement Rejection Notification</title>" +
                            "<style>" +
                            "body {" +
                            "font-family: Arial, sans-serif;" +
                            "margin: 0;" +
                            "padding: 0;" +
                            "background-color: #f9f9f9;" +
                            "}" +
                            "table {" +
                            "max-width: 600px;" +
                            "margin: 20px auto;" +
                            "padding: 0;" +
                            "background-color: #ffffff;" +
                            "border-radius: 8px;" +
                            "overflow: hidden;" +
                            "border: 1px solid #dddddd;" +
                            "box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);" +
                            "}" +
                            "thead {" +
                            "background-color: #dc3545;" + // Red header for rejection
                            "}" +
                            "th {" +
                            "padding: 15px;" +
                            "color: white;" +
                            "}" +
                            "h1 {" +
                            "font-size: 24px;" +
                            "margin: 0;" +
                            "text-align: center;" +
                            "}" +
                            "td {" +
                            "padding: 20px;" +
                            "color: #333333;" +
                            "text-align: center;" +
                            "}" +
                            "h2 {" +
                            "font-size: 20px;" +
                            "margin-bottom: 15px;" +
                            "}" +
                            "p {" +
                            "font-size: 16px;" +
                            "line-height: 1.5;" +
                            "color: #555555;" +
                            "margin: 10px 0;" +
                            "}" +
                            ".footer {" +
                            "background-color: #f9f9f9;" +
                            "text-align: center;" +
                            "padding: 15px;" +
                            "font-size: 12px;" +
                            "color: #888888;" +
                            "border-top: 1px solid #dddddd;" +
                            "}" +
                            ".highlight {" +
                            "background-color: #f8d7da;" + // Light red highlight for rejection reason
                            "padding: 10px;" +
                            "border-radius: 5px;" +
                            "margin-bottom: 20px;" +
                            "border: 1px solid #f5c6cb;" +
                            "}" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<table>" +
                            "<thead>" +
                            "<tr>" +
                            "<th>" +
                            "<h1>Announcement Rejected</h1>" +
                            "</th>" +
                            "</tr>" +
                            "</thead>" +
                            "<tbody>" +
                            "<tr>" +
                            "<td>" +
                            "<h2>We're Sorry!</h2>" +
                            "<p>Your announcement titled <strong>" + announcementTitle + "</strong> has been rejected.</p>" +
                            "<div class='highlight'>" +
                            "<p><strong>Reason for Rejection:</strong></p>" +
                            "<p>" + rejectionReason + "</p>" +
                            "</div>" +
                            "<p>If you have any questions or need clarification, please feel free to reach out.</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tbody>" +
                            "<tfoot>" +
                            "<tr>" +
                            "<td class='footer'>" +
                            "<p>If you have any questions, please contact HR at hr@example.com</p>" +
                            "<p>© 2024 All rights reserved by ACE Data Systems.</p>" +
                            "</td>" +
                            "</tr>" +
                            "</tfoot>" +
                            "</table>" +
                            "</body>" +
                            "</html>";

            helper.setText(htmlContent, true);

            // Send the email
            javaMailSender.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error sending email to: " + recipientEmail);
        }
    }



}
