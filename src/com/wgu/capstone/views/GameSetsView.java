package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;


public class GameSetsView {
    public static String getHtml(List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
            ("game-sets",
                div(
                    TableTemplate.tableHeader("Game Sets", "/game-sets/create", true),
                    TableTemplate.tableBody(headers, data, "/game-set-values/", "/game-sets/"),
                    TableTemplate.tableFooter(page, count, "/game-sets")
                )
            );
    }
}