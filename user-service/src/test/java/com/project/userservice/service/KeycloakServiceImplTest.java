package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.service.exception.UserAlreadyExistsException;
import com.project.userservice.service.impl.KeycloakServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeycloakServiceImplTest {

  @Mock
  private Keycloak keycloak;
  @Mock
  private UsersResource usersResource;
  private KeycloakServiceImpl keycloakService;

  @BeforeEach
  void setUp() {
    keycloakService = new KeycloakServiceImpl(keycloak, "testRealm");
  }

  @Test
  void createUser_WhenUserNotExist_Success() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("password")
        .build();

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setUsername(userRegistrationRequest.getEmail());
    userRepresentation.setEmail(userRegistrationRequest.getEmail());
    userRepresentation.setFirstName(userRegistrationRequest.getFirstName());
    userRepresentation.setLastName(userRegistrationRequest.getLastName());
    userRepresentation.setEmailVerified(false);

    Response response = Response.status(Response.Status.CREATED).build();

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("testRealm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(usersResource.search(userRegistrationRequest.getEmail())).thenReturn(
        Collections.emptyList());
    when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);

    // When
    Response createUserResponse = keycloakService.createUser(userRegistrationRequest);

    // Then
    assertEquals(Status.CREATED.getStatusCode(), createUserResponse.getStatus());
  }

  @Test
  void createUser_WhenUserExist_ThrowsUserAlreadyExistsException() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .build();

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("testRealm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(usersResource.search(userRegistrationRequest.getEmail())).thenReturn(
        Collections.singletonList(new UserRepresentation()));

    // When & Then
    assertThrows(UserAlreadyExistsException.class,
        () -> keycloakService.createUser(userRegistrationRequest));
  }

  @Test
  void verifyUser_ValidUser_VerifiedSuccessfully() {
    // Given
    String email = "demchenko@gmail.com";
    String userId = "userId";
    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setId(userId);
    userRepresentation.setEmail(email);

    RealmResource realmResource = mock(RealmResource.class);
    UserResource userResource = mock(UserResource.class);
    when(usersResource.search(email)).thenReturn(
        Collections.singletonList(userRepresentation));
    when(keycloak.realm("testRealm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(usersResource.get(userId)).thenReturn(userResource);

    // When
    keycloakService.verifyUser(email);

    // Then
    verify(keycloak, times(2)).realm("testRealm");
    verify(realmResource.users(), times(1)).search(email);
    verify(realmResource.users().get(userId), times(1)).update(any(UserRepresentation.class));
  }

  @Test
  void verifyUser_UserNotFound_ThrowsEntityNotFoundException() {
    // Given
    String email = "nonexistent@example.com";

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("testRealm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(usersResource.search(email)).thenReturn(
        Collections.emptyList());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> keycloakService.verifyUser(email));

    verify(keycloak, times(1)).realm("testRealm");
    verify(realmResource.users(), times(1)).search(email);
    verify(realmResource.users(), never()).get(anyString());
  }
}
