package com.talimere.vinlotteri.service;

import com.talimere.vinlotteri.IntegrationTestBase;
import com.talimere.vinlotteri.model.Lottery;
import com.talimere.vinlotteri.repository.LotteryRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@SpringBootTest
@Transactional
class LotteryServiceTest extends IntegrationTestBase {

    @Autowired
    private LotteryRepository lotteryRepository;

    @Autowired
    private LotteryService lotteryService;

    @Test
    void shouldCreateDefaultLotteryWithNullParams() {
        LocalDate expectedDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        Lottery lottery = lotteryService.createLottery(null, null);

        assertNotNull(lottery.getId());
        assertEquals(expectedDate, lottery.getDate());
        assertEquals(100, lottery.getBallots().size());
        assertEquals(1, lottery.getBallots().getFirst().getNumber());
        assertEquals(100, lottery.getBallots().getLast().getNumber());
        assertTrue(lottery.getBallots().stream().allMatch(ballot -> ballot.getLottery() == lottery));

        Lottery saved = lotteryRepository.findById(lottery.getId()).orElseThrow();
        assertEquals(expectedDate, saved.getDate());
        assertEquals(100, saved.getBallots().size());
        assertEquals(1, saved.getBallots().getFirst().getNumber());
        assertEquals(100, saved.getBallots().getLast().getNumber());
        assertTrue(saved.getBallots().stream().allMatch(ballot -> ballot.getLottery() == lottery));
    }

    @Test
    void shouldCreateLotteryWithCustomDateAndBallotAmounts() {
        LocalDate date = LocalDate.now().plusDays(10);
        int ballotCount = 4242;

        Lottery lottery = lotteryService.createLottery(date, ballotCount);

        assertEquals(date, lottery.getDate());
        assertEquals(ballotCount, lottery.getBallots().size());
        assertEquals(1, lottery.getBallots().getFirst().getNumber());
        assertEquals(ballotCount, lottery.getBallots().getLast().getNumber());
    }

    @Test
    void shouldThrowErrorWhenCreatingLotteryInThePast() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lotteryService.createLottery(LocalDate.now().minusDays(10), null)
        );
    }

    @Test
    void shouldThrowErrorOnDuplicateLotteryDates() {
        LocalDate now = LocalDate.now();
        lotteryService.createLottery(now, null);
        assertThrows(
                RuntimeException.class,
                () -> lotteryService.createLottery(now, 100)
        );
    }

    @Test
    void shouldThrowErrorWhenZeroOrLessBallots() {
        assertThrows(
                IllegalArgumentException.class,
                () -> lotteryService.createLottery(LocalDate.now(), -1)
        );
    }
}