package com.growith.global.config.enumConverter;

import com.growith.domain.post.Category;
import org.springframework.core.convert.converter.Converter;

public class PostEnumConverter implements Converter<String, Category> {

    @Override
    public Category convert(String requestCategory) {
        return Category.create(requestCategory.toUpperCase());
    }
}
