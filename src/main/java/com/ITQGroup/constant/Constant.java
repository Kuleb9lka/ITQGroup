package com.ITQGroup.constant;

import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;

import java.util.Map;

public final class Constant {

    public static final Map<DocumentStatus, Action> DOCUMENT_ACTIONS_MAP = Map.ofEntries(
            Map.entry(DocumentStatus.SUBMITTED, Action.SUBMIT),
            Map.entry(DocumentStatus.APPROVED, Action.APPROVE)
    );

    private Constant() {
    }
}
