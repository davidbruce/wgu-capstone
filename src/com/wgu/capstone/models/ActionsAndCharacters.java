package com.wgu.capstone.models;

import java.util.List;

public class ActionsAndCharacters {
    public List<Action> actions;
    public List<Character> characters;

    public ActionsAndCharacters(List<Action> actions, List<Character> characters) {
        this.actions = actions;
        this.characters = characters;
    }
}
