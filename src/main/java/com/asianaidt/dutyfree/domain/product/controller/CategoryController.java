package com.asianaidt.dutyfree.domain.product.controller;

import com.asianaidt.dutyfree.domain.product.dto.CategoryListDto;
import com.asianaidt.dutyfree.domain.product.service.CategoryService;
import com.asianaidt.dutyfree.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    private final ProductService productService; // 테스트중

    /*
    전체 카테고리 반환
    category = CategoryListDto(id=1, name=bag, product=null)

    여기서 가져온 id로
     */
    @GetMapping("/category")
    public String getCategory(Model model) {
//    public ResponseEntity<?> getCategory(Model model){
        List<CategoryListDto> categoryList = categoryService.getAllCategory();

        for (CategoryListDto allCategory : categoryList) {
            System.out.println("allCategory = " + allCategory);
        }

        model.addAttribute("categoryList", categoryList);
        System.out.println("model = " + model);
        // html 변경가능
        return null;
//        return ResponseEntity.ok(categoryList);
    }

    @GetMapping("/category/{categoryNum}")
    public ResponseEntity<?> test(@PathVariable Long categoryNum) {
        return ResponseEntity.ok(categoryService.getCategory(categoryNum));
    }

    @GetMapping("/getusd")
    public ResponseEntity<?> test() {
        Integer res = productService.getUsd();

        return ResponseEntity.ok(res);
//        return null;
    }
}
