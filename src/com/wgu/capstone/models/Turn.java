package com.wgu.capstone.models;

public class Turn {
    public int player; //gameSetCharacterId
    public int action; //gameSetActionId
    public int damage;

    public Turn() {}

    public Turn(int player, int action, int damage) {
        this.player = player;
        this.action = action;
        this.damage = damage;
    }
}
