/* (C) 2025 */
package pl.dealsniper.core.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.dealsniper.core.dto.request.user.UserRequest;

@Service
public class CryptoService {

    public UserRequest prepareHashedUserRequest(String userEmail, String userPassword) {
        return UserRequest.builder()
                .email(userEmail)
                .password(hashPassword(userPassword))
                .build();
    }

    public boolean verifyPassword(String enteredPassword, String storedPassword) {
        return BCrypt.checkpw(enteredPassword, storedPassword);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
