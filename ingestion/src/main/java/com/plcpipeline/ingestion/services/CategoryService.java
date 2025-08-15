package com.plcpipeline.ingestion.services;

import com.plcpipeline.ingestion.dtos.CategoryDto;
import com.plcpipeline.ingestion.entities.Category;
import com.plcpipeline.ingestion.mapper.Mapper;
import com.plcpipeline.ingestion.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(Mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        return Mapper.toCategoryDto(category);
    }

    public CategoryDto createCategory(CategoryDto dto) {
        Category category = Mapper.toCategoryEntity(dto);
        category = categoryRepository.save(category);
        return Mapper.toCategoryDto(category);
    }

    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category = categoryRepository.save(category);
        return Mapper.toCategoryDto(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
