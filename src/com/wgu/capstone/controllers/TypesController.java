package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.ActionsView;
import com.wgu.capstone.views.TypesView;
import io.javalin.Javalin;
import org.jdbi.v3.core.generic.GenericType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

public class TypesController {

    public static void createRoutes(Javalin app) {
        app.get("/types", ctx -> ctx.html(
            TypesView.getHtml(Arrays.asList("Id", "Name"), Main.types)
        ));
    }
}
