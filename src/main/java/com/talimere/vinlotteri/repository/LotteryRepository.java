package com.talimere.vinlotteri.repository;

import com.talimere.vinlotteri.model.Lottery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LotteryRepository extends JpaRepository<Lottery, Long> {
    Optional<Lottery> findFirstByRaffledFalseAndDateGreaterThanEqualOrderByDateAsc(LocalDate today);
    List<Lottery> findByRaffledFalseOrderByDateAsc();
    List<Lottery> findByDateGreaterThanEqualOrderByDateAsc(LocalDate today);
    Optional<Lottery> findByDate(LocalDate date);
}
