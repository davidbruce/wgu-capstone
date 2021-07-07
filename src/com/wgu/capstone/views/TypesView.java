package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;

public class TypesView {

    public static String getHtml(List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("types",
                div(
                    TableTemplate.tableHeader("Types", null, false),
                    TableTemplate.tableBody(headers, data, null, null)
                )
            );
    }
}
