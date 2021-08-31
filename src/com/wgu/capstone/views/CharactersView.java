package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;

public class CharactersView {
    public static String getHtml(List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
            ("characters",
                div(
                    TableTemplate.tableHeader("Characters", "/characters/create", true, page, count, "/characters"),
                    TableTemplate.tableBody(headers, data, "/character-values/", "/characters/", page)
                )
            );
    }
}
