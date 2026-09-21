package br.com.provasmart.api.service;

public interface IEmailService {

    void sendTwoFactorCode(String to, String code);

    void sendPasswordResetCode(String to, String code);
}
