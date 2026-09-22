package br.com.provasmart.api.service;

import br.com.provasmart.api.dto.request.auth.ForgotPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.LoginRequestDTO;
import br.com.provasmart.api.dto.request.auth.ResetPasswordRequestDTO;
import br.com.provasmart.api.dto.request.auth.VerifyTwoFactorRequestDTO;
import br.com.provasmart.api.dto.response.auth.LoginResponseDTO;
import br.com.provasmart.api.dto.response.auth.TokenResponseDTO;

public interface IAuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    TokenResponseDTO verifyTwoFactor(VerifyTwoFactorRequestDTO verifyTwoFactorRequestDTO);

    LoginResponseDTO forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    LoginResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}
