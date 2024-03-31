package com.mtg.card.collectible;

import com.mtg.admin.user.User;
import com.mtg.admin.user.UserDetailsServiceImpl;
import com.mtg.error.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cards/collectibles")
public class CollectibleController {

    @Autowired
    CollectibleRepository collectibleRepository;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("")
    Page<Collectible> all(HttpServletRequest request, @RequestParam(required=false) Long cardId, Pageable pageable) {
        if (cardId != null) {
            Page<Collectible> collectiblesByCardId = collectibleRepository.findByCard_Id(cardId, pageable);
            if (!collectiblesByCardId.hasContent()) {
                throw new EntityNotFoundException(cardId, "collectible card");
            }
            return collectiblesByCardId;
        }
        User user = userDetailsService.getUserFromRequestCookies(request);
        List<User> users = new ArrayList<>(List.of(user));
        return collectibleRepository.findAllByUsersIn(users, pageable);
    }

    @GetMapping("/{id}")
    Collectible one(@PathVariable Long id) {
        return collectibleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "collectible card"));
    }

    @PatchMapping("/{id}")
    Page<Collectible> addOrRemoveCollectibleFromCollection(HttpServletRequest request, @PathVariable Long id, @RequestBody Map<String, String> fields, Pageable pageable) {

        User user = userDetailsService.getUserFromRequestCookies(request);

        return userDetailsService.addOrRemoveCollectible(user, id, fields, pageable);
    }

}
