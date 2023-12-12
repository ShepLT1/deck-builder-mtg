package com.mtg.admin.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.mtg.admin.role.ERole.*;

@Component
public class RoleLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;
    @Autowired
    public RoleLoader(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void run(final ApplicationArguments args) {
        if (roleRepository.count() != 0) {
            return;
        }
        final Role user = new Role();
        user.setId(1);
        user.setName(ROLE_USER);
        roleRepository.save(user);
        final Role moderator = new Role();
        moderator.setId(2);
        moderator.setName(ROLE_MODERATOR);
        roleRepository.save(moderator);
        final Role admin = new Role();
        admin.setId(3);
        admin.setName(ROLE_ADMIN);
        roleRepository.save(admin);
    }

}
