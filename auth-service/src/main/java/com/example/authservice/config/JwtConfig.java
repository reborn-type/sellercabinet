package com.example.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Bean
    public RSAPrivateKey rsaPrivateKey(@Value("${jwt.private-key-path}") Resource resource)
            throws Exception {
        try (InputStream is = resource.getInputStream()){
            return RsaKeyConverters.pkcs8().convert(is);
        }
    }

    @Bean
    public RSAPublicKey rsaPublicKey(@Value("classpath:keys/public.pem") Resource resource)
            throws Exception {
        try (InputStream is = resource.getInputStream()){
            return RsaKeyConverters.x509().convert(is);
        }
    }
}