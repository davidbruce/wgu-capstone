package com.wgu.capstone.models;

public class Action {
    public int gameSetActionId;
    public String name;
    public String type;
    public int damage;
    public int category;
    public String effect;
    public int testCount;

    public Action(int gameSetActionId, String name, String type, int damage, int category, String effect) {
        this.gameSetActionId = gameSetActionId;
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.category = category;
        this.effect = effect;
    }
}

