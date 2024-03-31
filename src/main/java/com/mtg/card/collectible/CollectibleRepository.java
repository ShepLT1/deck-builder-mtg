package com.mtg.card.collectible;

import com.mtg.admin.user.User;
import com.mtg.card.base.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectibleRepository extends JpaRepository<Collectible, Long> {

    Page<Collectible> findByCard_Id(Long cardId, Pageable pageable);

    Page<Collectible> findAllByUsersIn(List<User> users, Pageable pageable);

    Collectible findByCardAndCollectorNumberAndFinishAndPromo(Card card, String collectorNumber, Collectible.Finish finish, Collectible.PromoType promo);

}
