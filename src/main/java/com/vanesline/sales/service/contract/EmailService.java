package com.vanesline.sales.service.contract;

public interface EmailService {

    void sendActivationEmail(String toEmail, String fullName, String activationLink);
}
