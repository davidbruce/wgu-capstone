package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.ActionsView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.wgu.capstone.views.FormTemplate.*;
import static j2html.TagCreator.*;

public class ActionsController {
    public static void createRoutes(Javalin app) {
        app.get("/actions", ctx -> {
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 15 * (page - 1);
                List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT a.id, a.name, (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magic' END) as category, t.name as type, (SELECT COUNT(*) FROM ActionValues av WHERE av.action_id = a.id) as vals FROM Actions a inner join Types t on a.type_id = t.id
                            LIMIT 15 OFFSET :offset
                            """)
                            .bind("offset", offset)
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
                int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                    SELECT COUNT(*) as count FROM Actions
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
                    ActionsView.getHtml(Arrays.asList("Id", "Name", "Type", "Category", "Values"), data, page, count)
                );
            }
        );
        app.get("/actions/create", ctx -> ctx.html(
                form(
                    textFormControl("Name", "Name of the move."),
                    selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0)))),
                    radioFormControl("category", new TreeMap<>(Map.of("Physical Attack", "0", "Magic Attack", "1"))),
                    button(attrs(".btn"), "Submit").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/actions/create")
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
        ));
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
        app.get("/actions/update/:id", ctx -> {
                List<String> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                            SELECT a.id, a.name, a.category, a.type_id FROM Actions a WHERE a.id = :id 
                            """)
                            .bind("id", ctx.pathParam("id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("name"),
                                    rs.getString("type_id"),
                                    rs.getString("category")
                                )
                            ))
                            .list().get(0);
                    }
                );
                ctx.html(
                    form(
                        textFormControl("Name", "Name of the move.", data.get(1)),
                        selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0))), data.get(2)),
                        radioFormControl("category", new TreeMap<>(Map.of("Physical Attack", "0", "Magic Attack", "1")), data.get(3)),
                        button(attrs(".btn"), "Submit Edit").withType("submit"),
                        cancelFormButton("Cancel")
                    ).withAction("/actions/update/" + ctx.pathParam("id"))
                        .withMethod("post")
                        .attr("hx-boost", "true")
                        .render()
                );
        });
        app.post("/actions/update/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("update Actions SET name = :name, type_id = :type_id, category = :category where id = :id")
                    .bind("name", ctx.formParam("name"))
                    .bind("type_id", Integer.parseInt(ctx.formParam("type")))
                    .bind("category", ctx.formParam("category"))
                    .bind("id", ctx.pathParam("id"))
                    .execute();
                return null;
            });
            ctx.redirect("/actions");
        });
        app.get("/actions/delete/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                            SELECT a.id, a.name, a.category, a.type_id FROM Actions a WHERE a.id = 
                            """ + ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("id"),
                                rs.getString("name"),
                                rs.getString("type_id"),
                                rs.getString("category")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Name", "Name of the move.", data.get(1), true),
                    selectFormControl("Type", Main.types.stream().collect(Collectors.toMap((List<String> item) -> item.get(1), (List<String> item) -> item.get(0))), data.get(2), true),
                    radioFormControl("category", new TreeMap<>(Map.of("Physical Attack", "0", "Magic Attack", "1")), data.get(3), true),
                    button(attrs(".btn"), "Confirm Delete").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/actions/delete/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/actions/delete/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("DELETE FROM Actions WHERE id = :id")
                    .bind("id", ctx.pathParam("id"))
                    .execute();
                return null;
            });
            ctx.redirect("/actions");
        });
    }

}