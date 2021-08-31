package com.wgu.capstone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wgu.capstone.Main;
import com.wgu.capstone.views.GameSetNavView;
import com.wgu.capstone.views.MainTemplate;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.TreeMap;

import static com.wgu.capstone.views.FormTemplate.selectFormControl;
import static j2html.TagCreator.*;

public class AnalysisController {
    public static void createRoutes(Javalin app) {
        app.get("/game-set-analysis/:gameset_id", ctx -> {
            int games = Main.jdbi.withHandle(
                    handle ->
                            handle.createQuery("""
                              SELECT COUNT(*) FROM Simulations where gameset_id = :gameset_id 
                               """)
                                    .bind("gameset_id", ctx.pathParam("gameset_id"))
                                    .mapTo(Integer.class)
                                    .one()
            );
            int turns = Main.jdbi.withHandle(
                    handle ->
                            handle.createQuery("""
                                          SELECT COUNT(*) FROM Simulations s --number of turns
                                        INNER JOIN SimulationTurns st ON st.simulation_id = s.id 
                                        WHERE s.gameset_id = :gameset_id 
                                        GROUP BY gameset_id
                                   """)
                                    .bind("gameset_id", ctx.pathParam("gameset_id"))
                                    .mapTo(Integer.class)
                                    .one()
            );
            int characters = Main.jdbi.withHandle(
                    handle ->
                            handle.createQuery("""
                                         Select COUNT(*) From GameSetCharacters gsc WHERE gameset_id = :gameset_id
                                   """)
                                    .bind("gameset_id", ctx.pathParam("gameset_id"))
                                    .mapTo(Integer.class)
                                    .one()
            );
            int actions = Main.jdbi.withHandle(
                    handle ->
                            handle.createQuery("""
                                          SELECT COUNT(*) FROM GameSetActions gsa WHERE gameset_id = :gameset_id
                                   """)
                                    .bind("gameset_id", ctx.pathParam("gameset_id"))
                                    .mapTo(Integer.class)
                                    .one()
            );
            ctx.html(
                    MainTemplate.mainView
                            ("game-sets",
                                    false,
                                    div(
                                            attrs(".rounded.p-3"),
                                            GameSetNavView.gameSetNav(GameSetValuesController.getGameSet(ctx).get(1), ctx.pathParam("gameset_id"), 2)
                                    ),
                                    div(
                                            attrs(".d-flex.mb-auto.fw-bold"),
                                            div(
                                                    attrs(".d-flex.col.rounded.p-3.me-3.justify-content-between"),
                                                    div("Games"),
                                                    div("" + games)
                                            ),
                                            div(
                                                    attrs(".d-flex.col.rounded.p-3.me-3.justify-content-between"),
                                                    div("Turns"),
                                                    div("" + turns)
                                            ),
                                            div(
                                                    attrs(".d-flex.col.rounded.p-3.me-3.justify-content-between"),
                                                    div("Characters"),
                                                    div("" + characters)
                                            ),
                                            div(
                                                    attrs(".d-flex.col.rounded.p-3.justify-content-between"),
                                                    div("Actions"),
                                                    div("" + actions)
                                            )
                                    ),
                                    div(

                                            attrs(".rounded.p-3"),
                                            div(
                                                    attrs(".d-flex.mb-auto.fw-bold.justify-content-between"),
                                                    div(
                                                            attrs("#chart-type.d-flex.me-3.justify-content-between"),
                                                            selectFormControl("Chart Type", new TreeMap<String, String>(){{
                                                                        put("1. Stat Analysis Scatter", "stat-analysis");
                                                                        put("2. Total Stats Analysis", "total-analysis");
                                                                        put("3. Fire Analysis", "fire-analysis");
                                                                        put("4. Water Analysis", "water-analysis");
                                                                        put("5. Earth Analysis", "earth-analysis");
                                                                        put("6. Wind Analysis", "wind-analysis");
                                                                        put("7. Light Analysis", "light-analysis");
                                                                        put("8. Dark Analysis", "dark-analysis");
                                                            }}
                                                            )
                                                    ),
                                                    div(
                                                            attrs("#chart-options.d-flex.me-3.justify-content-between"),
                                                            selectFormControl("Chart Options", new HashMap<String, String>(){{
                                                                put("Empty", "Empty");
                                                            }})
                                                    ),
                                                    div(
                                                            label("Toggle"),
                                                            div(
                                                                    attrs(".d-flex.justify-content-between"),
                                                                    div(
                                                                            button(attrs(".btn.me-2"), "Scatter Points").attr("onclick", "toggleAllScatters()")
                                                                    ),
                                                                    div(
                                                                            button(attrs(".btn"), "Regression Lines").attr("onclick", "toggleAllLines()")
                                                                    )
                                                            )
                                                    )
                                            ),
                                            div(
                                                    canvas(attrs("#regression"))
                                            ),
                                            div(attrs("#coefficients"))
                                    ),
                                    div(
                                            rawHtml("""
                                                   <script type="text/javascript">
                                                   var script = document.createElement('script')
                                                   script.src = '/analysis.js'
                                                   document.head.append(script); 
                                                   </script>
                       """)
                                    )
                            )
            );
        });
        app.get("/game-set-analysis/:gameset_id/stats-regression", ctx -> {
            Object data = Main.jdbi.withHandle(
                    handle ->
                            handle.createQuery("""
                          SELECT name, type, phy_attack, mag_attack, phy_defense, mag_defense, speed, hp, total_stats,
                           (phy_attack + mag_attack + speed) as offense,
                           (hp + phy_defense + mag_defense) as defense, 
                           (games_won * 1.0 / (games_won + games_lost)) * 100 as winrate FROM (
                            SELECT -- character type win rates 
                            c.name,
                            t.name as type,
                            phy_attack + mag_attack + phy_defense + mag_defense + speed + hp as total_stats,
                            phy_attack, mag_attack, phy_defense, mag_defense, speed, hp,
                            COUNT(sc.winner) FILTER (WHERE sc.winner = 1) as games_won,
                            COUNT(sc.winner) FILTER (WHERE sc.winner = 0) as games_lost 
                            FROM SimulationCharacters sc
                            INNER JOIN GameSetCharacters gsc ON sc.gameSetCharacter_id = gsc.id
                            INNER JOIN "Characters" c ON gsc.character_id = c.id
                            INNER JOIN CharacterValues cv ON gsc.charactervalue_id = cv.id
                            INNER JOIN Types t ON c.type_id = t.id
                            WHERE gsc.gameset_id = :gameset_id
                            GROUP BY c.name 
                            ) 
                                    """)
                                    .bind("gameset_id", ctx.pathParam("gameset_id"))
                                    .map(((rs, context) ->
                                            new HashMap<String, Object>() {
                                                {
                                                    put("name", rs.getString("name"));
                                                    put("type", rs.getString("type"));
                                                    put("total_stats", rs.getInt("total_stats"));
                                                    put("offense", rs.getInt("offense"));
                                                    put("defense", rs.getInt("defense"));
                                                    put("winrate", rs.getDouble("winrate"));
                                                    put("hp", rs.getString("hp"));
                                                    put("phy_attack", rs.getString("phy_attack"));
                                                    put("mag_attack", rs.getString("mag_attack"));
                                                    put("phy_defense", rs.getString("phy_defense"));
                                                    put("mag_defense", rs.getString("mag_defense"));
                                                    put("speed", rs.getString("speed"));
                                                }
                                            }
                                    ))
                                    .list()
            );
            ObjectMapper mapper = new ObjectMapper();
            ctx.json(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        });
    }
}
