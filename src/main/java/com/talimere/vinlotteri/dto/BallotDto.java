package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Ballot;

public record BallotDto(
        Long id,
        Integer number,
        String holderName,
        String wonWineName
) {
    public BallotDto(Ballot ballot) {
        this(
                ballot.getId(),
                ballot.getNumber(),
                ballot.getHolder() != null ? ballot.getHolder().getName() : "",
                ballot.getWonWine() != null ? ballot.getWonWine().getName() : ""
        );
    }
}
