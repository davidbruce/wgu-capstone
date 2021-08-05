package com.wgu.capstone.views;

import j2html.tags.Tag;

import java.util.List;

import static j2html.TagCreator.div;

public class GameSetActionsView {
    public static Tag getPartial(String gameSetId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return div(
                TableTemplate.tableHeader(title, "Manage", "/game-set-actions/" + gameSetId + "/create", true, page, count, "/game-set-actions/" + gameSetId),
                TableTemplate.tableBody(headers, data, null, null)
            );
    }
}