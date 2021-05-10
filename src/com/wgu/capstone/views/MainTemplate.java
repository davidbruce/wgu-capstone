package com.wgu.capstone.views;

import com.wgu.capstone.Main;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

import static j2html.TagCreator.*;

public class MainTemplate {
    public static String mainView(String route, Tag... main) {
        return document(html(
            head(
                title("WGU Capstone"),
                meta().withCharset("utf-8"),
                link().withRel("shortcut icon").withType("icon/x-icon").withHref("favicon.ico"),
                link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css"),
                link().withRel("stylesheet").withHref("https://cdn.jsdelivr.net/npm/bootstrap-icons@1.4.1/font/bootstrap-icons.css"),
                link().withRel("stylesheet").withHref("stylesheet.css")
            ),
            TagCreator.body(
                attrs(".bg-light"),
                sidebar(route),
                div(
                    attrs(".container"),
                    div(
                        attrs(".row"),
                        div(
                            attrs(".p-4"),
                            div(
                                attrs(".shadow.bg-white.rounded.p-3"),
                                main
                            )
                        )
                    )
                )
            ),
            script().withSrc("https://unpkg.com/htmx.org@1.3.3"),
            script().withSrc("https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js")
        ));
    }

    static ContainerTag sidebar(String route) {
        return div(
            attrs("#sidebar.d-flex.flex-column.p-3.bg-white.shadow"),
            a(
                attrs(".d-flex.align-items-center.me-3.link-dark.text-decoration-none"),
                img(attrs(".bi.me-2"))
                    .withSrc("wgu-logo.svg")
                    .attr("width", 100)
                    .attr("height", 22),
                span(attrs(".fs-4"), "Capstone")
            ),
            hr(),
            TagCreator.ul(attrs(".nav.nav-pills.flex-column.mb-auto.px-2"),
                          sidebarLi(".nav-link.link-dark" + iffElse(route == "home" , ".active", ".border"), "bi-speedometer2", "Dashboard", "/"),
                          sidebarLi(".nav-link.link-dark" + iffElse(route == "actions" , ".active", ".border"), "bi-table", "Action", "/actions"),
                          sidebarLi(".nav-link.link-dark" + iffElse(route == "simulations" , ".active", ".border"), "bi-cpu", "Simulations", "/simulations"),
                          sidebarLi(".nav-link.link-dark" + iffElse(route == "game" , ".active", ".border"), "bi-controller", "Play Testing", "/game")
            ).attr("hx-boost", "true"),
            hr(),
            div(
                attrs(".d-flex.align-items-center.text-decorations-none"),
                img(attrs(".rounded-circle.ms-4.me-3"))
                    .attr("width", 32)
                    .attr("height", 32)
                    .withSrc("profile.jpg"),
                strong("David Bruce")
            )
        ).withStyle("min-width: 200px");
    }

    static ContainerTag sidebarLi(String attrs, String icon, String text, String href) {
        return li(
                a(
                    attrs(attrs + ".d-flex.justify-content-center"),
                    div(
                        attrs(".wrapper"),
                        i(attrs(".bi.me-2." + icon)),
                        text(text)
                    )
                ).withHref(href)
        );
    }
}
