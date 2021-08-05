package com.wgu.capstone.views;

import java.util.List;

import static j2html.TagCreator.*;
import static j2html.TagCreator.text;

public class SimulationsView {

    public static String getHtml(String gameSetId, String title, List<String> headers, List<List<String>> data, int page, int count) {
        return MainTemplate.mainView
                ("game-sets",
                        div(
                                attrs(".d-flex.justify-content-between.mb-auto"),
                                h4(
                                        attrs(".bd-highlight"),
                                        title
                                ),
                                ul(
                                        attrs(".nav.nav-pills.d-flex.justify-content-center"),
                                        li(
                                                attrs(".nav-item"),
                                                a(
                                                        attrs(".nav-link.link-dark.me-2.border"),
                                                        text("Game Set Values")
                                                ).withHref("/game-set-values/" + gameSetId)
                                        ),
                                        li(
                                                attrs(".nav-item"),
                                                a(
                                                        attrs(".nav-link.active.link-dark.me-2"),
                                                        text("Simulation Results")
                                                ).withHref("/game-set-simulations/" + gameSetId)
                                        )
                                )
                        ),
                        div(
                                TableTemplate.tableHeader("Simulations", "", false, page, count, "/game-set-simulations/" + gameSetId),
                                TableTemplate.tableBody(headers, data, "/game-set-simulations/" + gameSetId + "/details/", null)
                        )
                );
    }
}
