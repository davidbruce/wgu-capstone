package com.wgu.capstone.views;

import java.util.List;

public class CharacterValuesView {
    public static String getHtml(String characterId, String title, List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("character-values",
                TableTemplate.tableHeader(title, "/character-values/" + characterId + "/create", true),
                TableTemplate.tableBody(headers, data, null, "/character-values/"),
                TableTemplate.tableFooter()
            );
    }
}
