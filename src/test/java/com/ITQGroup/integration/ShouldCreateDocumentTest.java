package com.ITQGroup.integration;

import com.ITQGroup.AbstractIntegrationTest;
import com.ITQGroup.entity.Document;
import com.ITQGroup.enums.DocumentStatus;
import com.ITQGroup.reposiroty.DocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ShouldCreateDocumentTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentRepository documentRepository;


    @Test
    @DisplayName("Should create document")
    void create_Success() throws Exception {

        String requestJson = """
        {
            "authorId": 12,
            "name": "Some test name"
        }
        """;

        mockMvc.perform(post("/documents/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        assertThat(documentRepository.findAll()).hasSize(1);

        Document document = documentRepository.findAll().get(0);
        assertThat(document.getName()).isEqualTo("Some test name");
        assertThat(document.getAuthorId()).isEqualTo(12L);
        assertThat(document.getStatus()).isEqualTo(DocumentStatus.DRAFT);
        assertThat(document.getUpdateDate()).isNull();
        assertThat(document.getCreateDate()).isNotNull();
        assertThat(document.getUniqueNumber()).isNotNull();
    }


}

