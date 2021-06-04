package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.CharacterValuesView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class CharacterValuesController {
    public static void createRoutes(Javalin app) {
        app.get("/character-values/:character_id", ctx -> {

                List<String> character = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT c.id,
                                 c.name || ' ' ||  t.name  as name
                                 FROM Characters c inner join Types t on c.type_id = t.id
                                 WHERE c.id =
                            """ + ctx.pathParam("character_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name")
                                )
                            ))
                            .list().get(0);
                    }
                );
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT 
                             cv.*
                            FROM CharacterValues cv 
                            WHERE cv.character_id =
                            """ + ctx.pathParam("character_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("hp"),
                                    rs.getString("phy_attack"),
                                    rs.getString("mag_attack"),
                                    rs.getString("phy_defense"),
                                    rs.getString("mag_defense"),
                                    rs.getString("speed")
                                )
                            ))
                            .list() ;
                    }
                );
                ctx.html(
                    CharacterValuesView.getHtml(character.get(0), character.get(1), Arrays.asList("Id", "HP", "Physical Attack", "Magic Attack", "Physical Defense", "Magic Defense", "Speed"), data)
                );
            }
        );
        app.get("/character-values/:character_id/create", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("character_id")).withName("character_id"),
                textFormControl("HP", "Amount of Health the character has."),
                textFormControl("Physical Attack", "Used for calculating damage dealt by Physical moves."),
                textFormControl("Magic Attack", "Used for calculating damage dealt by Magical moves."),
                textFormControl("Physical Defense", "Used for calculating damage received by Physical moves."),
                textFormControl("Magic Defense", "Used for calculating damage received by Magical moves."),
                textFormControl("Speed", "Used for calculating turn order"),
                button(attrs(".btn"), "Submit").withType("submit")
            ).withAction("/character-values/" + ctx.pathParam("character_id") + "/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.get("/character-values/:character_id/update", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("character_id")).withName("character_id"),
                textFormControl("HP", "Amount of Health the character has."),
                textFormControl("Physical Attack", "Used for calculating damage dealt by Physical moves."),
                textFormControl("Magic Attack", "Used for calculating damage dealt by Magical moves."),
                textFormControl("Physical Defense", "Used for calculating damage received by Physical moves."),
                textFormControl("Magic Defense", "Used for calculating damage received by Magical moves."),
                textFormControl("Speed", "Used for calculating turn order"),
                button(attrs(".btn"), "Submit").withType("submit")
            ).withAction("/character-values/" + ctx.pathParam("character_id") + "/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/character-values/:character_id/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into CharacterValues (character_id, phy_attack, mag_attack, phy_defense, mag_defense, speed) values (:character_id, :phy_attack, :mag_attack, :phy_defense, :mag_defense, :speed)")
                    .bind("character_id", Integer.parseInt(ctx.pathParam("character_id")))
                    .bind("hp", Integer.parseInt(ctx.formParam("hp")))
                    .bind("phy_attack", Integer.parseInt(ctx.formParam("physical_attack")))
                    .bind("mag_attack", Integer.parseInt(ctx.formParam("magic_attack")))
                    .bind("phy_defense", Integer.parseInt(ctx.formParam("physical_defense")))
                    .bind("mag_defense", Integer.parseInt(ctx.formParam("magic_defense")))
                    .bind("speed", Integer.parseInt(ctx.formParam("speed")))
                    .execute();
                return null;
            });
            ctx.redirect("/character-values/" + ctx.pathParam("character_id"));
        });
    }

}
