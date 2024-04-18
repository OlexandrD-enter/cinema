package com.project.userservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userservice.api.controller.AuthController;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.service.AuthService;
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
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
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

  private String asJsonString(final Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
