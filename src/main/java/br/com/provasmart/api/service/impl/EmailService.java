package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendTwoFactorCode(String to, String code) {
        log.info("Sending two-factor code to email service");

        var message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Prova Smart - Código de autenticação");
        message.setText("Seu código de autenticação é: " + code + "\n\nEste código é válido por 5 minutos.");

        mailSender.send(message);
        log.info("Two-factor authentication code sent successfully");

    }
}
