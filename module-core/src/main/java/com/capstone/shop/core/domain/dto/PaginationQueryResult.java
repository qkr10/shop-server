package com.capstone.shop.core.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginationQueryResult<T> {
    List<T> data;
    long totalPage;
}