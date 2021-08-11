package com.wgu.capstone.views;

import j2html.tags.Tag;

import static j2html.TagCreator.*;
import static j2html.TagCreator.text;

public class GameSetNavView {
    public static Tag gameSetNav(String title, String gameSetId, int active) {
        return div(
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
                                                attrs(".nav-link.me-2" + (active == 0 ? ".active" : ".link-dark.border")),
                                                text("Game Set Values")
                                        ).withHref("/game-set-values/" + gameSetId)
                                ),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2" + (active == 1 ? ".active" : ".link-dark.border")),
                                                text("Simulation Results")
                                        ).withHref("/game-set-simulations/" + gameSetId)
                                )
                        )
                );
    }
}
