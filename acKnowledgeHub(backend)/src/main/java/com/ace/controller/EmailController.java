package com.ace.controller;

import com.ace.dto.OTPEmailDTO;
import com.ace.dto.PasswordResponseDTO;
import com.ace.entity.Staff;
import com.ace.service.EmailService;
import com.ace.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
@Slf4j
@RestController
@RequestMapping("api/v1/email")
public class EmailController {

    private final StaffService staffService;
    private final EmailService emailService;

    public EmailController(StaffService staffService, EmailService emailService) {
        this.staffService = staffService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/send-otp")
    public void verify(@RequestParam("email") String email) {
        Staff dto = staffService.findByEmail(email);
        if (dto != null) {
            Random random = new Random();
            int otp = random.nextInt(1000000);
            String otpNumber = String.format("%06d", otp);
            String otpAndText = otpNumber + " is your verification code.";
            emailService.sendOTPEmail(email, "Verification code", otpAndText);
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(3);
            EmailService.storeOTP(email, otpNumber, expiryTime);
            log.info("Sending Email is successfully");
        } else {
            System.out.println("email is not login");
        }
    }

    @PostMapping(value = "/verify-otp")
    public int verifyOtp(@RequestBody OTPEmailDTO bean) {
        int isValid = EmailService.verifyOTP(bean.getEmail(), bean.getOtp());
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

}
