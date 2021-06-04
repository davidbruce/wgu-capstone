package com.wgu.capstone.views;

import java.util.List;


public class GameSetsView {
    public static String getHtml(List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("game-sets",
                TableTemplate.tableHeader("Game Sets", "/game-sets/create", true),
                TableTemplate.tableBody(headers, data, "/game-set-values/", "/game-sets/"),
                TableTemplate.tableFooter()
            );
    }
}