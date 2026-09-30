
package com.kodnest.app.services;

import java.util.List;
import com.kodnest.app.entities.User;

public interface UserService {

    User saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}