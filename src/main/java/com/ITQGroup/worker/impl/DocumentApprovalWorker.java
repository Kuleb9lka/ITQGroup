package com.ITQGroup.worker.impl;

import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.worker.DocumentWorker;
import com.ITQGroup.service.DocumentWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentApprovalWorker implements DocumentWorker {


    private final DocumentWorkerService documentWorkerService;

    @Value("${worker.approve.batch-size}")
    private Integer documentsBatchSize;


    @Override
    public void work() {

        documentWorkerService.processByStatus(DocumentStatus.SUBMITTED, documentsBatchSize);
    }
}
