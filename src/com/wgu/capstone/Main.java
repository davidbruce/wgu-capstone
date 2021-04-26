package com.wgu.capstone;

import io.javalin.Javalin;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    static void setupDB() {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next())
            {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    static ContainerTag sidebarLi(String attrs, String icon, String text, String href) {
        return li(
            a(
                attrs(attrs),
                i(attrs(".bi.ms-4.me-3." + icon)),
                text(text)
            ).withHref(href)
        );
    }

    static ContainerTag sidebar() {
        return div(
            attrs("#sidebar.d-flex.flex-column.p-3.bg-white.shadow"),
            a(
                attrs(".d-flex.align-items-center.ms-4.me-3.link-dark.text-decoration-none"),
                img(attrs(".bi.me-2"))
                    .withSrc("wgu-logo.svg")
                    .attr("width", 100)
                    .attr("height", 22),
                span(attrs(".fs-4"), "Capstone")
            ),
            hr(),
            ul(attrs(".nav.nav-pills.flex-column.mb-auto"),
               sidebarLi(".nav-link.active", "bi-speedometer2", "Dashboard", "/"),
               sidebarLi(".nav-link.link-dark", "bi-table", "Action Sets", "/actionSets"),
               sidebarLi(".nav-link.link-dark", "bi-cpu", "Simulations", "/simulations"),
               sidebarLi(".nav-link.link-dark", "bi-controller", "Play Testing", "/game")
            ),
            hr(),
            div(
                attrs(".d-flex.align-items-center.text-decorations-none"),
                img(attrs(".rounded-circle.ms-4.me-3"))
                    .attr("width", 32)
                    .attr("height", 32)
                    .withSrc("profile.jpg"),
                strong("David Bruce")
            )
        ).withStyle("min-width: 280px");
    }

    static String mainView(ContainerTag main) {
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
                sidebar(),
                div(
                    attrs(".container"),
                    div(
                        attrs(".row"),
                        div(
                            attrs(".p-4"),
                            div(
                                attrs(".shadow.bg-white"),
                                main
                            )
                        )
                    )
                )
            ),
            script().withSrc("https://unpkg.com/htmx.org@1.3.3"),
            script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"),
            script(rawHtml("""
                       document.querySelector('.nav').querySelectorAll("li").forEach(function(item) {
                          item.addEventListener('mouseenter', function() {
                            this.classList.add('shadow');
                          });

                          item.addEventListener('mouseleave', function() {
                            this.classList.remove('shadow');
                          });
                        });
                        """
                   )
            )
        ));
    }

    static void actionSets(Javalin app) {
        app.get("/action-sets", ctx -> ctx.html(
                mainView(div("actions!"))));
    }

    static void actions(Javalin app) {
        app.get("/actions", ctx -> ctx.html(
                mainView(div("actions!"))));
    }

    static void simulations(Javalin app) {
        app.get("/simulations", ctx -> ctx.html(
                mainView(div("actions!"))));
    }

    static void game(Javalin app) {
        app.get("/game", ctx -> ctx.html(
                mainView(div("game"))));
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create(
                config -> {
                    config.addStaticFiles("/public");
                }
        ).start(7001);
        setupDB();

        app.get("/", ctx -> ctx.html(
               mainView(div("Home!"))));
        actionSets(app);
    }
}