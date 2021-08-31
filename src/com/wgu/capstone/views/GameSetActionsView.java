package com.wgu.capstone.views;

import j2html.tags.Tag;

import java.util.List;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;

public class GameSetActionsView {
    public static Tag getPartial(String gameSetId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return div(
                attrs("#actions-table"),
                TableTemplate.tableHeader(title, "Manage", "/game-set-actions/" + gameSetId + "/manage", true, page, count, 10, "", "actions-page"),
                TableTemplate.tableBody(headers, data, null, null, 0)
            );
    }
}