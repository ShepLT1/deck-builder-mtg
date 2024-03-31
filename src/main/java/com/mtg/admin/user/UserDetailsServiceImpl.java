package com.mtg.admin.user;

import com.mtg.auth.JwtUtils;
import com.mtg.card.collectible.Collectible;
import com.mtg.card.collectible.CollectibleRepository;
import com.mtg.error.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    CollectibleRepository collectibleRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public User getUserFromRequestCookies(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

    public boolean isAdmin(HttpServletRequest request) {
        User user = getUserFromRequestCookies(request);
        return user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));
    }

    public Page<Collectible> addOrRemoveCollectible(User user, Long collectible_id, Map<String, String> fields, Pageable pageable) {
        if (!fields.containsKey("action") || (!fields.get("action").equals("add") && !fields.get("action").equals("remove"))) {
            throw new IllegalArgumentException("request body must contain 'action' field with value equal to either 'add' or 'remove'");
        }
        Collectible collectible = collectibleRepository.findById(collectible_id).orElseThrow(() -> new EntityNotFoundException(collectible_id, "collectible card"));
        if (fields.get("action").equals("add")) {
            user.addCollectible(collectible);
        } else {
            user.removeCollectible(collectible);
        }
        userRepository.save(user);
        List<User> users = new ArrayList<>(List.of(user));
        return collectibleRepository.findAllByUsersIn(users, pageable);
    }

}
