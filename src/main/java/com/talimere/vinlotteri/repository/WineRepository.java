package com.talimere.vinlotteri.repository;

import com.talimere.vinlotteri.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WineRepository extends JpaRepository<Wine, Long> {
    List<Wine> findByLotteryIdOrderByPriceAsc(Long lotteryId);

    @Query("""
        SELECT w FROM Wine w
        WHERE w.winningBallot.holder.id = :holderId
""")
    List<Wine> findWinesByHolderId(@Param("holderId") Long holderId);
}
