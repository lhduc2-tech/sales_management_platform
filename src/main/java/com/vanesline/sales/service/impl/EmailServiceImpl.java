package com.vanesline.sales.service.impl;

import com.vanesline.sales.service.contract.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendActivationEmail(String toEmail, String fullName, String activationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Kích hoạt tài khoản - Sales Management Platform");
            
            String htmlContent = buildActivationEmailContent(fullName, activationLink);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }

    private String buildActivationEmailContent(String fullName, String activationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { 
                        display: inline-block; 
                        padding: 12px 30px; 
                        background-color: #4CAF50; 
                        color: white; 
                        text-decoration: none; 
                        border-radius: 5px; 
                        margin: 20px 0;
                    }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Sales Management Platform</h1>
                    </div>
                    <div class="content">
                        <h2>Xin chào %s,</h2>
                        <p>Tài khoản của bạn đã được tạo trên hệ thống Sales Management Platform.</p>
                        <p>Vui lòng nhấn vào nút bên dưới để kích hoạt tài khoản và thiết lập mật khẩu:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Kích hoạt tài khoản</a>
                        </p>
                        <p>Hoặc copy đường link sau vào trình duyệt:</p>
                        <p style="word-break: break-all; background-color: #eee; padding: 10px;">%s</p>
                        <p><strong>Lưu ý:</strong> Link này sẽ hết hạn sau 24 giờ.</p>
                    </div>
                    <div class="footer">
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                        <p>&copy; 2026 Sales Management Platform</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, activationLink, activationLink);
    }
}
