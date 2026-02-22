package com.ITQGroup.service;

import com.ITQGroup.enums.DocumentStatus;

public interface DocumentWorkerService {

    void processByStatus(DocumentStatus status, Integer docsLimit);
}
