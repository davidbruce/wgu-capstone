package com.wgu.capstone.views;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

public class TableTemplate {
    public static ContainerTag tableBody(List<String> headers, List<List<String>> values, String childLink, String updelLink) {
        return table(
            attrs(".table.table-sm"),
            thead(
                tr(
                    each(headers, header -> th(header).attr("scope", "col")),
                    iff(updelLink != null || childLink != null, th("").attr("scope", "col")))
                ),
            tbody(
                attrs(".align-middle"),
                each(values, row ->
                    tr(
                        th(String.valueOf(row.get(0))),
                        each(row.subList(1, row.size()), col -> td(col)),
                        iff(updelLink != null || childLink != null,
                            td(
                                attrs(".d-flex.justify-content-end"),
                                iff(updelLink != null,
                                    div(
                                        button(
                                            attrs(".btn.me-2"),
                                            i(attrs(".bi.bi-pencil-square"))
                                        )
                                            .attr("onclick",
                                                """
                                                        htmx.addClass(htmx.find("#form-wrapper"), "show"); 
                                                        htmx.addClass(htmx.find("#form-wrapper"), "p-4"); 
                                                      """)
                                            .attr("hx-get", updelLink + "update/" + row.get(0))
                                            .attr("hx-target", "#form")
                                            .attr("hx-swap", "innerHTML"),
                                        button(
                                            attrs(".btn.me-2"),
                                            i(attrs(".bi.bi-trash"))
                                        )
                                            .attr("onclick",
                                                """
                                                        htmx.addClass(htmx.find("#form-wrapper"), "show"); 
                                                        htmx.addClass(htmx.find("#form-wrapper"), "p-4"); 
                                                      """)
                                            .attr("hx-get", updelLink + "delete/" + row.get(0))
                                            .attr("hx-target", "#form")
                                            .attr("hx-swap", "innerHTML")
                                    )
                                ),
                                iff(childLink != null,
                                    div(a(
                                        attrs(".table-link.link-dark.d-flex.justify-content-center"),
                                        i(attrs(".bi.bi-arrow-right-square"))
                                    ).withHref(childLink + row.get(0)).attr("hx-boost", "true"))
                                )
                            )
                        )
                    )
                )
            )
        );
    }
    public static ContainerTag tableHeader(String title, String route, boolean showButtons) {
        return tableHeader(title, "Create", route, showButtons);
    }
    public static ContainerTag tableHeader(String title, String buttonText, String route, boolean showButtons) {
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight.mb-3"),
            h4(attrs(".p-2.bd-highlight"), title),
            iff(showButtons,
                div(
                    attrs(".p-2.d-flex.justify-content-center"),
                    button(attrs(".btn.me-2"), buttonText)
                        .attr("onclick",
                            """
                                    htmx.addClass(htmx.find("#form-wrapper"), "show"); 
                                    htmx.addClass(htmx.find("#form-wrapper"), "p-4"); 
                                  """
                        )
                        .attr("hx-get", route)
                        .attr("hx-target", "#form")
                        .attr("hx-swap", "innerHTML")
//                    button(attrs(".btn.me-2.disabled"), "Update"),
//                    button(attrs(".btn.disabled"), "Delete")
                )
            )
        );
    }
    public static ContainerTag tableFooter(int page, int count, String route) {
        List<DomContent> pages = new ArrayList<>();
        int maxPage = (int)Math.ceil(count / 15);
        if (maxPage == 0) {
            maxPage = 1;
        }
        int startPage = page - 2;
        if (startPage < 1) {
            startPage = page;
        }
        if (page == 2) {
            startPage = 1;
        }

        if (maxPage > 5) {
            maxPage = 5;
        } else {
            startPage = 1;
        }
        for (int i = startPage; i <= maxPage; i++) {
            pages.add(
              li(
                  attrs(".page-item.me-2" + (page == i ? ".active" : "")),
                  a(
                      attrs(".page-link.link-dark"),
                      text("" + i)
                  ).withHref(route + "?page=" + i)
              )
            );
        }
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight"),
            nav(
                attrs(".p-2.bd-highlight"),
                ul(
                    attrs(".pagination"),
                    each(pages, pageLi -> pageLi)
                ).withStyle("margin-bottom: 0")
            ).attr("aria-label", "Page Navigation for labels"),
            nav(
                attrs(".p-2.bd-highlight"),
                ul(
                    attrs(".pagination"),
                    li(
                        attrs(".page-item.me-2" + (page == 1 ? ".disabled" : "")),
                        a(
                            attrs(".page-link.link-dark"),
                            "Previous"
                        ).withHref(route + "?page=" + (page - 1))
                    ),
                    li(
                        attrs(".page-item.me-2" + (page == maxPage ? ".disabled" : "")),
                        a(
                            attrs(".page-link.link-dark"),
                            "Next"
                        ).withHref(route + "?page=" + (page + 1))
                    )
                ).withStyle("margin-bottom: 0")

            ).attr("aria-label", "Page Navigation for labels")
        );
    }
}
