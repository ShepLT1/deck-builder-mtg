package com.mtg.releaseSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseSetRepository extends JpaRepository<ReleaseSet, Long> {

    Page<ReleaseSet> findByNameContains(String name, Pageable pageable);

    ReleaseSet findByName(String name);

}
