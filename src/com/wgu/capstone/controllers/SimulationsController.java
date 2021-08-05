package com.wgu.capstone.controllers;

import com.wgu.capstone.Main;
import com.wgu.capstone.views.ActionsView;
import com.wgu.capstone.views.MainTemplate;
import com.wgu.capstone.views.SimulationsView;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.text;

public class SimulationsController {
    public static void createRoutes(Javalin app) {
        app.get("/game-set-simulations/:gameset_id", ctx -> {
            int page = 1;
            if (ctx.queryParam("page") != null) {
                page = Integer.parseInt(ctx.queryParam("page"));
            }
            int offset = 15 * (page - 1);
            List<String> gameSet = Main.jdbi.withHandle(
                    handle -> {
                        return handle.createQuery("""
                                SELECT gs.id,
                                 gs.name
                                 FROM GameSets gs 
                                 WHERE gs.id = :gameset_id
                            """)
                                .bind("gameset_id", ctx.pathParam("gameset_id"))
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
                                WITH simulation_results as (
                                  SELECT s.id, c.name, t.name as type, sc.winner, sc.endHP, s.created,
                                  (SELECT COUNT(*) FROM SimulationTurns st WHERE st.simulation_id  = s.id) as turns 
                                  FROM Simulations s
                                  INNER JOIN SimulationCharacters sc ON sc.simulation_id = s.id
                                  INNER JOIN GameSetCharacters gsc ON sc.gameSetCharacter_id = gsc.id 
                                  INNER JOIN Characters c ON gsc.character_id = c.id 
                                  INNER JOIN Types t on c.type_id = t.id
                                  WHERE s.gameset_id = :gameset_id
                                )
                                SELECT 
                                sr1.id,
                                STRFTIME('%m-%d-%Y %H:%M', sr1.created) as created, 
                                CASE sr1.winner
                                    WHEN 1 THEN sr1.name
                                    ELSE sr2.name
                                END as winner,
                                CASE sr1.winner
                                    WHEN 1 THEN sr1.type
                                    ELSE sr2.type
                                END as winner_type,
                                CASE sr1.winner
                                    WHEN 1 THEN sr1.endHP
                                    ELSE sr2.endHP
                                END as winner_hp,
                                CASE sr1.winner
                                    WHEN 0 THEN sr1.name
                                    ELSE sr2.name 
                                END as loser,
                                CASE sr1.winner
                                    WHEN 0 THEN sr1.type
                                    ELSE sr2.type 
                                END as loser_type,
                                CASE sr1.winner
                                    WHEN 0 THEN sr1.endHP
                                    ELSE sr2.endHP 
                                END as loser_hp,
                                sr1.turns
                                from simulation_results sr1
                                INNER JOIN simulation_results sr2 ON sr1.id = sr2.id
                                GROUP BY sr1.id
                                LIMIT 15 OFFSET :offset
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
                SimulationsView.getHtml(ctx.pathParam("gameset_id"), gameSet.get(1), Arrays.asList("Id", "Created", "Winner", "Type", "HP", "Loser", "Type", "HP", "Turns"), data, page, count)
            );
        });
    }
}
