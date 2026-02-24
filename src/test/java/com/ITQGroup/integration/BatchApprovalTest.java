package com.ITQGroup.integration;

import com.ITQGroup.AbstractIntegrationTest;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.reposiroty.ApprovalRegistryRepository;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.reposiroty.HistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
public class BatchApprovalTest extends AbstractIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ApprovalRegistryRepository approvalRegistryRepository;

    void setUp() {

        Document document1 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);
        Document document2 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);
        Document document3 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);

        Document document4 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.APPROVED, LocalDateTime.now(), null, List.of(), 0L);
        Document document5 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);
        Document document6 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, List.of(), 0L);
        documentRepository.saveAll(List.of(document1, document2, document3, document4, document5, document6));
    }

    @Test
    @DisplayName("Should make batch update to approval status")
    void batchApproval_Success() throws Exception {

        setUp();

        List<Long> requestIds = documentRepository.findAll().stream()
                .filter(doc -> doc.getStatus().equals(DocumentStatus.SUBMITTED))
                .map(Document::getId).toList();
        String requestJson = objectMapper.writeValueAsString(requestIds);

        mockMvc.perform(post("/documents/send-approval/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        List<Document> allDocs = documentRepository.findAll();
        List<History> allHistory = historyRepository.findAll();
        List<ApprovalRegistry> allRegistry = approvalRegistryRepository.findAll();

        List<Document> allApprovedDocs = allDocs.stream()
                .filter(document -> document.getStatus().equals(DocumentStatus.APPROVED)).toList();

        List<Long> allApprovedDocsIds = allApprovedDocs.stream()
                .map(Document::getId).toList();

        List<Long> docsIdsFromApprovalRegistry = allRegistry.stream()
                .map(ApprovalRegistry::getDocument)
                .map(Document::getId).toList();

        assertThat(allApprovedDocsIds).containsAll(docsIdsFromApprovalRegistry);
        assertThat(allApprovedDocs)
                .extracting(Document::getStatus)
                        .containsOnly(DocumentStatus.APPROVED);
        assertThat(allHistory)
                .extracting(History::getAction)
                        .containsOnly(Action.APPROVE);
        assertThat(docsIdsFromApprovalRegistry)
                .containsExactlyInAnyOrderElementsOf(requestIds);
    }

    @Test
    @DisplayName("Should make partial batch update to approval status")
    void batchApproval_partialSuccess() throws Exception {

        setUp();

        List<Document> allDocsAfterSetUp = documentRepository.findAll();

        long submittedCountBeforeRequest = allDocsAfterSetUp.stream()
                .filter(document -> document.getStatus().equals(DocumentStatus.SUBMITTED))
                .count();

        List<Long> allDocumentsIds = allDocsAfterSetUp.stream()
                .map(Document::getId).toList();
        String requestJson = objectMapper.writeValueAsString(allDocumentsIds);

        MvcResult mvcResult = mockMvc.perform(post("/documents/send-approval/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();



        String resultAsAString = mvcResult.getResponse().getContentAsString();

        List<DocumentProcessingResultDto> response = objectMapper.readValue(
                resultAsAString,
                new TypeReference<List<DocumentProcessingResultDto>>() {});

        long successResponseCount = response.stream()
                .filter(processingDto -> processingDto.getStatus().equals("SUCCESS"))
                .count();

        List<Document> allDocsAfterRequest = documentRepository.findAll();

        assertThat(submittedCountBeforeRequest).isEqualTo(successResponseCount);

        assertThat(allDocsAfterRequest)
                .filteredOn(doc -> doc.getStatus().equals(DocumentStatus.SUBMITTED))
                .isEmpty();

        assertThat(allDocsAfterRequest)
                .extracting(Document::getStatus)
                .contains(DocumentStatus.DRAFT);

        assertThat(approvalRegistryRepository.findAll())
                .hasSize((int) successResponseCount);

        assertThat(historyRepository.findAll())
                .hasSize((int) successResponseCount);
    }


}
