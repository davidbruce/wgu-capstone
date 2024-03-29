package com.wgu.capstone.views;

import j2html.tags.Tag;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;
import static j2html.TagCreator.attrs;

public class FormTemplate {

    public static Tag textFormControl(String label, String placeholder) {
        return textFormControl(label, placeholder, null, false, false);
    }
    public static Tag textFormControl(String label, String placeholder, boolean required) {
        return textFormControl(label, placeholder, required, null, false, false);
    }
    public static Tag textFormControl(String label, String placeholder, boolean required, String value) {
        return textFormControl(label, placeholder, required, value, false, false);
    }
    public static Tag textFormControl(String label, String placeholder, String value) {
        return textFormControl(label, placeholder, value, false, false);
    }
    public static Tag textFormControl(String label, String placeholder, String value, boolean disabled) {
        return textFormControl(label, placeholder, value, disabled, false);
    }
    public static Tag textFormControl(String label, String placeholder, String value, boolean disabled, boolean hideLabel) {
        return textFormControl(label, placeholder, false, value, disabled, hideLabel);
    }
    public static Tag textFormControl(String label, String placeholder, boolean required, String value, boolean disabled, boolean hideLabel) {
        return div(
                attrs(".mb-3"),
                label(label + (required ? " *" : "")).withClass(hideLabel ? "visually-hidden" : ""),
                input(attrs(".form-control"))
                        .withName(label.toLowerCase().replace(' ', '_'))
                        .withPlaceholder(placeholder)
                        .withValue(value)
                        .attr(disabled ? "disabled" : "")
                        .attr(required ? "required" : "")
        );
    }

    public static Tag radioFormControl(String label, SortedMap<String, String> inputs, boolean required) {
        return radioFormControl(label, inputs, required, null, false);
    }

    public static Tag radioFormControl(String label, SortedMap<String, String> inputs, boolean required, String value) {
        return radioFormControl(label, inputs, required, value,false);
    }

    public static Tag radioFormControl(String label, SortedMap<String, String> inputs, boolean required, String value, boolean disabled) {
        return div(
            attrs(".mb-3"),
            label(label + (required ? " *" : "")),
            each(inputs, (String name, String radioValue) ->
                div(
                    attrs(".form-check"),
                    input(attrs(".form-check-input"))
                        .withName(label.toLowerCase().replace(' ', '_'))
                        .withType("radio")
                        .withValue(radioValue)
                        .attr(radioValue.equals(value) ? "checked" : "", radioValue.equals(value) ? "checked" : "")
                        .attr(disabled ? "disabled" : "")
                        .attr(required ? "required" : "")
                    ,
                    label(attrs(".form-check-label"), name)
                )
            )
        );
    }

    public static Tag checkFormControl(String name, SortedMap<String, String> inputs, String value, boolean disabled) {
        return div(
                attrs(".mb-3"),
                each(inputs, (String label, String checkValue) ->
                        div(
                                attrs(".form-check"),
                                input(attrs(".form-check-input"))
                                        .withName(name)
                                        .withType("checkbox")
                                        .withValue(checkValue)
                                        .attr(checkValue.equals(value) ? "checked" : "", checkValue.equals(value) ? "checked" : "")
                                        .attr(disabled ? "disabled" : "")
                                ,
                                label(attrs(".form-check-label"), label)
                        )
                )
        );
    }

    public static Tag selectFormControl(String label, Map<String, String> options) {
        return selectFormControl(label, options, null, false);
    }

    public static Tag selectFormControl(String label, Map<String, String> options, String value) {
        return selectFormControl(label, options, value, false);
    }

    public static Tag selectFormControl(String label, Map<String, String> options, String value, boolean disabled) {
        TreeMap<String, String> sorted = options.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, TreeMap::new));
        return div(
            attrs(".mb-3"),
            label(label),
            select(
                attrs(".form-select"),
                each(sorted, (option, selectValue) ->
                    option(rawHtml(option))
                        .withValue(selectValue)
                        .attr(selectValue.equals(value) ? "selected" : "")
                )
            ).withName(label.toLowerCase().replace(' ', '_'))
            .attr(disabled ? "disabled" : "")
        );
    }

    public static Tag nestedMultiSelectFormControl(String label, Map<String, String> options) {
        return div(
            attrs(".mb-3"),
            label(label),
            div(
                attrs(".multi-check"),
                each(options, (option, selectValue) ->
                    div(
                        attrs(".form-check"),
                        input(
                            attrs("#action" + selectValue + ".form-check-input")
                        ).withType("checkbox")
                         .withValue(selectValue)
                         .withName(label.toLowerCase().replace(' ', '_')),
                        label(
                            attrs(".form-check-label"),
                            rawHtml(option)
                        ).attr("for", "action" + selectValue)
                    )
                )
            )
        );
    }

    public static Tag cancelFormButton(String text) {
        return button(
            attrs(".btn.ms-2"),
            text
        ) .attr("type", "cancel")
            .attr("onclick",
                """
                        htmx.removeClass(htmx.find("#form-wrapper"), "show"); 
                        htmx.removeClass(htmx.find("#form-wrapper"), "p-4"); 
                        return false; 
                      """);
    }
}
