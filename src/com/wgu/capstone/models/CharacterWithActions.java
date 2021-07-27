package com.wgu.capstone.models;

import java.util.List;

public class CharacterWithActions {
    public Character character;
    public List<Action> actions;

    public CharacterWithActions() {}

    public CharacterWithActions(Character character, List<Action> actions) {
        this.character = character;
        this.actions = actions;
    }
}
