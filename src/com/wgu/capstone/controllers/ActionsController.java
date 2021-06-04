package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.ActionsView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class ActionsController {
    public static void createRoutes(Javalin app) {
        app.get("/actions", ctx -> {
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("SELECT a.id, a.name, (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magic' END) as category, t.name as type, (SELECT COUNT(*) FROM ActionValues av WHERE av.action_id = a.id) as vals FROM Actions a inner join Types t on a.type_id = t.id")
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("type"),
                                    rs.getString("category"),
                                    rs.getString("vals")
                                )
                            ))
                            .list();
                    }
                );
                ctx.html(
                    ActionsView.getHtml(Arrays.asList("Id", "Name", "Type", "Category", "Values"), data)
                );
            }
        );
        app.get("/actions/create", ctx -> ctx.html(
                form(
                    textFormControl("Name", "Name of the move."),
                    selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0)))),
                    radioFormControl("category", Map.of("Physical Attack", "0", "Magic Attack", "1")),
                    button(attrs(".btn"), "Submit").withType("submit")
                ).withAction("/actions/create")
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
        ));
        app.get("/actions/update/:id", ctx -> {
                List<String> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT a.id, a.name, (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magical' END) as category, t.name as type FROM Actions a inner join Types t on a.type_id = t.id
                            AND a.id = 
                            """ + ctx.pathParam("id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("type"),
                                    rs.getString("category")
                                )
                            ))
                            .list().get(0);
                    }
                );
                ctx.html(
                    form(
                        input().withType("hidden").withValue(ctx.pathParam("id")).withName("id"),
                        textFormControl("Name", "Name of the move.", data.get(1)),
                        selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0))), data.get(2)),
                        radioFormControl("category", Map.of("Physical Attack", "0", "Magic Attack", "1"), data.get(3)),
                        button(attrs(".btn"), "Submit").withType("submit")
                    ).withAction("/actions/update/" + ctx.pathParam("id"))
                        .withMethod("post")
                        .attr("hx-boost", "true")
                        .render()
                );
        });
        app.post("/actions/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
              handle.createUpdate("insert into Actions (name, type_id, category) values (:name,:type_id,:category)")
                .bind("name", ctx.formParam("name"))
                .bind("type_id", Integer.parseInt(ctx.formParam("type")))
                .bind("category", ctx.formParam("category"))
                .execute();
              return null;
            });
            ctx.redirect("/actions");
        });
    }

}