package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.persistence.enums.Role;
import com.project.userservice.persistence.enums.UserStatus;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.repository.UserRepository;
import com.project.userservice.service.exception.UserAlreadyExistsException;
import com.project.userservice.service.impl.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private UserServiceImpl userService;

  @Test
  public void testSaveUser_WhenUserNotExist_Success() {
    // Given
    String email = "demchenko@gmail.com";
    String firstName = "Oleksandr";
    String lastName = "Demchenko";

    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .build();

    User user = User.builder()
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .role(Role.USER)
        .userStatus(UserStatus.INACTIVE)
        .build();

    when(userRepository.findUserByEmail(userRegistrationRequest.getEmail())).thenReturn(
        Optional.empty());
    when(userMapper.toUser(userRegistrationRequest)).thenReturn(user);

    // When
    User savedUser = userService.saveUser(userRegistrationRequest);

    // Then
    assertNotNull(savedUser);
    assertEquals("demchenko@gmail.com", savedUser.getEmail());
    assertEquals("Oleksandr", savedUser.getFirstName());
    assertEquals("Demchenko", savedUser.getLastName());
    assertEquals(Role.USER, savedUser.getRole());
    assertEquals(UserStatus.INACTIVE, savedUser.getUserStatus());
  }

  @Test
  public void testSaveUser_WhenUserExist_ShouldThrowUserAlreadyExistsException() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .build();

    when(userRepository.findUserByEmail(userRegistrationRequest.getEmail())).thenReturn(
        Optional.of(new User()));

    // When & Then
    assertThrows(UserAlreadyExistsException.class,
        () -> userService.saveUser(userRegistrationRequest));
  }
}
