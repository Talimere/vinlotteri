package com.talimere.vinlotteri.repository;

import com.talimere.vinlotteri.model.Ballot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BallotRepository extends JpaRepository<Ballot, Long> {
    List<Ballot> findByLotteryIdAndHolderIsNull(Long lotteryId);

    @Query("""
        SELECT b FROM Ballot b
        WHERE b.lottery.id = :lotteryId
        AND b.holder IS NOT NULL
        AND b NOT IN (
            SELECT w.winningBallot FROM Wine w
            WHERE w.lottery.id = :lotteryId
            AND w.winningBallot IS NOT NULL
        )
""")
    List<Ballot> findEligibleBallotsForLottery(@Param("lotteryId") Long lotteryId);
}
