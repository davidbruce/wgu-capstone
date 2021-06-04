package com.wgu.capstone.views;

import j2html.tags.Tag;

import java.util.Map;

import static j2html.TagCreator.*;
import static j2html.TagCreator.attrs;

public class FormTemplate {

    public static Tag textFormControl(String label, String placeholder) {
        return textFormControl(label, placeholder, null);
    }

    public static Tag textFormControl(String label, String placeholder, String value) {
        return div(
            attrs(".mb-3"),
            label(label),
            input(attrs(".form-control"))
                .withName(label.toLowerCase().replace(' ', '_'))
                .withPlaceholder(placeholder)
                .withValue(value)
        );
    }

    public static Tag radioFormControl(String name, Map<String, String> inputs) {
        return radioFormControl(name, inputs, null);
    }

    public static Tag radioFormControl(String name, Map<String, String> inputs, String value) {
        return div(
            attrs(".mb-3"),
            each(inputs, (String label, String radioValue) ->
                div(
                    attrs(".form-check"),
                    input(attrs(".form-check-input"))
                        .withName(name)
                        .withType("radio")
                        .withValue(radioValue)
                        .attr("checked", radioValue.equals(value) ? true : false),
                    label(attrs(".form-check-label"), label)
                )
            )
        );
    }

    public static Tag selectFormControl(String label, Map<String, String> options) {
        return selectFormControl(label, options, null);
    }

    public static Tag selectFormControl(String label, Map<String, String> options, String value) {
        return div(
            attrs(".mb-3"),
            label(label),
            select(
                attrs(".form-select"),
                each(options, (option, selectValue) ->
                    option(option).withValue(selectValue)
                )
            ).withName(label.toLowerCase().replace(' ', '_'))
            .withValue(value)
        );
    }
}
