package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Wine;

import java.math.BigDecimal;

public record WineDto(
        Long id,
        String name,
        BigDecimal price,
        String winner
) {
    public WineDto(Wine wine) {
        this(
                wine.getId(),
                wine.getName(),
                wine.getPrice(),
                wine.getWinningBallot() != null ? wine.getWinningBallot().getHolder().getName() : ""
        );
    }
}
