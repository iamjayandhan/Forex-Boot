//package gomobi.io.forex.util;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MailUtil {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    // Sends the OTP email to the provided email address
//    public boolean sendOtpEmail(String toEmail, String otp) {
//    	
//    	System.out.println("[MAIL UTIL] email: "+ toEmail);
//    	System.out.println("[MAIL UTIL] otp: "+ otp);
//    	
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(toEmail);
//            message.setSubject("Your OTP Code");
//            message.setText("Your OTP code is: " + otp);
//            message.setFrom("jayandhan.r@gomobi.io");
//
//            System.out.println("[MAIL UTIL] moments before departure!...");
//
//            try {
//                mailSender.send(message);
//                System.out.println("[MAIL UTIL] Mail sent.");
//            } catch (Exception e) {
//                System.out.println("[MAIL UTIL] Failed to send OTP email: "+ e.getMessage()+" : "+ e);
//                return false;
//            }
//            
//            return true;
//        } catch (Exception e) {
//        	System.out.println("[MAIL UTIL] something wrong buddy!");
//            return false;
//        }
//    }
//}

package gomobi.io.forex.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.springframework.core.io.ClassPathResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MailUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

    private static final String FROM_EMAIL = "forexnotifications25@gmail.com";
    private static final String OTP_SUBJECT = "Your One-Time Password (OTP)";

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an HTML-based OTP email to the specified address.
     *
     * @param toEmail the recipient email address
     * @param otp the one-time password to send
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendOtpEmail(String toEmail, String otp) {
        logger.info("Preparing to send OTP email to {}", toEmail);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px;'>" +
                    "<div style='text-align: center;'>" +
                    "<img src='cid:companyLogo' alt='Company Logo' style='height: 100px; margin-bottom: 20px;'/>" +
                    "<h2 style='color: #333;'>Your OTP Code</h2>" +
                    "</div>" +
                    "<p>Hello,</p>" +
                    "<p>Thank you for using <strong>Forex Trader</strong>. Please use the following OTP to complete your verification:</p>" +
                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<span style='font-size: 24px; font-weight: bold; color: #2E86C1;'>" + otp + "</span>" +
                    "</div>" +
                    "<p>This OTP is valid for <strong>10 minutes</strong>. Do not share it with anyone.</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #888;'>If you did not request this, please ignore this email.</p>" +
                    "<p style='font-size: 12px; color: #888;'>© 2025 forexnotifications25 | All rights reserved.</p>" +
                    "</div></body></html>";

            helper.setTo(toEmail);
            helper.setSubject(OTP_SUBJECT);
            helper.setFrom(FROM_EMAIL);
            helper.setText(htmlContent, true); // true = HTML
            
            ClassPathResource image = new ClassPathResource("static/company.png");
            helper.addInline("companyLogo", image);

            mailSender.send(message);
            logger.info("HTML OTP email sent to {}", toEmail);
            return true;

        } catch (MessagingException e) {
            logger.error("Failed to send HTML OTP email to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }
}
