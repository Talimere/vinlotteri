package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Lottery;

import java.time.LocalDate;
import java.util.List;

public record LotteryDetailDto(
        Long id,
        LocalDate date,
        boolean raffled,
        List<BallotDto> ballots,
        List<WineDto> wines
) {
    public LotteryDetailDto(Lottery lottery) {
        this(
                lottery.getId(),
                lottery.getDate(),
                lottery.isRaffled(),
                lottery.getBallots().stream().map(BallotDto::new).toList(),
                lottery.getWines().stream().map(WineDto::new).toList()
        );
    }
}
