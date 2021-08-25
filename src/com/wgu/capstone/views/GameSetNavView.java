package com.wgu.capstone.views;

import com.wgu.capstone.controllers.SimulationsController;
import j2html.tags.Tag;

import static j2html.TagCreator.*;
import static j2html.TagCreator.text;

public class GameSetNavView {
    public static Tag gameSetNav(String title, String gameSetId, int active) {
        boolean disabled = SimulationsController.enableResults(Integer.parseInt(gameSetId));
        return div(
                        attrs("#game-set-nav.d-flex.justify-content-between.mb-auto"),
                        h4(
                                attrs(".bd-highlight"),
                                title
                        ),
                        ul(
                                attrs(".nav.nav-pills.d-flex.justify-content-center"),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2.link-dark.border"),
                                                i(attrs(".bi.me-2.bi-arrow-90deg-up")),
                                                text("Game Sets")
                                        ).withHref("/game-sets/")
                                ),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2" + (active == 0 ? ".active" : ".link-dark.border")),
                                                i(attrs(".bi.me-2.bi-laptop-fill")),
                                                text("Simulator")
                                        ).withHref("/game-set-values/" + gameSetId)
                                ),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2" + (active == 1 ? ".active" : ".link-dark.border") + (disabled ? ".disabled" : "")),
                                                i(attrs(".bi.me-2.bi-list-ul")),
                                                text("Simulation Results")
                                        ).withHref("/game-set-simulations/" + gameSetId)
                                ),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2" + (active == 2 ? ".active" : ".link-dark.border") + (disabled ? ".disabled" : "")),
                                                i(attrs(".bi.me-2.bi-bar-chart-fill")),
                                                text("Analysis")
                                        ).withHref("/game-set-analysis/" + gameSetId)
                                ),
                                li(
                                        attrs(".nav-item"),
                                        a(
                                                attrs(".nav-link.me-2" + (active == 3 ? ".active" : ".link-dark.border") + (disabled ? ".disabled" : "")),
                                                i(attrs(".bi.me-2.bi-speedometer2")),
                                                text("Dashboard")
                                        ).withHref("/game-set-dashboard/" + gameSetId)
                                )
                        )
                );
    }
}
