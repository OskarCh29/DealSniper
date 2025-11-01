/* (C) 2025 */
package pl.dealsniper.core.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.dealsniper.core.dto.request.user.UserRequest;

@Service
public class CryptoService {

    @Value("${security.saltPrefix}")
    private String saltPrefix;

    @Value("${security.saltSuffix}")
    private String saltSuffix;

    public UserRequest prepareHashedUserRequest(String userEmail, String userPassword) {
        return UserRequest.builder()
                .email(userEmail)
                .password(encryptPassword(userPassword))
                .build();
    }

    private String encryptPassword(String password) {
        String saltedPassword = saltPrefix + password + saltSuffix;
        return DigestUtils.sha256Hex(saltedPassword);
    }
}
