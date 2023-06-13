package com.growith.controller;

import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductService productService;

    @GetMapping
    public String products(Model model, Pageable pageable) {

        Page<ProductGetResponse> products = productService.getProducts(pageable);

        model.addAttribute("products", products);
        return "products/main";
    }

    @GetMapping("/add")
    public String addProducts() {
        return "products/add";
    }
}
