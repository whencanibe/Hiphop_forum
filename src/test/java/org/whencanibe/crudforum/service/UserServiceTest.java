package org.whencanibe.crudforum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.whencanibe.crudforum.domain.User;
import org.whencanibe.crudforum.repository.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";

        // When
        User registeredUser = userService.registerUser(email, password, username);

        //Then
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(passwordEncoder.matches(password, registeredUser.getPassword())).isTrue(); // 비밀번호가 암호화되어 저장되었는지 확인
        assertThat(registeredUser.getEmail()).isEqualTo(email);
        assertThat(registeredUser.getUsername()).isEqualTo(username);
    }

    @Test
    void loginSuccess() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";

        User registeredUser = userService.registerUser(email, password, username);

        // When
        User loggedInUser = userService.login(email, password);

        // Then
        assertThat(loggedInUser.getId()).isEqualTo(registeredUser.getId()); // 로그인 성공 여부 확인
    }

    @Test
    public void loginFailureInvalidPassword() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";

        userService.registerUser(email, password, username);

        // When / Then
        assertThatThrownBy(() -> userService.login(email, "wrongpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이메일 혹은 비밀번호가 틀립니다.");
    }

    @Test
    public void loginFailureUserNotFound() {
        // When / Then
        assertThatThrownBy(() -> userService.login("nonexistent@example.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이메일 혹은 비밀번호가 틀립니다.");
    }

    @Test
    void deleteUserNotFound() {
        //Given
        Long wrongUserId = -1L;
        // When / Then
        assertThatThrownBy(()-> userService.deleteUser(wrongUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("해당 회원을 찾을 수 없습니다.");
    }

    @Test
    public void deleteUserSuccess(){
        // Given
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";

        //When
        User user = userService.registerUser(email, password, username);
        Long userId = user.getId();
        userService.deleteUser(userId);

        //Then
        assertThat(userRepository.findById(userId)).isEmpty();
    }
}