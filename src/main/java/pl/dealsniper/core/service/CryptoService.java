package pl.dealsniper.core.service;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pl.dealsniper.core.dto.request.UserRequest;

@Service
public class CryptoService {

    @Value("${security.saltPrefix}")
    private String saltPrefix;

    @Value("${security.saltSuffix}")
    private String saltSuffix;

    public UserRequest hashUserRequestPassword(UserRequest userRequest) {
        return UserRequest.builder()
                .email(userRequest.email())
                .password(encryptPassword(userRequest.password()))
                .build();
    }

    private String encryptPassword(String password) {
        String saltedPassword = saltPrefix + password + saltSuffix;
        return DigestUtils.sha256Hex(saltedPassword);
    }

    public String getRandomHash() {
        String randomUUID = UUID.randomUUID().toString();
        return DigestUtils.sha256Hex(randomUUID);
    }
}
