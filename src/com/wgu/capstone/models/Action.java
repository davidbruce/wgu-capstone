package com.wgu.capstone.models;

public class Action {
    public int gameSetActionId;
    public String name;
    public String type;
    public int damage;
    public int accuracy;
    public int category;
    public String effect;
    public int useCount;
    public int testCount;

    public Action() {}

    public Action(int gameSetActionId, String name, String type, int damage, int accuracy, int category, String effect) {
        this.gameSetActionId = gameSetActionId;
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.accuracy = accuracy;
        this.category = category;
        this.effect = effect;
    }
}

