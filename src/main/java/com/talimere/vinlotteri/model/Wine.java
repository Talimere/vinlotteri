package com.talimere.vinlotteri.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wines")
public class Wine {

    public Wine() {}

    public Wine(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    @OneToOne
    @JoinColumn(name = "winning_ballot_id", unique = true)
    private Ballot winningBallot;

    @ManyToOne
    @JoinColumn(name = "lottery_id")
    private Lottery lottery;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Ballot getWinningBallot() {
        return winningBallot;
    }

    public void setWinningBallot(Ballot winningBallot) {
        this.winningBallot = winningBallot;
    }

    public Lottery getLottery() {
        return lottery;
    }

    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }
}
