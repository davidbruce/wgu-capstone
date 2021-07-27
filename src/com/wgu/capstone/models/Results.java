package com.wgu.capstone.models;

import java.util.List;

public class Results {
    public CharacterWithActions winner;
    public CharacterWithActions loser;
    public List<Turn> turns;

    public Results() {};

    public Results(CharacterWithActions winner, CharacterWithActions loser, List<Turn> turns) {
        this.winner = winner;
        this.loser = loser;
        this.turns= turns;
    }
}
