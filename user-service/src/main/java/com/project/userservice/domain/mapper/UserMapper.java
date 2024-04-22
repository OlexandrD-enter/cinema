package com.project.userservice.domain.mapper;

import com.project.userservice.domain.dto.UserEmailVerificationResponse;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for mapping between User entities and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "role", constant = "USER")
  @Mapping(target = "userStatus", constant = "INACTIVE")
  User toUser(UserRegistrationRequest userRegistrationRequest);

  UserRegistrationResponse toRegistrationResponse(User user);

  UserEmailVerificationResponse toEmailVerificationResponse(User user);
}
