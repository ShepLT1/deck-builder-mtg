package com.mtg.admin.user;

import com.mtg.card.collectible.Collectible;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("")
    Page<User> all(HttpServletRequest request, Pageable pageable) throws AccessDeniedException {
        User user = userDetailsService.getUserFromRequestCookies(request);
        if (!userDetailsService.isAdmin(request)) {
            throw new AccessDeniedException("Access denied: Admin-only resource");
        }
        return userRepository.findAll(pageable);
    }

    // TODO: add get self endpoint that filters out password, roles, etc (used for profile page)

    @PatchMapping("/collectibles/{id}")
    List<Collectible> addOrRemoveCollectible(HttpServletRequest request, @PathVariable Long id, @RequestBody Map<String, String> fields) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        return userDetailsService.addOrRemoveCollectible(user, id, fields);
    }

}
