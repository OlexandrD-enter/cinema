package com.project.userservice.service;

import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;

/**
 * UserTokenService interface for user-tokens related operations.
 */
public interface UserTokenService {

  UserToken saveUserToken(User user, TokenType tokenType);
}
