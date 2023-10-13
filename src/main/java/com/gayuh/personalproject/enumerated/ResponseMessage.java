package com.gayuh.personalproject.enumerated;

public enum ResponseMessage {
    GET_ALL_DATA("Data found with size "),
    GET_DATA("Data found"),
    UPDATE_DATA("Success update data"),
    CREATE_DATA("Success create new data"),
    DELETE_DATA("Success delete data"),
    DATA_NOT_FOUND("Data not found"),
    UNAUTHORIZED("Unauthorized"),
    LOGIN_FAILED("Wrong email or password"),
    ACCOUNT_SUSPEND("Your account being suspend"),
    LOGIN_SUCCESS("Success login"),
    ACCOUNT_INACTIVE("Your account is not activated"),
    REGISTER_SUCCESS("Success register your account, please check email for verification"),
    ACCOUNT_ALREADY_EXIST("Account already exist"),
    PASSWORD_NOT_SAME("Password and retype password not match"),
    PASSWORD_REGEX_NOT_MATCH("must contains at least one symbol, one lowercase, one uppercase, one digit, and length is between 8-20"),
    TOKEN_EXPIRED("Token expired, resend email to get new token"),
    ACCOUNT_ALREADY_ACTIVATED("Your account already activated, please login"),
    ACTIVATE_ACCOUNT_SUCCESS("Success activated your account, please login"),
    SUCCESS_RESEND_EMAIL("Success resend, please check your email"),
    SUCCESS_CHANGE_PASSWORD("Success change password, please login"),
    PASSWORD_SAME_OLD_PASSWORD("Password are same with old password"),
    FORBIDDEN("Forbidden");

    private final String value;

    ResponseMessage(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
