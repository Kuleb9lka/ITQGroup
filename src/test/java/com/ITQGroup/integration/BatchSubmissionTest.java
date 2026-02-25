package com.ITQGroup.integration;

import com.ITQGroup.AbstractIntegrationTest;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.Action;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.reposiroty.HistoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
public class BatchSubmissionTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private HistoryRepository historyRepository;


    void setUp() {

        Document document1 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, new ArrayList<>(), 0L);
        Document document2 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, new ArrayList<>(), 0L);
        Document document3 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, new ArrayList<>(), 0L);

        Document document4 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, new ArrayList<>(), 0L);
        Document document5 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.APPROVED, LocalDateTime.now(), null, new ArrayList<>(), 0L);
        Document document6 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, new ArrayList<>(), 0L);

        documentRepository.saveAll(List.of(document1, document2, document3, document4, document5, document6));
    }

    @Test
    @DisplayName("Should make batch update to submission status")
    void batchSubmission_Success() throws Exception {

        setUp();

        List<Document> addDocs = documentRepository.findAll();

        List<Long> draftIds = addDocs.stream()
                .filter(document -> document.getStatus().equals(DocumentStatus.DRAFT))
                .map(Document::getId).toList();

        String requestJson = objectMapper.writeValueAsString(draftIds);

        MvcResult mvcResult = mockMvc.perform(post("/documents/send-submission/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsAString = mvcResult.getResponse().getContentAsString();

        List<DocumentProcessingResultDto> response = objectMapper.readValue(
                resultAsAString,
                new TypeReference<List<DocumentProcessingResultDto>>() {
                });

        List<History> allHistories = historyRepository.findAll();

        List<Long> historySubmittedIds = allHistories.stream()
                .map(History::getDocument)
                .map(Document::getId).toList();

        assertThat(historySubmittedIds).containsAll(draftIds);

        assertThat(response)
                .extracting(DocumentProcessingResultDto::getStatus)
                .containsOnly("SUCCESS");
        assertThat(documentRepository.findAll())
                .filteredOn(document -> document.getStatus().equals(DocumentStatus.DRAFT))
                .isEmpty();
    }

    @Test
    @DisplayName("Should make batch update with partial success")
    void batchSubmission_PartialSuccess() throws Exception {

        setUp();

        List<Document> allDocsBefore = documentRepository.findAll();

        Map<Long, DocumentStatus> statusBefore = allDocsBefore.stream()
                .collect(Collectors.toMap(Document::getId, Document::getStatus));

        long draftCountBefore = allDocsBefore.stream()
                .filter(d -> d.getStatus().equals(DocumentStatus.DRAFT))
                .count();

        List<Long> allIds = allDocsBefore.stream()
                .map(Document::getId)
                .toList();

        String requestJson = objectMapper.writeValueAsString(allIds);

        long initialHistoryCount = historyRepository.count();

        MvcResult result = mockMvc.perform(post("/documents/send-submission/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<DocumentProcessingResultDto> webResponse = objectMapper.readValue(
                json,
                new TypeReference<List<DocumentProcessingResultDto>>() {
                }
        );

        long successCount = webResponse.stream()
                .filter(r -> "SUCCESS".equals(r.getStatus()))
                .count();

        assertThat(successCount)
                .as("Success count should match DRAFT documents count")
                .isEqualTo(draftCountBefore);

        List<Document> allDocsAfter = documentRepository.findAll();

        List<Document> draftedDocs = allDocsAfter.stream()
                .filter(d -> statusBefore.get(d.getId()).equals(DocumentStatus.DRAFT))
                .toList();

        assertThat(draftedDocs)
                .as("All DRAFT documents should be changed to SUBMITTED")
                .extracting(Document::getStatus)
                .containsOnly(DocumentStatus.SUBMITTED);

        List<Document> nonDraftDocs = allDocsAfter.stream()
                .filter(d -> !statusBefore.get(d.getId()).equals(DocumentStatus.DRAFT))
                .toList();

        for (Document doc : nonDraftDocs) {
            assertThat(doc.getStatus())
                    .as("Non-DRAFT document %s should not change status", doc.getId())
                    .isEqualTo(statusBefore.get(doc.getId()));
        }

        long historyCountAfter = historyRepository.count();
        assertThat(historyCountAfter)
                .as("History should be created for each successful update")
                .isEqualTo(initialHistoryCount + successCount);

        List<History> newHistory = historyRepository.findAll();
        assertThat(newHistory)
                .extracting(History::getAction)
                .containsOnly(Action.SUBMIT);

    }
}
