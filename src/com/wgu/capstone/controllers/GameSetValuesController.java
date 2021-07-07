package com.wgu.capstone.controllers;

import com.wgu.capstone.ListHelper;
import com.wgu.capstone.Main;
import com.wgu.capstone.views.CharacterValuesView;
import com.wgu.capstone.views.GameSetActionsView;
import com.wgu.capstone.views.GameSetCharactersView;
import com.wgu.capstone.views.MainTemplate;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;
import static j2html.TagCreator.attrs;

public class GameSetValuesController {

   private static List<List<String>> actions = null;
   private static List<List<String>> actionValues = null;
   private static List<List<String>> characters = null;
   private static List<List<String>> characterValues = null;

   public static List<List<String>> getActions() {
       int count = Main.jdbi.withHandle(
           handle -> handle.createQuery("SELECT COUNT(*) as count FROM Actions").mapTo(Integer.class).list().get(0)
       );
       if (actions == null || count != actions.size()) {
           GameSetValuesController.actions = Main.jdbi.withHandle(
               handle -> handle.createQuery("SELECT a.id, a.name, (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magic' END) as category, t.name as type FROM Actions a inner join Types t on a.type_id = t.id")
                   .map(((rs, context) ->
                       Arrays.asList(
                           rs.getString("id"),
                           rs.getString("name"),
                           rs.getString("type"),
                           rs.getString("category")
                       )
                   )).list()
           );
       }
       return actions;
   }
   public static List<List<String>> getActionValues() {
       int count = Main.jdbi.withHandle(
           handle -> handle.createQuery("SELECT COUNT(*) as count FROM ActionValues").mapTo(Integer.class).list().get(0)
       );
       if (actionValues == null || count != actionValues.size()) {
           GameSetValuesController.actionValues = Main.jdbi.withHandle(
               handle -> handle.createQuery("SELECT * FROM ActionValues")
                   .map(((rs, context) ->
                       Arrays.asList(
                           rs.getString("id"),
                           rs.getString("damage"),
                           rs.getString("accuracy"),
                           rs.getString("effect")
                       )
                   )).list()
           );
       }
       return actionValues;
   }
   public static List<List<String>> getCharacters() {
       int count = Main.jdbi.withHandle(
           handle -> handle.createQuery("SELECT COUNT(*) as count FROM Characters").mapTo(Integer.class).list().get(0)
       );
       if (characters == null || count != characters.size()) {
           GameSetValuesController.characters = Main.jdbi.withHandle(
               handle -> handle.createQuery("SELECT a.id, a.name, t.name as type FROM Characters a inner join Types t on a.type_id = t.id")
                   .map(((rs, context) ->
                       Arrays.asList(
                           rs.getString("id"),
                           rs.getString("name"),
                           rs.getString("type")
                       )
                   )).list()
           );
       }
       return characters;
   }
   public static List<List<String>> getCharacterValues() {
       int count = Main.jdbi.withHandle(
           handle -> handle.createQuery("SELECT COUNT(*) as count FROM CharacterValues").mapTo(Integer.class).list().get(0)
       );
       if (characters == null || count != characters.size()) {
           GameSetValuesController.characterValues = Main.jdbi.withHandle(
               handle -> handle.createQuery("SELECT * FROM CharacterValues")
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
                   )).list()
           );
       }
       return characterValues;
   }
   public static void createRoutes(Javalin app) {
       getActions();
       getActionValues();
       getCharacters();
       getCharacterValues();
       app.get("/game-set-values/:gameset_id", ctx -> {
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 10 * (page - 1);
                    List<String> gameSet = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT gs.id,
                                 gs.name
                                 FROM GameSets gs 
                                 WHERE gs.id = :gameset_id
                            """)
                            .bind("gameset_id", ctx.pathParam("gameset_id"))
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
                             INNER JOIN ActionValues av ON av.id = gsa.actionvalue_id
                             WHERE gsa.gameset_id = :gameset_id 
                             LIMIT 10 OFFSET :offset
                            """)
                            .bind("gameset_id", ctx.pathParam("gameset_id"))
                            .bind("offset", offset)
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

                List<List<String>> gameSetCharactersData = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT 
                                 gsc.id,
                                 c.name, 
                                 t.name as type, 
                                 cv.hp,
                                 cv.phy_attack,
                                 cv.phy_defense,
                                 cv.mag_attack,
                                 cv.mag_defense,
                                 cv.speed
                                 FROM GameSetCharacters gsc
                                 INNER JOIN Characters c ON c.id = gsc.character_id 
                                 INNER JOIN Types t ON t.id = c.type_id 
                                 INNER JOIN CharacterValues cv ON cv.id = gsc.charactervalue_id
                                 WHERE gsc.gameset_id = :gameset_id 
                                 LIMIT 10 OFFSET :offset
                                """)
                            .bind("gameset_id", ctx.pathParam("gameset_id"))
                            .bind("offset", offset)
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("type"),
                                    rs.getString("hp"),
                                    rs.getString("phy_attack"),
                                    rs.getString("phy_defense"),
                                    rs.getString("mag_attack"),
                                    rs.getString("mag_defense"),
                                    rs.getString("speed")
                                )
                            ))
                            .list() ;
                    }
                );
                int countActions = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                            SELECT COUNT(*) as count FROM GameSetActions
                                            WHERE gameset_id = :gameset_id """)
                            .bind("gameset_id", ctx.pathParam("gameset_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("count")
                                )
                            ))
                            .list().get(0).get(0);
                    }
                ));
                int countCharacters = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                            SELECT COUNT(*) as count FROM GameSetCharacters
                                            WHERE gameset_id = :gameset_id
                                        """)
                            .bind("gameset_id", ctx.pathParam("gameset_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("count")
                                )
                            ))
                            .list().get(0).get(0);
                    }
                ));
                ctx.html(
                    MainTemplate.mainView("game-sets",
                        GameSetActionsView.getPartial(gameSet.get(0), "Game Set Actions: " +  gameSet.get(1), Arrays.asList("Id", "Name", "Category", "Type", "Damage", "Accuracy", "Effect"), gameSetActionsData, page, countActions),
                        GameSetCharactersView.getPartial(gameSet.get(0), "Game Set Characters: " + gameSet.get(1), Arrays.asList("Id", "Name", "Type", "HP", "Phy Atk", "Mag Atk", "Phy Def", "Mag Def", "Spd"), gameSetCharactersData, page, countCharacters)
                    )
                );
            }
       );
       app.get("/game-set-actions/:gameset_id/create", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("gameset_id")).withName("gameset_id"),
                nestedMultiSelectFormControl("Actions", getActions().stream().collect(
                        Collectors.toMap(
                            (List<String> item) -> item.get(1) +
                                span(attrs(".badge.rounded-pill.bg-primary.ms-2"), item.get(2)).render() +
                                span(attrs(".badge.rounded-pill.bg-secondary.ms-2"), item.get(3)).render(),
                            (List<String> item) -> item.get(0)
                        )
                    )
                ),
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
