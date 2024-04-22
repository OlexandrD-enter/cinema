package com.project.userservice.service;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.persistence.model.User;

/**
 * UserService interface for user related operations.
 */
public interface UserService {

  User saveUser(UserRegistrationRequest userRegistrationRequest);

  User verifyUser(String email);
}
