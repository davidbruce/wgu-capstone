package com.wgu.capstone.views;

import com.wgu.capstone.Main;
import com.wgu.capstone.controllers.GameSetsController;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

import java.util.Arrays;

import static j2html.TagCreator.*;

public class MainTemplate {

    public static String mainView(String route, Tag... main) {
        return mainView(route, true, main);
    }

    public static String mainView(String route, boolean roundChildren, Tag... main) {
        return document(html(
            head(
                title("WGU Capstone"),
                meta().withCharset("utf-8"),
                link().withRel("shortcut icon").withType("icon/x-icon").withHref("/favicon.ico"),
                link().withRel("stylesheet").withHref("/bootstrap.min.css"),
                link().withRel("stylesheet").withHref("/bootstrap-icons-1.5.0/bootstrap-icons.css"),
                link().withRel("stylesheet").withHref("/Chart.min.css"),
                link().withRel("stylesheet").withHref("/stylesheet.css")
            ),
            TagCreator.body(
                attrs(".bg-light"),
                sidebar(route),
                div(
                    attrs("#body-container.container"),
                    div(
                        attrs(".row"),
                        div(
                            attrs("#form-wrapper.slide-hidden"),
                            div(
                                attrs(".shadow.bg-white.rounded.p-3"),
                                div(attrs("#form"))
                            )
                        ),
                        div(
                            attrs("#main-wrapper.p-4"),
                            each(Arrays.asList(main), tag ->
                                div(
                                    attrs(".section" + (roundChildren ? ".rounded.p-3": "")),
                                    tag
                                )
                            )
                        )
                    )
                )
            ),
            script().withSrc("/htmx.min.js"),
            script().withSrc("/bootstrap.bundle.min.js"),
            script().withSrc("/regression.min.js"),
            script().withSrc("/chart.min.js"),
            script().withType("module").withSrc("/App.js")
        ));
    }

    static ContainerTag sidebar(String route) {
        int dashboardId = GameSetsController.getFavorite();
        return div(
            attrs("#sidebar.d-flex.flex-column.p-3.bg-white.shadow"),
            a(
                attrs(".d-flex.align-items-center.me-3.link-dark.text-decoration-none"),
                img(attrs(".bi.me-2"))
                    .withSrc("/wgu-logo.svg")
                    .attr("width", 100)
                    .attr("height", 22),
                span(attrs(".fs-4"), "Capstone")
            ),
            hr(),
            TagCreator.ul(attrs(".nav.nav-pills.flex-column.mb-auto.px-2"),
                    li(
                            attrs(".d-flex.justify-content-center"),
                    button(attrs(".btn.me-2"), i(attrs(".bi.bi-arrow-left")))
                            .attr("onclick",
                                    """
                                          window.history.back()  
                                          """
                            ),
                    button(attrs(".btn.me-2"), i(attrs(".bi.bi-arrow-right")))
                            .attr("onclick",
                                    """
                                          window.history.forward()  
                                          """
                            )
                    ),
                    hr(),
                    sidebarLi(".mt-2.nav-link.link-dark.border", "bi-speedometer2", "Dashboard", "/game-set-dashboard/" + dashboardId, dashboardId == 0),
                sidebarLi(".mt-2.nav-link.link-dark" + iffElse(route == "game-sets" , ".active", ".border"), "bi-sliders", "Game Sets", "/game-sets"),
                    sidebarLi(".mt-2.nav-link.link-dark" + iffElse(route == "types" , ".active", ".border"), "bi-table", "Types", "/types"),
                    sidebarLi(".mt-2.nav-link.link-dark" + iffElse(route.contains("action") , ".active", ".border"), "bi-hurricane", "Actions", "/actions"),
                    sidebarLi(".mt-2.nav-link.link-dark" + iffElse(route.contains("character") , ".active", ".border"), "bi-person-circle", "Characters", "/characters")
            ).attr("hx-boost", "true"),
            hr(),
            div(
                attrs(".d-flex.align-items-center.text-decorations-none"),
                img(attrs(".rounded-circle.ms-4.me-3"))
                    .attr("width", 32)
                    .attr("height", 32)
                    .withSrc("/profile.jpg"),
                strong("David Bruce")
            )
        ).withStyle("min-width: 237px");
    }


    static ContainerTag sidebarLi(String attrs, String icon, String text, String href) {
        return sidebarLi(attrs, icon, text, href, false);
    }
    static ContainerTag sidebarLi(String attrs, String icon, String text, String href, boolean disabled) {
        return li(
                a(
                    attrs(attrs + ".d-flex.justify-content-center" + (disabled ? ".disabled" : "")),
                    div(
                        attrs(".wrapper"),
                        i(attrs(".bi.me-2." + icon)),
                        text(text)
                    )
                ).withHref(href)
        );
    }
}
