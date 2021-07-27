package com.wgu.capstone.models;

import java.util.List;

public class Character {
    public int gameSetCharacterId;
    public String name;
    public String type;
    public int currentHP;
    public int hp;
    public int phyAtk;
    public int magAtk;
    public int phyDef;
    public int magDef;
    public int speed;
    public int testCount;

    public Character() {}

    public Character(int gameSetCharacterId, String name, String type, int hp, int phyAtk, int magAtk, int phyDef, int magDef, int speed) {
        this.gameSetCharacterId = gameSetCharacterId;
        this.name = name;
        this.type = type;
        this.currentHP = hp;
        this.hp = hp;
        this.phyAtk = phyAtk;
        this.magAtk = magAtk;
        this.phyDef = phyDef;
        this.magDef = magDef;
        this.speed = speed;
    }
}