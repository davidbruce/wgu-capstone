package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.ActionsView;
import com.wgu.capstone.views.CharactersView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class CharactersController {
    public static void createRoutes(Javalin app) {
        app.get("/characters", ctx -> {
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("SELECT a.id, a.name, t.name as type FROM Characters a inner join Types t on a.type_id = t.id")
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("type")
                                )
                            ))
                            .list();
                    }
                );
                ctx.html(
                    CharactersView.getHtml(Arrays.asList("Id", "Name", "Type"), data)
                );
            }
        );
        app.get("/characters/create", ctx -> ctx.html(
            form(
                textFormControl("Name", "Name of the move."),
                selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0)))),
                button(attrs(".btn"), "Submit").withType("submit")
            ).withAction("/characters/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/characters/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into Characters (name, type_id) values (:name,:type_id)")
                    .bind("name", ctx.formParam("name"))
                    .bind("type_id", Integer.parseInt(ctx.formParam("type")))
                    .execute();
                return null;
            });
            ctx.redirect("/characters");
        });
    }

}