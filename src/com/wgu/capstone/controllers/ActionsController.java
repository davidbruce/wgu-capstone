package com.wgu.capstone.controllers;

import com.wgu.capstone.views.ActionsView;
import io.javalin.Javalin;

public class ActionsController {
    public static void createRoutes(Javalin app) {
        app.get("/actions", ctx -> ctx.html(
            ActionsView.getHtml()
        ));
    }
}
