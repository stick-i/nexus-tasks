package com.example.todo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.todo.entity.Category;
import com.example.todo.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    public List<Category> getCategoriesByUserId(Long userId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }

    public Category createCategory(Category category) {
        save(category);
        return category;
    }

    public Category updateCategory(Long id, Category category) {
        category.setId(id);
        updateById(category);
        return category;
    }

    public void deleteCategory(Long id) {
        removeById(id);
    }
}