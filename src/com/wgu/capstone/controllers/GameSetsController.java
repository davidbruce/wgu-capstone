package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.GameSetsView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class GameSetsController {
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
                                    rs.getString("name")
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
                    GameSetsView.getHtml(Arrays.asList("Id", "Name"), data, page, count)
                );
            }
        );
        app.get("/game-sets/create", ctx -> ctx.html(
            form(
                textFormControl("Name", "Name of the Game Set."),
                button(attrs(".btn"), "Submit").withType("submit"),
                cancelFormButton("Cancel")
            ).withAction("/game-sets/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/game-sets/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into GameSets (name) values (:name)")
                    .bind("name", ctx.formParam("name"))
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
                                rs.getString("name")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the Game Set.", data.get(0)),
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
                handle.createUpdate("Update GameSets SET name = :name where id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .bind("name", ctx.formParam("name"))
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
                                rs.getString("name")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the Game Set.", data.get(0), true),
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