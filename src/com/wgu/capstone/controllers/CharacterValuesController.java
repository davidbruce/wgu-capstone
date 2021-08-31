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
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 15 * (page - 1);
                List<String> character = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT c.id,
                                 c.name
                                 FROM Characters c inner join Types t on c.type_id = t.id
                                 WHERE c.id = :character_id
                            """)
                            .bind("character_id", ctx.pathParam("character_id"))
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
                            WHERE cv.character_id = :character_id
                            LIMIT 15 OFFSET :offset
                            """)
                            .bind("character_id", ctx.pathParam("character_id"))
                            .bind("offset", offset)
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
                            .list();
                    }
                );
                int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                        SELECT COUNT(*) as count FROM CharacterValues where character_id = :character_id
                                    """)
                            .bind("character_id", ctx.pathParam("character_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("count")
                                )
                            ))
                            .list().get(0).get(0);
                    }
                ));
                ctx.html(
                    CharacterValuesView.getHtml(character.get(0), "Character: " + character.get(1), Arrays.asList("Id", "HP", "Physical Attack", "Magic Attack", "Physical Defense", "Magic Defense", "Speed"), data, page, count)
                );
            }
        );
        app.get("/character-values/:character_id/create", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("character_id")).withName("character_id"),
                textFormControl("HP", "Amount of Health the character has.", true),
                textFormControl("Physical Attack", "Used for calculating damage dealt by Physical moves.", true),
                textFormControl("Magic Attack", "Used for calculating damage dealt by Magical moves.", true),
                textFormControl("Physical Defense", "Used for calculating damage received by Physical moves.", true),
                textFormControl("Magic Defense", "Used for calculating damage received by Magical moves.", true),
                textFormControl("Speed", "Used for calculating turn order", true),
                button(attrs(".btn"), "Submit").withType("submit"),
                cancelFormButton("Cancel")
            ).withAction("/character-values/" + ctx.pathParam("character_id") + "/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/character-values/:character_id/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into CharacterValues (character_id, hp, phy_attack, mag_attack, phy_defense, mag_defense, speed) values (:character_id, :hp, :phy_attack, :mag_attack, :phy_defense, :mag_defense, :speed)")
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
        app.get("/character-values/:character_id/update/:id", ctx -> {
            int page = 1;
            if (ctx.queryParam("page") != null) {
                page = Integer.parseInt(ctx.queryParam("page"));
            }
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                            SELECT 
                             cv.*
                            FROM CharacterValues cv 
                            WHERE id = :id 
                            """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("hp"),
                                rs.getString("phy_attack"),
                                rs.getString("mag_attack"),
                                rs.getString("phy_defense"),
                                rs.getString("mag_defense"),
                                rs.getString("speed")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("HP", "Amount of Health the character has.", true, data.get(0)),
                    textFormControl("Physical Attack", "Used for calculating damage dealt by Physical moves.", true, data.get(1)),
                    textFormControl("Magic Attack", "Used for calculating damage dealt by Magical moves.", true, data.get(2)),
                    textFormControl("Physical Defense", "Used for calculating damage received by Physical moves.", true, data.get(3)),
                    textFormControl("Magic Defense", "Used for calculating damage received by Magical moves.", true, data.get(4)),
                    textFormControl("Speed", "Used for calculating turn order", true, data.get(5)),
                    button(attrs(".btn"), "Submit Edit").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/character-values/" + ctx.pathParam("character_id") + "/update/" + ctx.pathParam("id") + "?page=" + page)
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/character-values/:character_id/update/:id", ctx -> {
            int page = 1;
            if (ctx.queryParam("page") != null) {
                page = Integer.parseInt(ctx.queryParam("page"));
            }
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Update CharacterValues Set hp = :hp, phy_attack = :phy_attack, mag_attack = :mag_attack, phy_defense = :phy_defense, mag_defense = :mag_defense, speed = :speed where id = :id")
                    .bind("id", Integer.parseInt(ctx.pathParam("id")))
                    .bind("hp", Integer.parseInt(ctx.formParam("hp")))
                    .bind("phy_attack", Integer.parseInt(ctx.formParam("physical_attack")))
                    .bind("mag_attack", Integer.parseInt(ctx.formParam("magic_attack")))
                    .bind("phy_defense", Integer.parseInt(ctx.formParam("physical_defense")))
                    .bind("mag_defense", Integer.parseInt(ctx.formParam("magic_defense")))
                    .bind("speed", Integer.parseInt(ctx.formParam("speed")))
                    .execute();
                return null;
            });
            ctx.redirect("/character-values/" + ctx.pathParam("character_id") + "?page=" + page);
        });
        app.get("/character-values/:character_id/delete/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                            SELECT 
                             cv.*
                            FROM CharacterValues cv 
                            WHERE id = :id 
                            """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("hp"),
                                rs.getString("phy_attack"),
                                rs.getString("mag_attack"),
                                rs.getString("phy_defense"),
                                rs.getString("mag_defense"),
                                rs.getString("speed")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("HP", "Amount of Health the character has.", data.get(0), true),
                    textFormControl("Physical Attack", "Used for calculating damage dealt by Physical moves.", data.get(1), true),
                    textFormControl("Magic Attack", "Used for calculating damage dealt by Magical moves.", data.get(2), true),
                    textFormControl("Physical Defense", "Used for calculating damage received by Physical moves.", data.get(3), true),
                    textFormControl("Magic Defense", "Used for calculating damage received by Magical moves.", data.get(4), true),
                    textFormControl("Speed", "Used for calculating turn order", data.get(5), true),
                    button(attrs(".btn"), "Confirm Delete").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/character-values/" + ctx.pathParam("character_id") + "/delete/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/character-values/:character_id/delete/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Delete from CharacterValues where id = :id")
                    .bind("id", Integer.parseInt(ctx.pathParam("id")))
                    .execute();
                return null;
            });
            ctx.redirect("/character-values/" + ctx.pathParam("character_id"));
        });
    }

}
