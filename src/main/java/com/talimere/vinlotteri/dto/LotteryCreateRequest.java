package com.talimere.vinlotteri.dto;

import java.time.LocalDate;

public record LotteryCreateRequest(
        LocalDate date,
        Integer ballots
) {
}
