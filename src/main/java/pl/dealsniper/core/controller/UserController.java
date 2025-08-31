/* (C) 2025 */
package pl.dealsniper.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dealsniper.core.dto.request.UserRequest;
import pl.dealsniper.core.dto.response.UserResponse;
import pl.dealsniper.core.mapper.UserMapper;
import pl.dealsniper.core.model.User;
import pl.dealsniper.core.service.CryptoService;
import pl.dealsniper.core.service.UserService;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CryptoService cryptoService;
    private final UserMapper userMapper;

    @PostMapping
    ResponseEntity<UserResponse> registerNewUser(@Valid @RequestBody UserRequest userRequest) {
        UserRequest hashedRequest = cryptoService.hashUserRequestPassword(userRequest);
        User savedUser = userService.saveUser(hashedRequest);
        // EmailService integration
        UserResponse userResponse = userMapper.toUserResponse(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
