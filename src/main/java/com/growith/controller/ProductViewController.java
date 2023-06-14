package com.growith.controller;

import com.growith.domain.Image.FileInfo;
import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductService productService;

    @GetMapping
    public String products(Model model, Pageable pageable) {

        Page<ProductGetResponse> products = productService.getAllProductsWithImage(pageable);
        model.addAttribute("products", products);
        return "products/main";
    }

    @GetMapping("/add")
    public String addProducts() {
        return "products/add";
    }

    @GetMapping("/{productId}")
    public String read(@PathVariable(name = "productId") Long productId, Model model) {
        ProductGetResponse product = productService.getProductWithImage(productId);
        model.addAttribute("product", product);
        return "products/detail";
    }
}
