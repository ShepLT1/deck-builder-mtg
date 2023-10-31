package com.mtg.admin.user;

import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}
