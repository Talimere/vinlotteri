package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Lottery;

import java.time.LocalDate;

public record LotteryInfoDto(
        Long id,
        LocalDate date,
        boolean raffled,
        Integer ballots
) {
    public LotteryInfoDto(Lottery lottery) {
        this(
                lottery.getId(),
                lottery.getDate(),
                lottery.isRaffled(),
                lottery.getBallots().size()
        );
    }
}
