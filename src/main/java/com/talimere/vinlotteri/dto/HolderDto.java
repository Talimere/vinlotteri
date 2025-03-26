package com.talimere.vinlotteri.dto;

import com.talimere.vinlotteri.model.Holder;

public record HolderDto(
        Long id,
        String name
) {
    public HolderDto(Holder holder) {
        this(
                holder.getId(),
                holder.getName()
        );
    }
}
