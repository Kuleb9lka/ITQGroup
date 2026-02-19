package com.ITQGroup.dto.document;

import com.ITQGroup.dto.history.HistoryResponseDto;
import com.ITQGroup.dto.user.UserResponseDto;
import com.ITQGroup.enums.DocumentStatus;
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

    private UserResponseDto user;

    private String name;

    private DocumentStatus status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private List<HistoryResponseDto> historyList;
}
