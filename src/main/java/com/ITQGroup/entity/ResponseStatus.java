package com.ITQGroup.entity;

public enum ResponseStatus {

    SUCCESS("SUCCESS"),
    NOT_FOUND("NOT FOUND"),
    CONFLICT("CONFLICT"),
    APPROVAL_REGISTRY_ERROR("APPROVAL REGISTRY ERROR"),
    UNKNOWN_ERROR("UNKNOWN ERROR");

    ResponseStatus(String status) {
    }
}
