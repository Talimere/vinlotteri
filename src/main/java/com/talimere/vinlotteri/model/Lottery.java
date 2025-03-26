package com.talimere.vinlotteri.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lotteries")
public class Lottery {

    public Lottery() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private boolean raffled = false;

    @OneToMany(mappedBy = "lottery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ballot> ballots = new ArrayList<>();

    @OneToMany(mappedBy = "lottery", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wine> wines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = Objects.requireNonNull(date, "Date cannot be null");
    }

    public boolean isRaffled() {
        return raffled;
    }

    public void setRaffled(boolean raffled) {
        if (raffled && this.date != null && this.date.isAfter(LocalDate.now())) {
            throw new IllegalStateException("Future lotteries cannot be marked as raffled");
        }
        this.raffled = raffled;
    }

    public List<Ballot> getBallots() {
        return ballots;
    }

    public void setBallots(List<Ballot> ballots) {
        this.ballots = new ArrayList<>(ballots);
        ballots.forEach(b -> b.setLottery(this));
    }

    public List<Wine> getWines() {
        return wines;
    }

    public void setWines(List<Wine> wines) {
        this.wines = new ArrayList<>(wines);
        wines.forEach(w -> w.setLottery(this));
    }

    public void addWine(Wine wine) {
        wine.setLottery(this);
        this.wines.add(wine);
    }
}
