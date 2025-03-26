package com.talimere.vinlotteri.service;

import com.talimere.vinlotteri.dto.BallotDto;
import com.talimere.vinlotteri.dto.RaffleResultDto;
import com.talimere.vinlotteri.dto.WineDto;
import com.talimere.vinlotteri.model.Holder;
import com.talimere.vinlotteri.model.Lottery;
import com.talimere.vinlotteri.model.Ballot;
import com.talimere.vinlotteri.model.Wine;
import com.talimere.vinlotteri.repository.BallotRepository;
import com.talimere.vinlotteri.repository.HolderRepository;
import com.talimere.vinlotteri.repository.LotteryRepository;
import com.talimere.vinlotteri.repository.WineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LotteryService {

    private final LotteryRepository lotteryRepository;
    private final WineRepository wineRepository;
    private final BallotRepository ballotRepository;
    private final HolderRepository holderRepository;

    private final Random random = ThreadLocalRandom.current();

    public LotteryService(
            LotteryRepository lotteryRepository,
            WineRepository wineRepository,
            BallotRepository ballotRepository,
            HolderRepository holderRepository) {
        this.lotteryRepository = lotteryRepository;
        this.wineRepository = wineRepository;
        this.ballotRepository = ballotRepository;
        this.holderRepository = holderRepository;
    }

    public Lottery createLottery(LocalDate date, Integer ballots) {
        if (date != null) {
            if (date.isBefore(LocalDate.now()))
                throw new IllegalArgumentException("Lottery can't be created in the past");
            if (lotteryRepository.findByDate(date).isPresent())
                throw new RuntimeException("There already exists a lottery for this date");
        } else {
            if (lotteryRepository.findByDate(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY))).isPresent())
                throw new RuntimeException("There already exists a lottery for this date");
        }
        if (ballots != null && ballots <= 0)
            throw new IllegalArgumentException("Lottery can't be created without ballots");

        Lottery lottery = new Lottery();
        lottery.setDate(
                Objects.requireNonNullElseGet(date, () ->
                        LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY))
                )
        );

        //Defaults to 100 ballots
        lottery.setBallots(
                IntStream.rangeClosed(1, ballots != null ? ballots : 100)
                        .mapToObj(i -> {
                            Ballot ballot = new Ballot(i);
                            ballot.setLottery(lottery);
                            return ballot;
                        }).collect(Collectors.toList())
        );

        return lotteryRepository.save(lottery);
    }

    @Transactional
    public Lottery addWineToLottery(Long lotteryId, String name, BigDecimal price) {
        Lottery lottery = lotteryRepository.findById(lotteryId).orElseThrow(() -> new NoSuchElementException("No lottery by that ID exists"));
        if (lottery.isRaffled()) throw new RuntimeException("Can't add wine to raffled lottery");

        Wine wine = new Wine(name, price);
        wine.setLottery(lottery);

        lottery.getWines().add(wine);

        return lotteryRepository.save(lottery);
    }

    public Optional<Lottery> getNextLottery() {
        return lotteryRepository
                .findFirstByRaffledFalseAndDateGreaterThanEqualOrderByDateAsc(LocalDate.now());
    }

    public List<Lottery> getAllLotteries() {
        return lotteryRepository.findAll();
    }

    public Optional<Lottery> getLotteryById(Long id) {
        return lotteryRepository.findById(id);
    }

    public List<Lottery> getUnraffledLotteries() {
        return lotteryRepository.findByRaffledFalseOrderByDateAsc();
    }

    public List<Lottery> getUpcomingLotteries() {
        return lotteryRepository.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now());
    }

    @Transactional
    public RaffleResultDto raffleWine(Long id, Long wineId) {
        Lottery lottery = lotteryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lottery does not exist"));
        if (lottery.getDate().isAfter(LocalDate.now())) throw new RuntimeException("Cannot raffle future lottery");

        Wine wine = wineRepository.getReferenceById(wineId);
        if(wine.getLottery() != lottery) throw new IllegalArgumentException("Wine does not belong to this lottery");

        //Check that wine is the cheapest one not yet raffled out, and exists in the lottery
        if (wine != getNextUnraffledWine(id).orElseThrow(() -> new NoSuchElementException("Unraffled wine not found"))) {
            throw new RuntimeException("Wine is not the cheapest unraffled one");
        }

        List<Ballot> ballots = lottery.getBallots().stream()
                .filter(ballot -> ballot.getHolder() != null) //Only get ballots with a holder
                .filter(ballot -> ballot.getWonWine() == null) //Only get ballots that haven't won
                .toList();

        if(ballots.isEmpty()) throw new RuntimeException("There are no undrawn ballots for this lottery");

        Ballot winningBallot = ballots.get(random.nextInt(ballots.size()));
        winningBallot.setWonWine(wine);
        wine.setWinningBallot(winningBallot);
        ballotRepository.save(winningBallot);

        RaffleResultDto resultDto = new RaffleResultDto(id, new WineDto(wine), new BallotDto(winningBallot));

        if (lottery.getWines().stream().noneMatch(w -> w.getWinningBallot() == null)) {
            lottery.setRaffled(true);
            lotteryRepository.save(lottery);
        }

        return resultDto;
    }

    public List<Ballot> getBallots(Long id, Boolean hasHolder) {
        if (hasHolder != null) {
            if (hasHolder) return ballotRepository.findEligibleBallotsForLottery(id);
            return ballotRepository.findByLotteryIdAndHolderIsNull(id);
        }
        return lotteryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Lottery does not exist"))
                .getBallots();
    }

    @Transactional
    public List<Ballot> purchaseBallots(Long lotteryId, Long holderId, List<Integer> ballotNumbers) {
        Lottery lottery = lotteryRepository.findById(lotteryId).orElseThrow(() -> new NoSuchElementException("Lottery does not exist"));
        Holder holder = holderRepository.findById(holderId).orElseThrow(() -> new NoSuchElementException("Holder does not exist"));
        if (lottery.isRaffled()) throw new RuntimeException("Lottery is already raffled");
        if (ballotNumbers.stream().anyMatch(n -> n < 1 || n > lottery.getBallots().size()))
            throw new IllegalArgumentException("One or more ballot numbers are invalid");

        List<Ballot> purchasedBallots = new ArrayList<>();

        ballotNumbers.forEach(n -> {
            Ballot ballot = lottery.getBallots().get(n - 1);
            if (ballot.getHolder() == null) {
                ballot.setHolder(holder);
                purchasedBallots.add(ballot);
            }
        });

        return purchasedBallots;
    }

    public List<Holder> getHolders() {
        return holderRepository.findAll();
    }

    public Optional<Holder> getHolder(Long id) {
        return holderRepository.findById(id);
    }

    public Holder createHolder(String name) {
        return holderRepository.save(new Holder(name));
    }

    //Helper method to check that next unraffled wine is the cheapest one
    private Optional<Wine> getNextUnraffledWine(Long lotteryId) {
        return wineRepository.findByLotteryIdOrderByPriceAsc(lotteryId).stream()
                .filter(w -> w.getWinningBallot() == null)
                .findFirst();
    }
}
