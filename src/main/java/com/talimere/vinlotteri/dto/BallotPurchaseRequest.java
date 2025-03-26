package com.talimere.vinlotteri.dto;

import java.util.List;

public record BallotPurchaseRequest(
        Long holderId,
        List<Integer> ballots
) {
}
