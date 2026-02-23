package com.ITQGroup.scheduler;

import com.ITQGroup.worker.impl.DocumentSubmissionWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "worker.submission.enabled", havingValue = "true")
public class DocumentSubmissionScheduler {

    private final DocumentSubmissionWorker submissionWorker;

    @Scheduled(cron = "${worker.submission.cron-expression}")
    public void run(){

        submissionWorker.work();
    }
}
