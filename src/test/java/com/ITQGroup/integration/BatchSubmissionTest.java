package com.ITQGroup.integration;

import com.ITQGroup.AbstractIntegrationTest;
import com.ITQGroup.dto.document.DocumentProcessingResultDto;
import com.ITQGroup.entity.Document;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.reposiroty.DocumentRepository;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
public class BatchSubmissionTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DocumentRepository repository;


    void setUpSuccess() {

        Document document1 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, null, 0L);
        Document document2 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, null, 0L);
        Document document3 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, null, 0L);

        repository.saveAll(List.of(document1, document2, document3));
    }

    void setUpSuccessPartialSuccess() {

        Document document1 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.DRAFT, LocalDateTime.now(), null, List.of(), 0L);
        Document document2 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.APPROVED, LocalDateTime.now(), null, List.of(), 0L);
        Document document3 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);

        repository.saveAll(List.of(document1, document2, document3));
    }

    @Test
    @DisplayName("Should make batch update to submission status")
    void batchSubmission_Success() throws Exception {

        setUpSuccess();

        String requestJson = """
                [1, 2, 3]
                """;

        mockMvc.perform(post("/documents/send-submission/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        repository.findAll().forEach(document -> {
            assertThat(document.getStatus()).isEqualTo(DocumentStatus.SUBMITTED);
            assertThat(document.getId()).isNotNull();
        });
    }

    @Test
    @DisplayName("Should make batch update with partial success")
    void batchSubmission_PartialSuccess() throws Exception {

        setUpSuccessPartialSuccess();

        String requestJson = """
                [1, 2, 3]
                """;

        MvcResult result = mockMvc.perform(post("/documents/send-submission/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        List<DocumentProcessingResultDto> webResponse =
                objectMapper.readValue(json,
                        new TypeReference<List<DocumentProcessingResultDto>>() {});

        List<Document> all = repository.findAll();
        assertThat(all)
                .extracting(Document::getStatus)
                .containsExactlyInAnyOrder(DocumentStatus.APPROVED, DocumentStatus.SUBMITTED, DocumentStatus.SUBMITTED);

        assertThat(webResponse)
                .extracting(r -> tuple(r.getDocumentId(), r.getStatus()))
                .containsExactlyInAnyOrder(
                        tuple(1L, "SUCCESS"),
                        tuple(2L, "CONFLICT"),
                        tuple(3L, "CONFLICT")
                );
    }
}
