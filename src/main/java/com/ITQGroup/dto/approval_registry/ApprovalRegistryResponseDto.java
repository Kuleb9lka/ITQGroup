package com.ITQGroup.dto.approval_registry;

import com.ITQGroup.dto.document.DocumentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRegistryResponseDto {

    private Long id;

    private DocumentResponseDto document;

    private Long authorId;

    private LocalDateTime approvedAt;
}
