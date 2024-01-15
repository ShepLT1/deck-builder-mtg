package com.mtg.mana;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManaSymbolRepository extends JpaRepository<ManaSymbol, Long> {

    List<ManaSymbol> findByIdIn(List<Long> idList);

}