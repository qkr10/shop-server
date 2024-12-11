package com.capstone.shop.user.v1.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilterType {
    NEGO("nego", "negotiationAvailable", QueryType.EQUAL),
    WISH("wish", "wish", QueryType.GREATER_OR_EQUAL),
    REPU("repu", "register.reputation", QueryType.GREATER_OR_EQUAL),
    TRAN("tran", "transactionMethod", QueryType.EQUAL),
    STAT("stat", "merchandiseState", QueryType.EQUAL);
    private final String filterName, columnName;
    private final QueryType query;
    public static FilterType findByFilterName(String filterName) {
        for (FilterType filterType : FilterType.values()) {
            if (filterType.filterName.equals(filterName)) {
                return filterType;
            }
        }
        return null;
    }
}