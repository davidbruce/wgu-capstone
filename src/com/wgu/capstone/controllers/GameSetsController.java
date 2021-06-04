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
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT gs.* FROM GameSets gs
                            """)
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name")
                                )
                            ))
                            .list();
                    }
                );
                ctx.html(
                    GameSetsView.getHtml(Arrays.asList("Id", "Name"), data)
                );
            }
        );
        app.get("/game-sets/create", ctx -> ctx.html(
            form(
                textFormControl("Name", "Name of the Game Set."),
                button(attrs(".btn"), "Submit").withType("submit")
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
    }

}