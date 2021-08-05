package com.wgu.capstone.controllers;

    import com.wgu.capstone.Main;
    import com.wgu.capstone.views.ActionValuesView;
    import io.javalin.Javalin;

    import java.util.Arrays;
    import java.util.List;

    import static com.wgu.capstone.views.FormTemplate.*;
    import static j2html.TagCreator.*;

public class ActionValuesController {
    public static void createRoutes(Javalin app) {
        app.get("/action-values/:action_id", ctx -> {
                int page = 1;
                if (ctx.queryParam("page") != null) {
                    page = Integer.parseInt(ctx.queryParam("page"));
                }
                int offset = 15 * (page - 1);
                List<String> action = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT a.id,
                                 a.name
                                 FROM Actions a inner join Types t on a.type_id = t.id
                                 WHERE a.id = :action_id
                            """)
                            .bind("action_id", ctx.pathParam("action_id"))
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
                             av.id, av.damage, av.accuracy, av.effect 
                            FROM ActionValues av 
                            WHERE av.action_id = :action_id
                            LIMIT 15 OFFSET :offset
                            """)
                            .bind("action_id", ctx.pathParam("action_id"))
                            .bind("offset", offset)
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("damage"),
                                    rs.getString("accuracy"),
                                    rs.getString("effect")
                                )
                            ))
                            .list();
                    }
                );
                int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                SELECT COUNT(*) as count FROM ActionValues WHERE action_id = :action_id
                            """)
                            .bind("action_id", ctx.pathParam("action_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("count")
                                )
                            ))
                            .list().get(0).get(0);
                    }
                ));
                ctx.html(
                    ActionValuesView.getHtml(action.get(0), "Action: " + action.get(1), Arrays.asList("Id", "Damage", "Accuracy", "Effect"), data, page, count)
                );
            }
        );
        app.get("/action-values/:action_id/create", ctx -> ctx.html(
            form(
                textFormControl("Damage", "Amount of damage the move will do."),
                textFormControl("Accuracy", "50 to 100 value with -1 for never miss."),
                textFormControl("Effect", "DSL String for different possible effects. Example: MA:U1"),
                button(attrs(".btn"), "Submit").withType("submit"),
                cancelFormButton("Cancel")
            ).withAction("/action-values/" + ctx.pathParam("action_id") + "/create")
                .withMethod("post")
                .attr("hx-boost", "true")
                .render()
        ));
        app.post("/action-values/:action_id/create", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("insert into ActionValues (action_id, damage, accuracy, effect) values (:action_id,:damage,:accuracy,:effect)")
                    .bind("action_id", Integer.parseInt(ctx.pathParam("action_id")))
                    .bind("damage", Integer.parseInt(ctx.formParam("damage")))
                    .bind("accuracy", Integer.parseInt(ctx.formParam("accuracy")))
                    .bind("effect", ctx.formParam("effect"))
                    .execute();
                return null;
            });
            ctx.redirect("/action-values/" + ctx.pathParam("action_id"));
        });
        app.get("/action-values/:action_id/update/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                        SELECT * FROM ActionValues WHERE id = :id
                        """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("damage"),
                                rs.getString("accuracy"),
                                rs.getString("effect")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Damage", "Amount of damage the move will do.", data.get(0)),
                    textFormControl("Accuracy", "50 to 100 value with -1 for never miss.", data.get(1)),
                    textFormControl("Effect", "DSL String for different possible effects. Example: MA:U1", data.get(2)),
                    button(attrs(".btn"), "Submit Edit").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/action-values/" + ctx.pathParam("action_id") + "/update/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/action-values/:action_id/update/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Update ActionValues set damage = :damage, accuracy = :accuracy, effect = :effect where id = :id")
                    .bind("id", Integer.parseInt(ctx.pathParam("id")))
                    .bind("damage", Integer.parseInt(ctx.formParam("damage")))
                    .bind("accuracy", Integer.parseInt(ctx.formParam("accuracy")))
                    .bind("effect", ctx.formParam("effect"))
                    .execute();
                return null;
            });
            ctx.redirect("/action-values/" + ctx.pathParam("action_id"));
        });
        app.get("/action-values/:action_id/delete/:id", ctx -> {
            List<String> data = Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery("""
                        SELECT * FROM ActionValues WHERE id = :id
                        """)
                        .bind("id", ctx.pathParam("id"))
                        .map(((rs, context) ->
                            Arrays.asList(
                                rs.getString("damage"),
                                rs.getString("accuracy"),
                                rs.getString("effect")
                            )
                        ))
                        .list().get(0);
                }
            );
            ctx.html(
                form(
                    textFormControl("Damage", "Amount of damage the move will do.", data.get(0), true),
                    textFormControl("Accuracy", "50 to 100 value with -1 for never miss.", data.get(1), true),
                    textFormControl("Effect", "DSL String for different possible effects. Example: MA:U1", data.get(2), true),
                    button(attrs(".btn"), "Confirm Delete").withType("submit"),
                    cancelFormButton("Cancel")
                ).withAction("/action-values/" + ctx.pathParam("action_id") + "/delete/" + ctx.pathParam("id"))
                    .withMethod("post")
                    .attr("hx-boost", "true")
                    .render()
            );
        });
        app.post("/action-values/:action_id/delete/:id", ctx -> {
            Main.jdbi.withHandle(handle -> {
                handle.createUpdate("Delete from ActionValues where id = :id")
                    .bind("id", Integer.parseInt(ctx.pathParam("id")))
                    .execute();
                return null;
            });
            ctx.redirect("/action-values/" + ctx.pathParam("action_id"));
        });
    }
}