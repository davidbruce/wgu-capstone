package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.*;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class SimulationsController {
    public static boolean enableResults(int gameSetId) {
         return Main.jdbi.withHandle(handle ->  handle.createQuery("""
                SELECT COUNT(*) FROM Simulations WHERE gameset_id = :gameset_id 
               """)
                 .bind("gameset_id", gameSetId)
                 .mapTo(Integer.class).one()) <= 0;
    }

    public static void createRoutes(Javalin app) {
        app.get("/game-set-simulations/:gameset_id", ctx -> {
            int page = 1;
            if (ctx.queryParam("page") != null) {
                page = Integer.parseInt(ctx.queryParam("page"));
            }
            int offset = 30 * (page - 1);
            List<List<String>> data = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                       WITH simulation_results as (
                                         SELECT s.id, c.name, t.name as type, sc.winner, sc.endHP, s.created
                                         FROM Simulations s
                                         INNER JOIN SimulationCharacters sc ON sc.simulation_id = s.id
                                         INNER JOIN GameSetCharacters gsc ON sc.gameSetCharacter_id = gsc.id
                                         INNER JOIN Characters c ON gsc.character_id = c.id
                                         INNER JOIN Types t on c.type_id = t.id
                                         WHERE s.gameset_id = :gameset_id
                                         LIMIT 30 OFFSET :offset
                                       ),
                                       winners as (
                                         SELECT * FROM simulation_results WHERE winner = 1
                                       ),
                                       losers as (
                                         SELECT * FROM simulation_results WHERE winner = 0
                                       ), 
                                       turns as (
                                         SELECT st.simulation_id as  id, MAX(turn) as turns from SimulationTurns st
                                         INNER JOIN simulation_results sr ON st.simulation_id = sr.id
                                         GROUP BY st.simulation_id\s
                                       )
                                      SELECT
                                      w.id,
                                      STRFTIME('%m-%d-%Y %H:%M', w.created) as created,
                                      w.name as winner,
                                      w.type as winner_type,
                                      w.endHP as winner_hp,
                                      l.name as loser,
                                      l.type as loser_type,
                                      l.endHP as loser_hp,
                                      t.turns
                                      FROM winners w
                                        INNER JOIN losers l on l.id = w.id
                                        INNER JOIN turns t on t.id = w.id;
                                           
                                """)
                                .bind("gameset_id", ctx.pathParam("gameset_id"))
                                .bind("offset", offset)
                                .map(((rs, context) ->
                                        Arrays.asList(
                                                rs.getString("id"),
                                                rs.getString("created"),
                                                rs.getString("winner"),
                                                rs.getString("winner_type"),
                                                rs.getString("winner_hp"),
                                                rs.getString("loser"),
                                                rs.getString("loser_type"),
                                                rs.getString("loser_hp"),
                                                rs.getString("turns")
                                        )
                                ))
                                .list();
                    }
            );
            int count = Integer.parseInt(Main.jdbi.withHandle(
                    handle -> {
                        return handle.select("""
                                    SELECT COUNT(*) as count FROM Simulations 
                                    WHERE gameset_id = :gameset_id
                                """)
                                .bind("gameset_id", ctx.pathParam("gameset_id"))
                                .map(((rs, context) ->
                                        Arrays.asList(
                                                rs.getString("count")
                                        )
                                ))
                                .list().get(0).get(0);
                    }
            ));
            ctx.html(
                SimulationsView.getHtml(ctx.pathParam("gameset_id"), GameSetValuesController.getGameSet(ctx).get(1), Arrays.asList("Id", "Created", "Winner", "Type", "HP", "Loser", "Type", "HP", "Turns"), data, page, count)
            );
        });
        app.get("/game-set-simulations/:gameset_id/details/:simulation_id", ctx -> {
            List<List<String>> results = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                 SELECT  
                                  gsc.id, c.name, t.name as type, sc.endHP
                                  FROM Simulations s
                                  INNER JOIN SimulationCharacters sc ON sc.simulation_id = s.id
                                  INNER JOIN GameSetCharacters gsc ON sc.gameSetCharacter_id = gsc.id 
                                  INNER JOIN Characters c ON gsc.character_id = c.id 
                                  INNER JOIN Types t on c.type_id = t.id
                                  WHERE s.gameset_id = :gameset_id and s.id = :simulation_id
                                  ORDER BY sc.winner ASC
                            """)
                                .bind("gameset_id", ctx.pathParam("gameset_id"))
                                .bind("simulation_id", ctx.pathParam("simulation_id"))
                                .map(((rs, context) ->
                                        Arrays.asList(
                                                rs.getString("id"),
                                                rs.getString("name"),
                                                rs.getString("type"),
                                                rs.getString("endHP")
                                        )
                                ))
                                .list();
                    }
            );
            String loserID = results.get(0).get(0);
            String winnerID = results.get(1).get(0);
            List<List<String>> loser =  results.subList(0, 1).stream().map(list -> list.subList(1,4)).collect(Collectors.toList());
            List<List<String>> winner = results.subList(1, 2).stream().map(list -> list.subList(1,4)).collect(Collectors.toList());

            List<List<String>> loserValues = getCharacterValues(ctx, loserID);
            List<List<String>> winnerValues = getCharacterValues(ctx, winnerID);

            List<List<String>> actions =  Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery(
                                """
                                        SELECT 
                                        sc.gameSetCharacter_id,
                                        sa.gameSetAction_id,
                                        a.name,
                                        t.name as type,
                                        av.id,
                                        av.damage,
                                        av.effect,
                                        COUNT(st.turn) as turns 
                                        FROM SimulationCharacters sc
                                        INNER JOIN SimulationActions sa on sa.simulationcharacter_id = sc.id 
                                        INNER JOIN GameSetActions gsa on sa.gameSetAction_id = gsa.id 
                                        INNER JOIN Actions a on gsa.action_id = a.id
                                        INNER JOIN ActionValues av on gsa.actionvalue_id = av.id
                                        INNER JOIN Types t on a.type_id = t.id 
                                        LEFT JOIN SimulationTurns st on (sc.simulation_id = st.simulation_id AND st.gameSetAction_id = sa.gameSetAction_id) 
                                        WHERE sc.simulation_id = :simulation_id 
                                        GROUP BY sa.simulationcharacter_id, sa.gameSetAction_id
                                        ORDER BY sc.winner, a.name ASC;
                                        """).bind("simulation_id", ctx.pathParam("simulation_id"))
                                .map(((rs, context) ->
                                        Arrays.asList(
                                                rs.getString("name"),
                                                rs.getString("type"),
                                                rs.getString("id"),
                                                rs.getString("damage"),
                                                rs.getString("effect"),
                                                rs.getString("turns")
                                        )
                                ))
                                .list();
                    }
            );
            List<List<String>> turns =  Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery(
                                """
                                SELECT st.turn, c.name as character_name, a.name as action_name, st.damage, av.effect FROM SimulationTurns st
                                INNER JOIN GameSetActions gsa on st.gameSetAction_id = gsa.id
                                INNER JOIN Actions a on gsa.action_id = a.id
                                INNER JOIN ActionValues av on gsa.actionvalue_id = av.id
                                INNER JOIN GameSetCharacters gsc on st.gameSetCharacter_id = gsc.id
                                INNER JOIN Characters c on gsc.character_id = c.id
                                WHERE simulation_id = :simulation_id;
                                """).bind("simulation_id", ctx.pathParam("simulation_id"))
                                .map(((rs, context) ->
                                        Arrays.asList(
                                                rs.getString("turn"),
                                                rs.getString("character_name"),
                                                rs.getString("action_name"),
                                                rs.getString("damage"),
                                                rs.getString("effect")
                                        )
                                ))
                                .list();
                    }
            );
            ctx.html(
                    MainTemplate.mainView("game-sets",
                            false,
                            div(
                                    attrs(".rounded.p-3"),
                                    GameSetNavView.gameSetNav(GameSetValuesController.getGameSet(ctx).get(1), ctx.pathParam("gameset_id"), 1)
                            ),
                            div(
                                    attrs(".d-flex.mb-auto"),
                                    div(
                                            attrs(".col.me-3"),
                                            div(
                                                   attrs(".rounded.p-3"),
                                                    TableTemplate.tableHeader("Winner", null, false),
                                                    TableTemplate.tableBody(Arrays.asList("Name", "Type", "End HP"), winner, null, null, 0)
                                            ),
                                            div(
                                                    attrs(".rounded.p-3.mt-3"),
                                                    TableTemplate.tableBody(Arrays.asList("Value ID", "HP", "Phy Atk", "Mag Atk", "Phy Def", "Mag Def", "Spd"), winnerValues, null, null, 0)
                                            ),
                                            div(
                                                    attrs(".actions-list.rounded.p-3.mt-3"),
                                                    TableTemplate.tableBody(Arrays.asList("Name", "Type", "Value ID", "Damage", "Effect", "Uses"), actions.subList(4, 8), null, null, 0)

                                            )
                                    ),
                                    div(
                                            attrs(".col.mw-3"),
                                            div(
                                                    attrs(".rounded.p-3"),
                                                    TableTemplate.tableHeader("Loser", null, false),
                                                    TableTemplate.tableBody(Arrays.asList("Name", "Type", "End HP"), loser, null, null, 0)
                                            ),
                                            div(
                                                    attrs(".rounded.p-3.mt-3"),
                                                    TableTemplate.tableBody(Arrays.asList("Value ID", "HP", "Phy Atk", "Mag Atk", "Phy Def", "Mag Def", "Spd"), loserValues, null, null, 0)
                                            ),
                                            div(
                                                    attrs(".actions-list.rounded.p-3.mt-3"),
                                                    TableTemplate.tableBody(Arrays.asList("Name", "Type", "Value ID", "Damage", "Effect", "Uses"), actions.subList(0, 4), null, null, 0)
                                            )
                                    )
                            ),
                            div(
                                    attrs(".rounded.p-3"),
                                    TableTemplate.tableHeader("Turns", null, false),
                                    TableTemplate.tableBody(Arrays.asList("Turn", "Character", "Action", "Damage", "Effect"), turns, null, null, 0)
                            )
                    )
            );
        });
    }

    private static List<List<String>> getCharacterValues(Context ctx, String id) {
        return Main.jdbi.withHandle(
                handle -> {
                    return handle.createQuery(
                            """
                                    SELECT cv.* FROM SimulationCharacters sc
                                    INNER JOIN GameSetCharacters gsc on sc.gameSetCharacter_id = gsc.id
                                    INNER JOIN CharacterValues cv on gsc.charactervalue_id = cv.id
                                    WHERE simulation_id = :simulation_id
                                    AND gsc.id = :id 
                                    """
                    )
                            .bind("simulation_id", ctx.pathParam("simulation_id"))
                            .bind("id", id)
                            .map(((rs, context) ->
                                    Arrays.asList(
                                            rs.getString("id"),
                                            rs.getString("hp"),
                                            rs.getString("phy_attack"),
                                            rs.getString("mag_attack"),
                                            rs.getString("phy_defense"),
                                            rs.getString("mag_defense"),
                                            rs.getString("speed")
                                    )
                            ))
                            .list();
                }
        );
    }

}
