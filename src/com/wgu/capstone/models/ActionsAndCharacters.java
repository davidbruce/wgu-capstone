package com.wgu.capstone.models;

import java.util.List;

public class ActionsAndCharacters {
    public List<Action> actions;
    public List<Character> characters;
    public int maxActionTestCount = 1;
    public int maxCharacterTestCount = 1;

    public ActionsAndCharacters(List<Action> actions, List<Character> characters) {
        this.actions = actions;
        this.characters = characters;
    }
}
