package com.task.jwt_server.infrastructure.repository;

import com.task.jwt_server.domain.entity.User;
import com.task.jwt_server.domain.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private static final Map<String, User> inMemoryUserNameMap = new ConcurrentHashMap<>();
  private static final Map<Long, User> inMemoryUserIdMap = new ConcurrentHashMap<>();
  private static Long userId = 1L;

  private static void increaseUserId() {
    userId++;
  }

  @Override
  public void clear() {
    inMemoryUserIdMap.clear();
    inMemoryUserNameMap.clear();
  }

  @Override
  public User save(User user) {
    inMemoryUserNameMap.put(user.getUsername(), user);
    inMemoryUserIdMap.put(user.getUserId(), user);
    increaseUserId();
    return user;
  }

  @Override
  public Long findNextUserId() {
    return getUserId();
  }

  @Override
  public Optional<User> findById(Long userId) {
    return Optional.ofNullable(inMemoryUserIdMap.get(userId));
  }

  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(inMemoryUserNameMap.get(username));
  }

  public Long getUserId() {
    return userId;
  }

  @Override
  public boolean existsByUsername(String username) {
    return inMemoryUserNameMap.containsKey(username);
  }
}
