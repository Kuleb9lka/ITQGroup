package com.ITQGroup.dto.document;

import com.ITQGroup.dto.history.HistoryResponseDto;
import com.ITQGroup.enums.DocumentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDto {

    private Long id;

    private UUID uniqueNumber;

    private Long authorId;

    private String name;

    private DocumentStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updateDate;

    private List<HistoryResponseDto> historyList;
}
