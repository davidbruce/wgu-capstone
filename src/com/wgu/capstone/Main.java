package com.wgu.capstone;

import com.wgu.capstone.controllers.ActionsController;
import com.wgu.capstone.views.ActionsView;
import com.wgu.capstone.views.MainTemplate;
import io.javalin.Javalin;
import j2html.tags.ContainerTag;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;

import static j2html.TagCreator.*;

import java.sql.*;

public class Main {
    static void setupDB(Connection connection) throws SQLException {
            //check if database has been generated
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("""
                                                          SELECT name FROM sqlite_master WHERE type='table' AND name='Actions';
                                                      """);
            //if not create tables
            if (!rs.next()) {
                statement.executeUpdate("""
                                                create table Types (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    name string,
                                                    created datetime default current_timestamp,
                                                    modified datetime
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table Actions (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    name string,
                                                    type_id integer,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(type_id) REFERENCES Types(id)
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table ActionValues (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    action_id integer,
                                                    damage integer,
                                                    accuracy integer,
                                                    effect string,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(action_id) REFERENCES Actions(id)
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table Characters (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    name string,
                                                    type_id integer,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(type_id) REFERENCES Types(id)
                                                )
                                            """);

                statement.executeUpdate("""
                                                create table CharacterValues (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    character_id integer,
                                                    phy_attack integer,
                                                    mag_attack integer,
                                                    phy_defense integer,
                                                    mag_defense integer,
                                                    speed integer,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(character_id) REFERENCES Characters(id)
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table GameSets (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    name string,
                                                    favorite boolean DEFAULT false,
                                                    created datetime default current_timestamp,
                                                    modified datetime
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table GameSetActions (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    gameset_id integer,
                                                    action_id integer,
                                                    actionvalue_id integer,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(action_id) REFERENCES Actions(id),
                                                    FOREIGN KEY(actionvalue_id) REFERENCES ActionValues(id)
                                                )
                                            """);
                statement.executeUpdate("""
                                                create table GameSetCharacters (
                                                    id integer PRIMARY KEY AUTOINCREMENT,
                                                    gameset_id integer,
                                                    character_id integer,
                                                    charactervalue_id integer,
                                                    created datetime default current_timestamp,
                                                    modified datetime,
                                                    FOREIGN KEY(character_id) REFERENCES Characters(id),
                                                    FOREIGN KEY(charactervalue_id) REFERENCES CharacterValues(id)
                                                )
                                            """);

            }
    }

    static void actionSets(Javalin app) {
        app.get("/action-sets", ctx -> ctx.html(
            MainTemplate.mainView("action-sets", div())));
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



    static void simulations(Javalin app) {
        app.get("/simulations", ctx -> ctx.html(
            MainTemplate.mainView("Simulations", div("actions!"))));
    }

    static void game(Javalin app) {
        app.get("/game", ctx -> ctx.html(
            MainTemplate.mainView("game", div("game"))));
    }

    private static void setupControllers(Javalin app) {
        app.get("/", ctx -> ctx.html(
            MainTemplate.mainView("home", div("Home!"))));
        ActionsController.createRoutes(app);
        actionSets(app);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create(
            config -> {
                config.addStaticFiles("/public");
            }
        ).start(7001);

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:capstone.db");
            setupDB(connection);
            Jdbi jdbi = Jdbi.create(connection).installPlugin(new SQLitePlugin());


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

        setupControllers(app);
    }
}