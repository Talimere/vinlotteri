package com.talimere.vinlotteri.dto;

import java.math.BigDecimal;

public record WineCreateRequest(
        String name,
        BigDecimal price
) {
}
