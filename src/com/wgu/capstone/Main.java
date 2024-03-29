package com.wgu.capstone;

import com.wgu.capstone.controllers.*;
import com.wgu.capstone.views.MainTemplate;
import io.javalin.Javalin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import static j2html.TagCreator.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    public static Jdbi jdbi = null;
    public static List<List<String>> types = null;
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
                                                    category integer not null default 0 check(category IN (0,1)),
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
                                                    hp integer,
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


    static void simulations(Javalin app) {
        app.get("/simulations", ctx -> ctx.html(
            MainTemplate.mainView("Simulations", div("actions!"))));
    }

    static void game(Javalin app) {
        app.get("/game", ctx -> ctx.html(
            MainTemplate.mainView("game", div("game"))));
    }

    private static void setupControllers(Javalin app) {
        ActionsController.createRoutes(app);
        ActionValuesController.createRoutes(app);
        TypesController.createRoutes(app);
        CharactersController.createRoutes(app);
        CharacterValuesController.createRoutes(app);
        GameSetsController.createRoutes(app);
        GameSetValuesController.createRoutes(app);
        SimulationsController.createRoutes(app);
        AnalysisController.createRoutes(app);
        DashboardController.createRoutes(app);
        actionSets(app);
    }

    public static void main(String[] args) {
        String dbPath = System.getProperty("databasePath") + "/capstone.db";
        System.out.println("DB Path: " + dbPath);

        Javalin app = Javalin.create(
            config -> {
                config.addStaticFiles("/public");
            }
        ).start(7001);

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            setupDB(connection);
            jdbi = Jdbi.create(connection).installPlugin(new SQLitePlugin());
            types = jdbi.withHandle(
                handle -> {
                    return handle.createQuery("Select * from Types")
                        .map(((rs, ctx) -> Arrays.asList(rs.getString("id"), rs.getString("name"))))
                        .list();
                }
            );
            setupControllers(app);
            Display display = new Display();
            final Shell shell = new Shell(display);
            shell.setText("WGU Capstone");
            shell.setImage(new Image(display, System.getProperty("iconFile")));
            shell.setLayout(new GridLayout(1, false));

            final Browser browser;
            try {
                browser = new Browser(shell, SWT.EDGE);
            } catch (SWTError e) {
                System.out.println("Could not instantiate Browser: " + e.getMessage());
                display.dispose();
                return;
            }
            browser.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

            shell.setSize(1300, 800);
            shell.open();
            browser.setUrl("http://localhost:7001/game-sets/");
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
            display.dispose();
            System.exit(0);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }
}