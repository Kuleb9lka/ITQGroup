package com.ITQGroup.dto.history;

import com.ITQGroup.enums.Action;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequestDto {

    @NotNull(message = "Action can't be null")
    private Action action;

    @Size(max = 1000, message = "Max comment size 1000 symbols")
    private String comment;
}
