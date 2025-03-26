package com.talimere.vinlotteri.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ballots")
public class Ballot {

    public Ballot() {}

    public Ballot(Integer number) {
        this.number = number;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    @ManyToOne
    @JoinColumn(name = "lottery_id", nullable = false)
    private Lottery lottery;

    @ManyToOne
    @JoinColumn(name = "holder_id")
    private Holder holder;

    @OneToOne(mappedBy = "winningBallot")
    private Wine wonWine;

    public Long getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Lottery getLottery() {
        return lottery;
    }

    public void setLottery(Lottery lottery) {
        this.lottery = lottery;
    }

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public Wine getWonWine() {
        return wonWine;
    }

    public void setWonWine(Wine wonWine) {
        this.wonWine = wonWine;
    }
}
