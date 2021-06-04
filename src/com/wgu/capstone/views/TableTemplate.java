package com.wgu.capstone.views;

import j2html.tags.ContainerTag;

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
                                                        htmx.toggleClass(htmx.find("#form-wrapper"), "show"); 
                                                        htmx.toggleClass(htmx.find("#form-wrapper"), "p-4"); 
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
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight.mb-3"),
            h4(attrs(".p-2.bd-highlight"), title),
            iff(showButtons,
                div(
                    attrs(".p-2.d-flex.justify-content-center"),
                    button(attrs(".btn.me-2"), "Create")
                        .attr("onclick",
                            """
                                    htmx.toggleClass(htmx.find("#form-wrapper"), "show"); 
                                    htmx.toggleClass(htmx.find("#form-wrapper"), "p-4"); 
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
    public static ContainerTag tableFooter() {
        return div(
            attrs(".d-flex.justify-content-between.bd-highlight"),
            nav(
                attrs(".p-2.bd-highlight"),
                ul(
                    attrs(".pagination"),
                    rawHtml("""
                                                 <li class="page-item me-2 active"><a class="page-link link-dark" href="#">1</a></li>
                                                 <li class="page-item me-2"><a class="page-link link-dark" href="#">2</a></li>
                                                 <li class="page-item me-2"><a class="page-link link-dark" href="#">3</a></li>
                                     """)
                ).withStyle("margin-bottom: 0")
            ).attr("aria-label", "Page Navigation for labels"),
            nav(
                attrs(".p-2.bd-highlight"),
                ul(
                    attrs(".pagination"),
                    rawHtml("""
                                                 <li class="page-item me-2"><a class="page-link link-dark" href="#">Previous</a></li>
                                                 <li class="page-item me-2"><a class="page-link link-dark" href="#">Next</a></li>
                                     """)
                ).withStyle("margin-bottom: 0")

            ).attr("aria-label", "Page Navigation for labels")
        );
    }
}
