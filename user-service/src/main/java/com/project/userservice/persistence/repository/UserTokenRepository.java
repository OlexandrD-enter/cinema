package com.project.userservice.persistence.repository;

import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing UserToken entities in the database.
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

  Optional<UserToken> findByUserAndTokenType(User user, TokenType tokenType);
  Optional<UserToken> findByToken(String token);
}
