package com.project.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userservice.api.controller.AuthController;
import com.project.userservice.api.handler.GlobalExceptionHandler;
import com.project.userservice.domain.dto.UserEmailVerificationResponse;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.persistence.enums.UserStatus;
import com.project.userservice.service.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

  @Mock
  private AuthService authService;
  @InjectMocks
  private AuthController authController;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new GlobalExceptionHandler()).build();
  }

  @Test
  @SneakyThrows
  void register_WhenRequestDataValid_Success() {
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("Password123@")
        .build();

    UserRegistrationResponse userRegistrationResponse = UserRegistrationResponse.builder()
        .id(1L)
        .email(userRegistrationRequest.getEmail())
        .firstName(userRegistrationRequest.getFirstName())
        .lastName(userRegistrationRequest.getLastName())
        .build();

    when(authService.createUser(any(UserRegistrationRequest.class))).thenReturn(
        userRegistrationResponse);

    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userRegistrationRequest)))
        .andExpect(status().isCreated())
        .andExpectAll(
            jsonPath("$.id").value(userRegistrationResponse.getId()),
            jsonPath("$.email").value(userRegistrationResponse.getEmail()),
            jsonPath("$.firstName").value(userRegistrationResponse.getFirstName()),
            jsonPath("$.lastName").value(userRegistrationResponse.getLastName())
        );
  }

  @Test
  @SneakyThrows
  void register_WhenRequestDataInvalid_ShouldReturn4xxStatus() {
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("email")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("p")
        .build();

    mockMvc.perform(post("/api/v1/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userRegistrationRequest)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @SneakyThrows
  void verifyUserEmailByToken_ValidToken_Success() {
    String token = "validToken";
    UserEmailVerificationResponse response = new UserEmailVerificationResponse(
        "demchenko@gmail.com",
        UserStatus.ACTIVE);
    when(authService.verifyUserEmail(token)).thenReturn(response);

    mockMvc.perform(post("/api/v1/users/email-confirm/{token}", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("demchenko@gmail.com"))
        .andExpect(jsonPath("$.userStatus").value(UserStatus.ACTIVE.toString()));
  }

  @Test
  @SneakyThrows
  void verifyUserEmailByToken_InvalidToken_ShouldReturn4xxStatus() {
    String token = "invalidToken";
    when(authService.verifyUserEmail(token)).thenThrow(
        new EntityNotFoundException("Token not found"));

    mockMvc.perform(post("/api/v1/users/email-confirm/{token}", token))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Token not found"));
  }

  @Test
  @SneakyThrows
  void resendEmailConfirmation_ValidEmail_Success() {
    String email = "demchenko@gmail.com";

    mockMvc.perform(post("/api/v1/users/resend/email-confirmation/{email}", email)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void resendEmailConfirmation_InvalidEmail_ShouldReturn4xxStatus() {
    String email = "demchenko@gmail.com";
    doThrow(new EntityNotFoundException("No such token for user")).when(authService)
        .resendEmailConfirmation(email);

    mockMvc.perform(post("/api/v1/users/resend/email-confirmation/{email}", email))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("No such token for user"));
  }


  private String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
