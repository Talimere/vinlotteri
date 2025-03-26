package com.talimere.vinlotteri.controller;

import com.talimere.vinlotteri.dto.*;
import com.talimere.vinlotteri.model.Lottery;
import com.talimere.vinlotteri.service.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    /**
     * Get next upcoming lottery
     *
     * @return LotteryInfoDTO
     */
    @GetMapping
    public ResponseEntity<LotteryInfoDto> getNextLottery() {
        return lotteryService.getNextLottery()
                .map(LotteryInfoDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new lottery, defaults to next friday with 100 ballots
     *
     * @param request JSON object with optional date and ballot amount
     * @return LotteryInfoDTO
     */
    @PostMapping
    public ResponseEntity<?> createLottery(@RequestBody LotteryCreateRequest request) {
        try {
            Lottery created = lotteryService.createLottery(request.date(), request.ballots());
            return ResponseEntity.ok(new LotteryInfoDto(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * Get all lotteries
     *
     * @return list of LotteryInfoDTO
     */
    @GetMapping("/all")
    public ResponseEntity<List<LotteryInfoDto>> getAllLotteries() {
        return ResponseEntity.ok(
                lotteryService.getAllLotteries().stream().map(LotteryInfoDto::new).toList()
        );
    }

    /**
     * Get specific lottery
     *
     * @param id Lottery ID
     * @return LotteryInfoDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<LotteryInfoDto> getLottery(@PathVariable Long id) {
        return lotteryService.getLotteryById(id)
                .map(LotteryInfoDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get detailed lottery
     *
     * @return LotteryDetailDTO
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<LotteryDetailDto> getDetailedLottery(@PathVariable Long id) {
        return lotteryService.getLotteryById(id)
                .map(LotteryDetailDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all upcoming (unraffled) lotteries
     *
     * @return list of LotteryInfoDTO
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<LotteryInfoDto>> getUpcomingLotteries() {
        return ResponseEntity.ok(
                lotteryService.getUpcomingLotteries().stream().map(LotteryInfoDto::new).toList()
        );
    }

    /**
     * Get all unraffled lotteries (including past ones)
     *
     * @return list of LotteryInfoDTO
     */
    @GetMapping("/unraffled")
    public ResponseEntity<List<LotteryInfoDto>> getUnraffledLotteries() {
        return ResponseEntity.ok(
                lotteryService.getUnraffledLotteries().stream().map(LotteryInfoDto::new).toList()
        );
    }

    /**
     * Get wines for lottery
     *
     * @param id Lottery ID
     * @return list of wines
     */
    @GetMapping("/{id}/wines")
    public ResponseEntity<LotteryWinesDto> getWinesForLottery(@PathVariable Long id) {
        return lotteryService.getLotteryById(id)
                .map(LotteryWinesDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add wine to lottery
     *
     * @param id   Lottery ID
     * @param wine JSON of wine name and price
     * @return LotteryWinesDTO
     */
    @PostMapping("/{id}/wines")
    public ResponseEntity<LotteryWinesDto> addWineToLottery(
            @PathVariable Long id,
            @RequestBody WineCreateRequest wine
    ) {
        return ResponseEntity.ok(
                new LotteryWinesDto(lotteryService.addWineToLottery(id, wine.name(), wine.price()))
        );
    }

    /**
     * Raffle out the next wine
     *
     * @param id     Lottery ID
     * @param wineId Wine ID
     * @return RaffleResultDTO
     */
    @PostMapping("/{id}/raffle/{wineId}")
    public ResponseEntity<?> raffleNextWine(@PathVariable Long id, @PathVariable Long wineId) {
        try {
            RaffleResultDto resultDto = lotteryService.raffleWine(id, wineId);
            return ResponseEntity.ok(resultDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * Get ballots for lottery
     *
     * @param id        Lottery ID
     * @param hasHolder Return ballots with holder?
     *                  Set to true for a list of ballots eligible to win a wine
     *                  Returns all ballots for lottery if unset
     * @return List of BallotDTO
     */
    @GetMapping("/{id}/ballots")
    public ResponseEntity<List<BallotDto>> getBallots(@PathVariable Long id, @RequestParam(required = false) Boolean hasHolder) {
        return ResponseEntity.ok(
                lotteryService.getBallots(id, hasHolder).stream().map(BallotDto::new).toList()
        );
    }

    /**
     * Purchase ballots for lottery
     *
     * @param id      Lottery ID
     * @param request BallotPurchaseRequest DTO
     * @return List of purchased ballots
     */
    @PostMapping(value = "/{id}/purchase")
    ResponseEntity<?> purchase(@PathVariable Long id, @RequestBody BallotPurchaseRequest request) {
        try {
            List<BallotDto> ballotDtos = lotteryService.purchaseBallots(id, request.holderId(), request.ballots()).stream().map(BallotDto::new).toList();
            return ResponseEntity.ok(ballotDtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
}
