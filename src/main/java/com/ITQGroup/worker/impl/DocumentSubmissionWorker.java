package com.ITQGroup.worker.impl;

import com.ITQGroup.service.DocumentWorkerService;
import com.ITQGroup.worker.DocumentWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentSubmissionWorker implements DocumentWorker {

    private final DocumentWorkerService documentWorkerService;

    @Value("${worker.submission.batch-size}")
    private Integer documentsBatchSize;


    @Override
    public void work() {

        documentWorkerService.processSubmission(documentsBatchSize);
    }
}
