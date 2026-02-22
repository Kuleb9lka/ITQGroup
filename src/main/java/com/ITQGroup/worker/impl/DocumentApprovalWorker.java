package com.ITQGroup.worker.impl;

import com.ITQGroup.service.DocumentWorkerService;
import com.ITQGroup.worker.DocumentWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentApprovalWorker implements DocumentWorker {

    private final DocumentWorkerService documentWorkerService;

    @Value("${worker.approval.batch-size}")
    private Integer documentsBatchSize;


    @Override
    public void work() {

        documentWorkerService.processApproval(documentsBatchSize);
    }
}
