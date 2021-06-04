package com.wgu.capstone.views;

import java.util.List;

public class ActionValuesView {

    public static String getHtml(String actionId, String title, List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("action-values",
                TableTemplate.tableHeader(title, "/action-values/" + actionId + "/create", true),
                TableTemplate.tableBody(headers, data, null, "/action-values/"),
                TableTemplate.tableFooter()
            );
    }
}
