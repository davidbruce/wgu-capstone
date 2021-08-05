package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.div;
import static j2html.TagCreator.hr;

public class ActionsView {

    public static String getHtml(List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
            ("actions",
                 div(
                     TableTemplate.tableHeader("Actions", "/actions/create", true, page, count, "/actions"),
                     TableTemplate.tableBody(headers, data, "/action-values/", "/actions/")
                 )
            );
    }
}
