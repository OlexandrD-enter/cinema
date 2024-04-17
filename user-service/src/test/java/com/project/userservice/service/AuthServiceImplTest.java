package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.persistence.model.User;
import com.project.userservice.service.exception.KeycloakException;
import com.project.userservice.service.impl.AuthServiceImpl;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock
  private KeycloakService keycloakService;
  @Mock
  private UserService userService;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void createUser_NewUser_SuccessfullyCreated() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("password")
        .build();

    User user = new User();
    user.setEmail(userRegistrationRequest.getEmail());

    UserRegistrationResponse expectedResponse = new UserRegistrationResponse();
    expectedResponse.setEmail(user.getEmail());

    Response keycloakResponse = Response.status(Status.CREATED).build();

    when(userService.saveUser(userRegistrationRequest)).thenReturn(user);
    when(keycloakService.createUser(userRegistrationRequest)).thenReturn(keycloakResponse);
    when(userMapper.toRegistrationResponse(user)).thenReturn(expectedResponse);

    // When
    UserRegistrationResponse response = authService.createUser(userRegistrationRequest);

    // Then
    assertEquals(expectedResponse.getEmail(), response.getEmail());
  }

  @Test
  void createUser_KeycloakExceptionThrown() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("password")
        .build();

    User user = new User();
    user.setEmail(userRegistrationRequest.getEmail());

    Response keycloakResponse = Response.status(Status.INTERNAL_SERVER_ERROR).build();

    when(userService.saveUser(userRegistrationRequest)).thenReturn(user);
    when(keycloakService.createUser(userRegistrationRequest)).thenReturn(keycloakResponse);

    // When & Then
    assertThrows(KeycloakException.class, () -> authService.createUser(userRegistrationRequest));
  }
}

