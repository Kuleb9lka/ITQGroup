package com.ITQGroup.scheduler;

import com.ITQGroup.worker.impl.DocumentApprovalWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "worker.approval.enabled", havingValue = "true")
public class DocumentApprovalScheduler {

    private final DocumentApprovalWorker approvalWorker;

    @Scheduled(cron = "${worker.approval.cron-expression}")
    public void run() {

        approvalWorker.work();
    }

}
