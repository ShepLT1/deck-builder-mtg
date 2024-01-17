package com.mtg.releaseSet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseSetRepository extends JpaRepository<ReleaseSet, Long> {

    Page<ReleaseSet> findByName(String name, Pageable pageable);

}
