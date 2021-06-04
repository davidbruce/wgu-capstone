package com.wgu.capstone.views;

import java.util.List;

public class CharactersView {
    public static String getHtml(List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("characters",
                TableTemplate.tableHeader("Characters", "/characters/create", true),
                TableTemplate.tableBody(headers, data, "/character-values/", "/characters/"),
                TableTemplate.tableFooter()
            );
    }
}
