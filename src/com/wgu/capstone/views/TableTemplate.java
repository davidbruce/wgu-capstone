package com.wgu.capstone.views;

import j2html.tags.ContainerTag;

import java.util.List;

import static j2html.TagCreator.*;

public class TableTemplate {
    public static ContainerTag bsTable(List<String> headers, List<List<String>> values) {
        return table(
            attrs(".table.table-sm"),
            thead(
                tr(
                    each(headers, header -> th(header).attr("scope", "col"))
                )
            ),
            tbody(
                each(values, row ->
                    tr(
                        th(String.valueOf(row.get(0))),
                        each(row.subList(1, row.size()), col -> td(col))
                    )
                )
            )
        );
    }
}
