package com.wgu.capstone.views;

import j2html.tags.Tag;

import java.util.List;

import static j2html.TagCreator.div;

public class GameSetActionsView {
    public static Tag getPartial(String gameSetId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return div(
                TableTemplate.tableHeader(title, "/game-set-actions/" + gameSetId + "/create", true),
                TableTemplate.tableBody(headers, data, null, "/game-set-actions/" + gameSetId + "/"),
                TableTemplate.tableFooter(page, count, "/game-set-actions/" + gameSetId)
            );
    }
}