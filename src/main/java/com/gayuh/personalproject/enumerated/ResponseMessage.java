package com.gayuh.personalproject.enumerated;

public enum ResponseMessage {
    GET_ALL_DATA("Data found with size "),
    GET_DATA("Data found"),
    UPDATE_DATA("Success update data"),
    CREATE_DATA("Success create new data"),
    DELETE_DATA("Success delete data"),
    DATA_NOT_FOUND("Data not found"),
    UNAUTHORIZED("Unauthorized");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
