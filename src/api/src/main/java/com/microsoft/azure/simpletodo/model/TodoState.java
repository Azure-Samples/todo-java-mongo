package com.microsoft.azure.simpletodo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TodoState
 */
public enum TodoState {

    todo("todo"),

    inprogress("inprogress"),

    done("done");

    private final String value;

    TodoState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TodoState fromValue(String value) {
        for (TodoState b : TodoState.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

