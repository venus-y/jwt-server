package com.task.jwt_server.domain.repository;

import com.task.jwt_server.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

  void clear();

  User save(User user);

  Long findNextUserId();

  Optional<User> findById(Long userId);

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);
}
