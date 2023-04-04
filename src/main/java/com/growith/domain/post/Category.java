package com.growith.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Category {
    QNA, COMMUNITY, STUDY, NOTICE;

    public static Category create(String requestCategory) {
        for (Category value : Category.values()) {
            if (value.toString().equals(requestCategory)) {
                return value;
            }
        }
        throw new IllegalStateException("일치하는 카테고리가 존재하지 않습니다.");
    }

    @JsonCreator
    public static Category parsing(String inputValue) {
        return Stream.of(Category.values())
                .filter(category -> category.toString().equals(inputValue.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
