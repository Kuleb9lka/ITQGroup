package com.ITQGroup.service;

public interface DocumentWorkerService {

    void processSubmission(Integer docsLimit);

    void processApproval(Integer docsLimit);
}
