package com.ITQGroup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {

    private List<T> content;

    private Long totalElements;

    private Integer totalPages;

    private Integer page;

    private Integer size;
}
