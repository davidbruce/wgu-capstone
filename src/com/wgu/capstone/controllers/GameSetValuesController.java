package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.CharacterValuesView;
import com.wgu.capstone.views.GameSetActionsView;
import com.wgu.capstone.views.GameSetCharactersView;
import com.wgu.capstone.views.MainTemplate;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;

import static com.wgu.capstone.views.FormTemplate.selectFormControl;
import static com.wgu.capstone.views.FormTemplate.textFormControl;
import static j2html.TagCreator.*;
import static j2html.TagCreator.attrs;

public class GameSetValuesController {
   public static void createRoutes(Javalin app) {
        app.get("/game-set-values/:gameset_id", ctx -> {

                List<String> gameSet = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT gs.id,
                                 gs.name
                                 FROM GameSets gs 
                                 WHERE gs.id =
                            """ + ctx.pathParam("gameset_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name")
                                )
                            ))
                            .list().get(0);
                    }
                );
                List<List<String>> gameSetActionsData = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT 
                             gsa.id,
                             a.name, 
                             (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magical'END) as category,
                             t.name as type, 
                             av.damage,
                             av.accuracy,
                             av.effect 
                             FROM GameSetActions gsa
                             INNER JOIN Actions a ON a.id = gsa.action_id 
                             INNER JOIN Types t ON t.id = a.type_id 
                             INNER JOIN ActionValues av ON a.id = gsa.actionvalue_id
                             WHERE gsa.gameset_id = 
                            """ + ctx.pathParam("gameset_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("category"),
                                    rs.getString("type"),
                                    rs.getString("damage"),
                                    rs.getString("accuracy"),
                                    rs.getString("effect")
                                )
                            ))
                            .list() ;
                    }
                );
                ctx.html(
                    MainTemplate.mainView("game-sets",
                        GameSetActionsView.getPartial(gameSet.get(0), gameSet.get(1), Arrays.asList("Id", "Name", "Category", "Type", "Damage", "Accuracy", "Effect"), gameSetActionsData),
                        GameSetCharactersView.getPartial(gameSet.get(0), gameSet.get(1), Arrays.asList("Id", "Name", "Category", "Type", "Damage", "Accuracy", "Effect"), gameSetActionsData)
                    )
                );
            }
        );
        app.get("/game-set-actions/:gameset_id/create", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("gameset_id")).withName("gameset_id"),
                selectFormControl("Actions", null),
                button(attrs(".btn"), "Submit").withType("submit")
            ).withAction("/game-set-values/" + ctx.pathParam("gameset_id") + "/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/game-set-values/:gameset_id/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into CharacterValues (character_id, phy_attack, mag_attack, phy_defense, mag_defense, speed) values (:character_id, :phy_attack, :mag_attack, :phy_defense, :mag_defense, :speed)")
                    .bind("character_id", Integer.parseInt(ctx.pathParam("character_id")))
                    .bind("hp", Integer.parseInt(ctx.formParam("hp")))
                    .bind("phy_attack", Integer.parseInt(ctx.formParam("phy_attack")))
                    .bind("mag_attack", Integer.parseInt(ctx.formParam("mag_attack")))
                    .bind("phy_defense", Integer.parseInt(ctx.formParam("phy_defense")))
                    .bind("mag_defense", Integer.parseInt(ctx.formParam("mag_defense")))
                    .bind("speed", Integer.parseInt(ctx.formParam("speed")))
                    .execute();
                return null;
            });
            ctx.redirect("/game-set-values/" + ctx.pathParam("gameset_id"));
        });
    }

}
