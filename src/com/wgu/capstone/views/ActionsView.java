package com.wgu.capstone.views;

import j2html.TagCreator;

import java.util.Arrays;

import static j2html.TagCreator.*;
import static j2html.TagCreator.div;

public class ActionsView {

    public static String getHtml() {
        return MainTemplate.mainView
            ("actions",
             div(
                 attrs(".d-flex.justify-content-between.bd-highlight.mb-3"),
                 h4(attrs(".p-2.bd-highlight"), "Actions"),
                 div(
                     attrs(".p-2.d-flex.justify-content-center"),
                     button(attrs(".btn.border.me-2"), "Create"),
                     button(attrs(".btn.border.me-2.disabled"), "Update"),
                     button(attrs(".btn.border.disabled"), "Delete")
                 )
             ),
             TagCreator.div(
                 TableTemplate.bsTable(Arrays.asList("#", "First", "Last", "Handle"), Arrays.asList(Arrays.asList("1", "Mark", "Otto", "@mdo")))
             ),
             div(
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
             )
            );
    }
}
