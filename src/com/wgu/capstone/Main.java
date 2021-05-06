package com.wgu.capstone;

import io.javalin.Javalin;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

import static j2html.TagCreator.*;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class Main {
    static void setupDB() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:capstone.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            //check if database has been generated
            ResultSet rs = statement.executeQuery("""
                                                          SELECT name FROM sqlite_master WHERE type='table' AND name='Actions';
                                                      """);
            //if not create tables
            if (!rs.next()) {
                statement.executeUpdate("""
                                                create table Actions (
                                                 id integer PRIMARY KEY AUTOINCREMENT,
                                                 name string,
                                                 type string,
                                                 damage integer,
                                                 accuracy integer,
                                                 effect string,
                                                 created datetime default current_timestamp,
                                                 modified datetime
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table CharacterTemplates (
                                                 id integer PRIMARY KEY AUTOINCREMENT,
                                                 name string,
                                                 phy_attack integer,
                                                 mag_attack integer,
                                                 phy_defense integer,
                                                 mag_defense integer,
                                                 speed integer,
                                                 created datetime default current_timestamp,
                                                 modified datetime
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table ActionSets (
                                                id integer PRIMARY KEY AUTOINCREMENT,
                                                name string,
                                                favorite boolean DEFAULT false,
                                                created datetime default current_timestamp,
                                                modified datetime
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table ActionSets_Join (
                                                id integer PRIMARY KEY AUTOINCREMENT,
                                                action_id integer,
                                                actionset_id integer,
                                                created datetime default current_timestamp,
                                                modified datetime,
                                                FOREIGN KEY(action_id) REFERENCES Actions(id)
                                                FOREIGN KEY(actionset_id) REFERENCES ActionSets(id)
                                                )
                                            """);

            }
//            statement.executeUpdate("insert into person values(1, 'leo')");
//            statement.executeUpdate("insert into person values(2, 'yui')");
//            rs = statement.executeQuery("select * from person");
//            while(rs.next())
//            {
//                 read the result set
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("id = " + rs.getInt("id"));
//            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    static ContainerTag sidebarLi(String attrs, String icon, String text, String href) {
        return li(
                a(
                    attrs(attrs + ".d-flex.justify-content-center"),
                    div(
                        attrs(".wrapper"),
                        i(attrs(".bi.me-2." + icon)),
                        text(text)
                    )
                ).withHref(href)
        );
    }

    static ContainerTag sidebar(String route) {
        return div(
            attrs("#sidebar.d-flex.flex-column.p-3.bg-white.shadow"),
            a(
                attrs(".d-flex.align-items-center.me-3.link-dark.text-decoration-none"),
                img(attrs(".bi.me-2"))
                    .withSrc("wgu-logo.svg")
                    .attr("width", 100)
                    .attr("height", 22),
                span(attrs(".fs-4"), "Capstone")
            ),
            hr(),
            ul(attrs(".nav.nav-pills.flex-column.mb-auto.px-2"),
               sidebarLi(".nav-link.link-dark" + iffElse(route == "home" , ".active", ".border"), "bi-speedometer2", "Dashboard", "/"),
               sidebarLi(".nav-link.link-dark" + iffElse(route == "actions" , ".active", ".border"), "bi-table", "Action", "/actions"),
               sidebarLi(".nav-link.link-dark" + iffElse(route == "simulations" , ".active", ".border"), "bi-cpu", "Simulations", "/simulations"),
               sidebarLi(".nav-link.link-dark" + iffElse(route == "game" , ".active", ".border"), "bi-controller", "Play Testing", "/game")
            ).attr("hx-boost", "true"),
            hr(),
            div(
                attrs(".d-flex.align-items-center.text-decorations-none"),
                img(attrs(".rounded-circle.ms-4.me-3"))
                    .attr("width", 32)
                    .attr("height", 32)
                    .withSrc("profile.jpg"),
                strong("David Bruce")
            )
        ).withStyle("min-width: 200px");
    }

    static String mainView(String route, Tag... main) {
        return document(html(
            head(
                title("WGU Capstone"),
                meta().withCharset("utf-8"),
                link().withRel("shortcut icon").withType("icon/x-icon").withHref("favicon.ico"),
                link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css"),
                link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"),
                link().withRel("stylesheet").withHref("stylesheet.css")
            ),
            body(
                attrs(".bg-light"),
                sidebar(route),
                div(
                    attrs(".container"),
                    div(
                        attrs(".row"),
                        div(
                            attrs(".p-4"),
                            div(
                                attrs(".shadow.bg-white.rounded.p-3"),
                                main
                            )
                        )
                    )
                )
            ),
            script().withSrc("https://unpkg.com/htmx.org@1.3.3"),
            script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js")
        ));
    }

    static ContainerTag bsTable(List<String> headers, List<List<String>> values) {
        return table(
            attrs(".table.table-sm"),
            thead(
                tr(
                    each(headers, header -> th(header).attr("scope", "col"))
                )
            ),
            tbody(
                each(values, row ->
                    tr(
                        th(String.valueOf(row.get(0))),
                        each(row.subList(1, row.size()), col -> td(col))
                    )
                )
            )
        );
    }

    static void actionSets(Javalin app) {
        app.get("/action-sets", ctx -> ctx.html(
            mainView("action-sets", div())));
    }

    static ContainerTag crudTableHeader(String title) {
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight.mb-3"),
            div(attrs(".p-2.bd-highlight"), title),
            div(attrs(".p-2.bd-highlight"), "Paging"),
            div(attrs(".p-2.bd-highlight"), "Create Update Button")
        );
    }

    static ContainerTag paging() {
        return div();
    }

    static void actions(Javalin app) {
        app.get("/actions", ctx -> ctx.html(
            mainView("actions",
                div(
                    attrs(".d-flex.justify-content-between.bd-highlight.mb-3"),
                    h4(attrs(".p-2.bd-highlight"), "Actions"),
                    div(
                        attrs(".p-2.d-flex.justify-content-center"),
                        button(attrs(".btn.border.me-2"), "Create"),
                        button(attrs(".btn.border.me-2.disabled"), "Update"),
                        button(attrs(".btn.border.disabled"), "Delete")
                    )
                ),
                div(
                    bsTable(Arrays.asList("#", "First", "Last", "Handle"), Arrays.asList(Arrays.asList("1", "Mark", "Otto", "@mdo")))
                ),
                div(
                     attrs(".d-flex.justify-content-between.bd-highlight"),
                     nav(
                        attrs(".p-2.bd-highlight"),
                        ul(
                            attrs(".pagination"),
                            rawHtml("""
                                        <li class="page-item me-2 active"><a class="page-link link-dark" href="#">1</a></li>
                                        <li class="page-item me-2"><a class="page-link link-dark" href="#">2</a></li>
                                        <li class="page-item me-2"><a class="page-link link-dark" href="#">3</a></li>
                            """)
                        ).withStyle("margin-bottom: 0")
                     ).attr("aria-label", "Page Navigation for labels"),
                     nav(
                         attrs(".p-2.bd-highlight"),
                         ul(
                             attrs(".pagination"),
                             rawHtml("""
                                        <li class="page-item me-2"><a class="page-link link-dark" href="#">Previous</a></li>
                                        <li class="page-item me-2"><a class="page-link link-dark" href="#">Next</a></li>
                            """)
                         ).withStyle("margin-bottom: 0")

                     ).attr("aria-label", "Page Navigation for labels")
                )
            )
        ));
    }

    static void simulations(Javalin app) {
        app.get("/simulations", ctx -> ctx.html(
            mainView("Simulations", div("actions!"))));
    }

    static void game(Javalin app) {
        app.get("/game", ctx -> ctx.html(
            mainView("game", div("game"))));
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create(
            config -> {
                config.addStaticFiles("/public");
            }
        ).start(7001);
        setupDB();

        app.get("/", ctx -> ctx.html(
            mainView("home", div("Home!"))));
        actions(app);
        actionSets(app);
    }
}