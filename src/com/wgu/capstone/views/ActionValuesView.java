package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;

public class ActionValuesView {

    public static String getHtml(String actionId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
            ("action-values",
                div(
                    TableTemplate.tableHeader(title, "/action-values/" + actionId + "/create", true),
                    TableTemplate.tableBody(headers, data, null, "/action-values/" + actionId + "/"),
                    TableTemplate.tableFooter(page, count, "/action-values/" + actionId)
                )
            );
    }
}
