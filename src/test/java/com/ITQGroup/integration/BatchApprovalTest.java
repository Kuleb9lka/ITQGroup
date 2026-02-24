package com.ITQGroup.integration;

import com.ITQGroup.AbstractIntegrationTest;
import com.ITQGroup.entity.ApprovalRegistry;
import com.ITQGroup.entity.Document;
import com.ITQGroup.entity.History;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.reposiroty.ApprovalRegistryRepository;
import com.ITQGroup.reposiroty.DocumentRepository;
import com.ITQGroup.reposiroty.HistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class BatchApprovalTest extends AbstractIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ApprovalRegistryRepository approvalRegistryRepository;

    void setUpSuccess() {

        Document document1 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);
        Document document2 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);
        Document document3 = new Document(null, UUID.randomUUID(), 12L, "some name", DocumentStatus.SUBMITTED, LocalDateTime.now(), null, List.of(), 0L);

        documentRepository.saveAll(List.of(document1, document2, document3));
    }

    @Test
    @DisplayName("Should make batch update to approval status")
    void batchApproval_Success() throws Exception {

        setUpSuccess();

        String requestJson = """
                [1, 2, 3]
                """;

        mockMvc.perform(post("/documents/send-approval/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        documentRepository.findAll().forEach(document -> {
            assertThat(document.getStatus()).isEqualTo(DocumentStatus.APPROVED);
            assertThat(document.getId()).isNotNull();
        });

        assertThat(approvalRegistryRepository.findAll()).hasSize(3);
        assertThat(historyRepository.findAll()).hasSize(3);
    }


}
