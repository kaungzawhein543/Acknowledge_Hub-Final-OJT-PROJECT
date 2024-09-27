package com.ace.controller;

import com.ace.dto.EmailResponseDTO;
import com.ace.dto.OTPEmailDTO;
import com.ace.dto.PasswordResponseDTO;
import com.ace.entity.Staff;
import com.ace.service.EmailService;
import com.ace.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("api/v1/email")
public class EmailController {

    private final StaffService staffService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public EmailController(StaffService staffService, EmailService emailService,PasswordEncoder passwordEncoder) {
        this.staffService = staffService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> verify(@RequestParam("staffId") String staffId) {
        Staff dto = staffService.findByStaffId(staffId);

        if (dto != null) {
            // Check if the password matches default or admin password
            if (passwordEncoder.matches("acknowledgeHub", dto.getPassword()) ||
                    passwordEncoder.matches("adminPassword", dto.getPassword())) {
                return ResponseEntity.ok(dto.getCompanyStaffId() + ": Please change your password");
            }

            // Generate OTP
            Random random = new Random();
            int otp = random.nextInt(1000000);
            String otpNumber = String.format("%06d", otp);
            String otpAndText = otpNumber + " is your verification code.";

            // Send OTP via email
            emailService.sendOTPEmail(dto.getEmail(), "Verification code", otpAndText);

            // Store OTP and its expiry time
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);
            emailService.storeOTP(dto.getEmail(), otpNumber, expiryTime);

            // Create email response DTO
            EmailResponseDTO emailDTO = new EmailResponseDTO();
            emailDTO.setEmail(dto.getEmail());
            emailDTO.setExpiryTime(expiryTime);

            return ResponseEntity.ok(emailDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff ID not found");
        }
    }


    @PostMapping(value = "/verify-otp")
    public int verifyOtp(@RequestBody OTPEmailDTO dto) {
//        System.out.println("email " + dto.getEmail());
//        System.out.println("otp : " + dto.getOtp());
        int isValid = EmailService.verifyOTP(dto.getEmail(), dto.getOtp());
        if (isValid == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @PostMapping(value = "/update-password")
    public void updatePassword(@RequestBody PasswordResponseDTO dto) {
        staffService.updatePassword(dto);
    }

    //    @PostMapping(value = "/send-file")
//    public ResponseEntity<String> sendEmailFile(@RequestParam("email") String email, @RequestParam("file") MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File must not be null or empty");
//        }
//        try {
//            emailService.sendFileEmail(email, "file announcement", file);
//            return ResponseEntity.ok("Email sent successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
//        }
//    }

//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendToEmail(@RequestParam("email") String toEmail, @RequestParam("subject") String subject, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
//        try {
//            emailService.sendFileEmail(toEmail, subject, file, fileName);
//            return ResponseEntity.ok("successful");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("fail");
//        }
//
//    }
//

}
