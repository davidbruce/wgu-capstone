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

                List<String> action = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT a.id,
                                 a.name || ' ' ||  t.name || ' ' ||
                                 (CASE a.category WHEN 0 THEN 'Physical' ELSE 'Magical'END) as name
                                 FROM Actions a inner join Types t on a.type_id = t.id
                                 WHERE a.id =
                            """ + ctx.pathParam("action_id"))
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
                            WHERE av.action_id =
                            """ + ctx.pathParam("action_id"))
                            .map(((rs, context) ->
                                Arrays.asList(
                                    rs.getString("id"),
                                    rs.getString("damage"),
                                    rs.getString("accuracy"),
                                    rs.getString("effect")
                                )
                            ))
                            .list() ;
                    }
                );
                ctx.html(
                    ActionValuesView.getHtml(action.get(0), action.get(1), Arrays.asList("Id", "Damage", "Accuracy", "Effect"), data)
                );
            }
        );
        app.get("/action-values/:action_id/create", ctx -> ctx.html(
            form(
                input().withType("hidden").withValue(ctx.pathParam("action_id")).withName("action_id"),
                textFormControl("Damage", "Amount of damage the move will do."),
                textFormControl("Accuracy", "50 to 100 value with -1 for never miss."),
                textFormControl("Effect", "DSL String for different possible effects. Example: MA:U1"),
                button(attrs(".btn"), "Submit").withType("submit")
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
    }

}