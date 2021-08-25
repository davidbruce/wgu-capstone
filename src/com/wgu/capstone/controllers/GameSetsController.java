package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.GameSetsView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class GameSetsController {

    public static int getFavorite() {
       return Main.jdbi.withHandle(handle ->  handle.createQuery("""
                select CASE WHEN exists(SELECT 1 FROM GameSets gs INNER JOIN Simulations s on s.gameset_id = gs.id WHERE favorite = 1) = 1
                    THEN (SELECT id FROM GameSets gs WHERE favorite = 1) 
                    ELSE 0
                    END
               """).mapTo(Integer.class).one());
    }

    public static void createRoutes(Javalin app) {
        app.get("/game-sets", ctx -> {
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 15 * (page - 1);
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT gs.* FROM GameSets gs
                            LIMIT 15 OFFSET :offset
                            """)
                            .bind("offset", offset)
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("favorite").equals("0") ? "" : "True"
                                )
                            ))
                            .list();
                    }
                );
                int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                        SELECT COUNT(*) as count FROM GameSets 
                                    """)
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("count")
                                )
                            ))
                            .list().get(0).get(0);
                    }
                ));
                ctx.html(
                    GameSetsView.getHtml(Arrays.asList("Id", "Name", "Favorite"), data, page, count)
                );
            }
        );
        app.get("/game-sets/create", ctx -> ctx.html(
            form(
                textFormControl("Name", "Name of the Game Set."),
                checkFormControl("favorite", new TreeMap<>(Map.of("Favorite", "1" )), null, false),
                button(attrs(".btn"), "Submit").withType("submit"),
                cancelFormButton("Cancel")
            ).withAction("/game-sets/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/game-sets/create", ctx -> {

            Main.jdbi.withHandle(handle -> {
                if (ctx.formParam("favorite") != null) {
                    handle.createUpdate("Update GameSets SET favorite = 0").execute();
                }
                handle.createUpdate("insert into GameSets (name, favorite) values (:name, :favorite)")
                    .bind("name", ctx.formParam("name"))
                    .bind("favorite", ctx.formParam("favorite"))
                    .execute();
                return null;
            });
            ctx.redirect("/game-sets");
        });
        app.get("/game-sets/update/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                            SELECT gs.* FROM GameSets gs where id = :id
                            """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("name"),
                                rs.getString("favorite")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the Game Set.", data.get(0)),
                    checkFormControl("favorite", new TreeMap<>(Map.of("Favorite", "1" )), data.get(1), false),
                    button(attrs(".btn"), "Submit Edit").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/game-sets/update/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/game-sets/update/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                String favorite = "0";
                if (ctx.formParam("favorite") != null) {
                    favorite = ctx.formParam("favorite");
                    handle.createUpdate("Update GameSets SET favorite = 0").execute();
                }

                handle.createUpdate("Update GameSets SET name = :name, favorite = :favorite where id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .bind("name", ctx.formParam("name"))
                    .bind("favorite", favorite)
                    .execute();
                return null;
            });
            ctx.redirect("/game-sets");
        });
        app.get("/game-sets/delete/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                            SELECT gs.* FROM GameSets gs where id = :id
                            """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("name"),
                                rs.getString("favorite")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the Game Set.", data.get(0), true),
                    checkFormControl("favorite", new TreeMap<>(Map.of("Favorite", "1" )), data.get(1), true),
                    button(attrs(".btn"), "Submit Edit").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/game-sets/delete/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/game-sets/delete/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Delete from GameSets where id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .execute();
                return null;
            });
            ctx.redirect("/game-sets");
        });
    }

}