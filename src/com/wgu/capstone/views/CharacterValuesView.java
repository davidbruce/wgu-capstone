package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;

public class CharacterValuesView {
    public static String getHtml(String characterId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
            ("character-values",
                div(
                    TableTemplate.tableHeader(title, "/character-values/" + characterId + "/create", true),
                    TableTemplate.tableBody(headers, data, null, "/character-values/" + characterId + "/"),
                    TableTemplate.tableFooter(page, count, "/character-values/" + characterId)
                )
            );
    }
}