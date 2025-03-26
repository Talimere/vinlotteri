package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Lottery;

import java.time.LocalDate;
import java.util.List;

public record LotteryWinesDto(
        Long id,
        LocalDate date,
        List<WineDto> wines
) {
    public LotteryWinesDto(Lottery lottery) {
        this(
                lottery.getId(),
                lottery.getDate(),
                lottery.getWines().stream().map(WineDto::new).toList()
        );
    }
}
