package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.text;

public class SimulationsView {

    public static String getHtml(String gameSetId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
                ("game-sets",
                        GameSetNavView.gameSetNav(title, gameSetId, 1),
                        div(
                                TableTemplate.tableHeader("Simulations", "", false, page, count, 15, "/game-set-simulations/" + gameSetId),
                                TableTemplate.tableBody(headers, data, "/game-set-simulations/" + gameSetId + "/details/", null, 0)
                        )
                );
    }
}
