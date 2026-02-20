package com.ITQGroup.dto.history;

import com.ITQGroup.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDto {

    private Long id;

    private Long authorId;

    private LocalDateTime updateDate;

    private Action action;

    private String comment;
}
