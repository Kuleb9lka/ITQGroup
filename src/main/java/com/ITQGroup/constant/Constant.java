package com.ITQGroup.constant;

import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;

import java.util.Map;

public final class Constant {

    public static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
    public static final String RESPONSE_STATUS_NOT_FOUND = "NOT FOUND";
    public static final String RESPONSE_STATUS_CONFLICT = "CONFLICT";

    public static final String RESPONSE_STATUS_APPROVAL_REGISTRY_ERROR = "APPROVAL REGISTRY ERROR";
    public static final String RESPONSE_STATUS_UNKNOWN_ERROR = "UNKNOWN ERROR";

    public static final Map<DocumentStatus, Action> DOCUMENT_ACTIONS_MAP = Map.ofEntries(
            Map.entry(DocumentStatus.DRAFT, Action.SUBMIT),
            Map.entry(DocumentStatus.SUBMITTED, Action.APPROVE)
    );

    private Constant() {
    }
}
