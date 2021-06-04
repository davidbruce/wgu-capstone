package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.div;

public class ActionsView {

    public static String getHtml(List<String> headers, List<List<String>> data) {
        return MainTemplate.mainView
            ("actions",
                 TableTemplate.tableHeader("Actions", "/actions/create", true),
                 TableTemplate.tableBody(headers, data, "/action-values/", "/actions/"),
                 TableTemplate.tableFooter()
            );
    }
}
