package com.talimere.vinlotteri.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "holders")
public class Holder {

    public Holder() {}

    public Holder(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "holder")
    private List<Ballot> ballots;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ballot> getBallots() {
        return ballots;
    }

    public void setBallots(List<Ballot> ballots) {
        this.ballots = ballots;
    }
}
