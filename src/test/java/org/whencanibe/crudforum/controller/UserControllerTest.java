package org.whencanibe.crudforum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register")) // 뷰 이름 확인
                .andExpect(model().attributeExists("user")); // 모델에 "user" 속성 존재 여부 확인
    }

    @Test
    public void testRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("username", "testuser"))
                .andExpect(status().is3xxRedirection()) // 리다이렉트 상태 코드 확인
                .andExpect(redirectedUrl("/login")); // 로그인 페이지로 리다이렉트 확인
    }

    @Test
    public void testLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login")); // 로그인 폼 뷰 이름 확인
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // Given: 먼저 회원가입을 진행
        mockMvc.perform(post("/register")
                .with(csrf())  // CSRF 토큰 추가
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("username", "testuser"));

        // When / Then: 올바른 정보로 로그인 시도
        mockMvc.perform(post("/login")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection()) // 로그인 성공 후 리다이렉트
                .andExpect(redirectedUrl("/")); // 홈 페이지로 리다이렉트 확인
    }

    @Test
    public void testLoginUser_Failure_InvalidPassword() throws Exception {
        // Given: 회원가입을 진행
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("username", "testuser"));

        // When / Then: 잘못된 비밀번호로 로그인 시도
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk()) // 로그인 실패로 로그인 페이지 반환
                .andExpect(view().name("user/login")) // 다시 로그인 페이지로 렌더링
                .andExpect(model().attributeExists("error")); // 에러 메시지가 모델에 포함
    }

    @Test
    public void testLoginUser_Failure_UserNotFound() throws Exception {
        // When / Then: 존재하지 않는 사용자로 로그인 시도
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "nonexistent@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk()) // 로그인 실패로 로그인 페이지 반환
                .andExpect(view().name("user/login")) // 다시 로그인 페이지로 렌더링
                .andExpect(model().attributeExists("error")); // 에러 메시지가 모델에 포함
    }

    @Test
    public void testLogoutUser() throws Exception {
        // Given: 세션 생성 (로그인 시도)
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("username", "testuser"));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "test@example.com")
                .param("password", "password123"));

        // When / Then: 로그아웃 요청
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection()) // 로그아웃 후 리다이렉트
                .andExpect(redirectedUrl("/")); // 홈 페이지로 리다이렉트 확인
    }
}
