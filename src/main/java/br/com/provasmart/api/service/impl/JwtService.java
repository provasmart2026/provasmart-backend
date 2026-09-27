package br.com.provasmart.api.service.impl;

import br.com.provasmart.api.domain.entity.users.UserEntity;
import br.com.provasmart.api.service.IJwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class JwtService implements IJwtService {

    private final JwtEncoder jwtEncoder;
    private final Long expiration;

    public JwtService(JwtEncoder jwtEncoder, @Value("${spring.jwt.expiration}") Long expiration) {
        this.jwtEncoder = jwtEncoder;
        this.expiration = expiration;
    }

    @Override
    public String generateToken(UserEntity userEntity) {
        log.info("Generating token");

        var issuedAt = Instant.now();
        var expiresAt = issuedAt.plusSeconds(expiration);

        var claims = JwtClaimsSet.builder().subject(userEntity.getId().toString())
                .issuedAt(issuedAt).expiresAt(expiresAt)
                .claim("email", userEntity.getEmail())
                .claim("role", userEntity.getRole().name())
                .build();

        var header = JwsHeader.with(MacAlgorithm.HS256).build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

        return jwt.getTokenValue();
    }
}
