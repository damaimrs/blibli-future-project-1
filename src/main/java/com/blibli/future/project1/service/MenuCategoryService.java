package com.blibli.future.project1.service;

import com.blibli.future.project1.model.MenuCategory;
import com.blibli.future.project1.repository.MenuCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class MenuCategoryService {

    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    public MenuCategoryService(MenuCategoryRepository menuCategoryRepository) {
        this.menuCategoryRepository = menuCategoryRepository;
    }

    public List<MenuCategory> findAll() {
        return menuCategoryRepository.findAll();
    }

    public Page<MenuCategory> findAllPageable(Pageable pageable) {
        return menuCategoryRepository.findAll(pageable);
    }

    public Page<MenuCategory> searchMenuCategories(String searchText, Pageable pageable) {
        return menuCategoryRepository.searchMenuCategories(searchText, pageable);
    }

    public MenuCategory addMenuCategory(MenuCategory menuCategory) {
        return menuCategoryRepository.save(menuCategory);
    }

    public MenuCategory findMenuCategoryById(Integer menuCategoryId) {
        return menuCategoryRepository.findOne(menuCategoryId);
    }

    public Long getMenuCategoryCount() {
        return menuCategoryRepository.count();
    }

    public void deleteMenuCategory(Integer menuCategoryId) {
        menuCategoryRepository.delete(menuCategoryId);
    }
}
