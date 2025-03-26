package com.talimere.vinlotteri.repository;

import com.talimere.vinlotteri.model.Holder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolderRepository extends JpaRepository<Holder, Long> {
    Holder findByName(String name);
}
