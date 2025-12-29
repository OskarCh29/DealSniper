/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.user.AccountRequest;
import pl.dealsniper.core.dto.request.user.UserRequest;
import pl.dealsniper.core.dto.request.user.VerificationRequest;
import pl.dealsniper.core.dto.response.user.UserResponse;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.service.CryptoService;
import pl.dealsniper.core.service.EmailService;
import pl.dealsniper.core.service.UserService;
import pl.dealsniper.core.service.VerificationService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final CryptoService cryptoService;
    private final UserMapper userMapper;

    @PostMapping("/verification")
    ResponseEntity<UserResponse> completeRegistration(@Valid @RequestBody VerificationRequest verificationRequest) {
        String userEmail = verificationService.getEmailByCode(verificationRequest);
        UserRequest hashedRequest = cryptoService.prepareHashedUserRequest(userEmail, verificationRequest.password());
        User savedUser = userService.saveUser(hashedRequest);
        verificationService.deleteUsedVerification(verificationRequest.code());
        UserResponse userResponse = userMapper.toUserResponse(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PostMapping("/registration")
    ResponseEntity<Void> register(@Valid @RequestBody AccountRequest accountRequest) {
        userService.ensureEmailAvailable(accountRequest.email());
        String verificationEmail = verificationService.generateVerificationLink(accountRequest.email());
        emailService.sendVerificationEmail(accountRequest.email(), verificationEmail);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/deactivation")
    ResponseEntity<Void> deactivateUserAccount(@RequestParam String userId) {
        userService.deleteUserAccount(UUID.fromString(userId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
