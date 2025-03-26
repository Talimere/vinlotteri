package com.talimere.vinlotteri.controller;

import com.talimere.vinlotteri.dto.HolderCreateRequest;
import com.talimere.vinlotteri.dto.HolderDto;
import com.talimere.vinlotteri.service.LotteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holder")
public class HolderController {

    private final LotteryService lotteryService;

    public HolderController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping
    public ResponseEntity<List<HolderDto>> getHolders() {
        return ResponseEntity.ok(lotteryService.getHolders().stream().map(HolderDto::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HolderDto> getHolder(@PathVariable Long id) {
        return lotteryService.getHolder(id)
                .map(HolderDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HolderDto> createHolder(@RequestBody HolderCreateRequest request) {
        return ResponseEntity.ok(
                new HolderDto(lotteryService.createHolder(request.name()))
        );
    }
}
