package org.example.gestionvacaciones.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {

    private String secret;
    private long expiration = 86400000;
    private long refreshExpiration = 604800000;
    private String tokenPrefix = "Bearer ";
    private String headerString = "Authorization";

    // Getters sin valores por defecto hardcodeados
    public String getSecretKey() {
        return secret;
    }

    public long getExpirationTime() {
        return expiration;
    }

    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public String getHeaderString() {
        return headerString;
    }
}