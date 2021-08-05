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
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 15 * (page - 1);
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("SELECT a.id, a.name, t.name as type FROM Characters a inner join Types t on a.type_id = t.id LIMIT 15 OFFSET :offset")
                            .bind("offset", offset)
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
                int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                        SELECT COUNT(*) as count FROM Characters
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
                    CharactersView.getHtml(Arrays.asList("Id", "Name", "Type"), data, page, count)
                );
            }
        );
        app.get("/characters/create", ctx -> ctx.html(
            form(
                textFormControl("Name", "Name of the move."),
                selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0)))),
                button(attrs(".btn"), "Submit").withType("submit"),
                cancelFormButton("Cancel")
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
        app.get("/characters/update/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("SELECT * FROM Characters where id = :id")
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("name"),
                                rs.getString("type_id")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
            form(
                textFormControl("Name", "Name of the move.", data.get(0)),
                selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0))), data.get(1)),
                button(attrs(".btn"), "Submit Edit").withType("submit"),
                cancelFormButton("Cancel")
            ).withAction("/characters/update/" + ctx.pathParam("id"))
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
            );
        });
        app.post("/characters/update/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Update Characters SET name = :name, type_id = :type_id where id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .bind("name", ctx.formParam("name"))
                    .bind("type_id", Integer.parseInt(ctx.formParam("type")))
                    .execute();
                return null;
            });
            ctx.redirect("/characters");
        });
        app.get("/characters/delete/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("SELECT * FROM Characters where id = :id")
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("name"),
                                rs.getString("type_id")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the move.", data.get(0), true),
                    selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0))), data.get(1), true),
                    button(attrs(".btn"), "Confirm Delete").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/characters/delete/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/characters/delete/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Delete from Characters where id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .execute();
                return null;
            });
            ctx.redirect("/characters");
        });

    }

}