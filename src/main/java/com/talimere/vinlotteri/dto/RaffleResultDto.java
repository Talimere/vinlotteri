package com.talimere.vinlotteri.dto;

public record RaffleResultDto(
        Long lotteryId,
        WineDto wine,
        BallotDto winningBallot
) {
}
