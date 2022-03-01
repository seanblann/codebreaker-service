package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.dao.UserRepository;
import edu.cnm.deepdive.codebreaker.model.entity.User;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository repository;

  @Autowired
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  public User getOrCreate(String oauthKey, String displayName) {
    return repository
        .findByOauthKey(oauthKey)
        .map((user) -> {
          user.setConnected(new Date());
          return repository.save(user);
        })
        .orElseGet(() -> {
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          user.setConnected(new Date());
          return repository.save(user);
        });
  }

  public User getCurrentuser() {
    return (User) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();
  }

  public User updateUser(User received) {
    return repository
        .findById(getCurrentuser().getId())
        .map((user) -> {
          //noinspection ConstantConditions
          if (received.getDisplayName() != null) {
            user.setDisplayName(received.getDisplayName());
          }
          user.setIncognito(received.isIncognito());
          return repository.save(user);
        })
        .orElseThrow();
  }
}
