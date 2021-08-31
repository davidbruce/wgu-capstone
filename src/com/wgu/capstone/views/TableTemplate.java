package com.wgu.capstone.views;

import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

import java.util.ArrayList;
import java.util.List;

import static j2html.TagCreator.*;

public class TableTemplate {
    public static ContainerTag tableBody(List<String> headers, List<List<String>> values, String childLink, String updelLink, int page) {
        return table(
            attrs(".table.table-sm.table-hover"),
            thead(
                tr(
                    each(headers, header -> th(attrs(headers.size() > 6 ? ".col" : ".col-2"), header).attr("scope", "col")),
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
                                            .attr("hx-get", updelLink + "update/" + row.get(0) + "?page=" + page)
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
        return tableHeader(title, "Create", route, showButtons, 0, 0, null);
    }
    public static ContainerTag tableHeader(String title, String route, boolean showButtons, int page, int count, String pageRoute) {
        return tableHeader(title, "Create", route, showButtons, page, count, pageRoute);
    }

    public static ContainerTag tableHeader(String title, String route, boolean showButtons, int page, int count, int offset, String pageRoute) {
        return tableHeader(title, "Create", route, showButtons, page, count, offset, pageRoute);
    }

    public static ContainerTag tableHeader(String title, String buttonText, String route, boolean showButtons) {
        return tableHeader(title, buttonText, route, showButtons, 0, 0, null);
    }

    public static ContainerTag tableHeader(String title, String buttonText, String route, boolean showButtons, int page, int count, String pageRoute) {
        return tableHeader(title, buttonText, route, showButtons, page, count, 15, pageRoute, null);
    }

    public static ContainerTag tableHeader(String title, String buttonText, String route, boolean showButtons, int page, int count, int offset, String pageRoute) {
        return tableHeader(title, buttonText, route, showButtons, page, count, offset, pageRoute, null);
    }
    public static ContainerTag tableHeader(String title, String buttonText, String route, boolean showButtons, int page, int count, int offset, String pageRoute, String onclick) {
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight.mb-2"),
            h4(attrs(".p-2.bd-highlight"), title),
            iff(pageRoute != null, tablePaging(page, count, offset, pageRoute, onclick)),
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
                )
            )
        );
    }
    public static ContainerTag tablePaging(int page, int count, int offset, String route, String onclick) {
        List<DomContent> pages = new ArrayList<>();
        int maxPage = (int)Math.ceil(count / (offset * 1.0));
        int endPage = maxPage;
        if (endPage == 0) {
            endPage = 1;
        }
        int startPage = page - 2;
        if (page < 3) {
            startPage = 1;
            if (endPage > 5) {
                endPage = 5;
            }
        }

        if (endPage > 5) {
            if (endPage - page >= 2) {
                endPage = page + 2;
            }
            if (endPage - startPage < 5) {
                startPage = endPage - 4;
            }
        }
        else {
            startPage = 1;
        }
        for (int i = startPage; i <= endPage; i++) {
            pages.add(
              li(
                  attrs(".page-item.me-2" + (page == i ? ".active" : "")),
                  a(
                      attrs(".page-link.link-dark"),
                      text("" + i)
                  ).withHref(onclick != null ? "#" : route + "?page=" + i)
                          .attr(onclick != null ? "onclick" : "",
                                  onclick != null ? "var searchParams = new URLSearchParams(window.location.search); searchParams.set('" +  onclick  + "','" + i + "');window.location.search = searchParams.toString();"
                                          : "")
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
                            "Start"
                        ).withHref(onclick != null ? "#" : route + "?page=" + 1)
                                .attr(onclick != null ? "onclick" : "",
                                        onclick != null ? "var searchParams = new URLSearchParams(window.location.search); searchParams.set('" +  onclick  + "','" + 1 + "');window.location.search = searchParams.toString();"
                                                : "")
                    ),
                    li(
                        attrs(".page-item.me-2" + (page == maxPage || maxPage == 0 ? ".disabled" : "")),
                        a(
                            attrs(".page-link.link-dark"),
                            "End"
                        ).withHref(onclick != null ? "#" : route + "?page=" + (maxPage))
                                .attr(onclick != null ? "onclick" : "",
                                        onclick != null ? "var searchParams = new URLSearchParams(window.location.search); searchParams.set('" +  onclick  + "','" + maxPage + "');window.location.search = searchParams.toString();"
                                                : "")
                    )
                ).withStyle("margin-bottom: 0")

            ).attr("aria-label", "Page Navigation for labels")
        );
    }
}
