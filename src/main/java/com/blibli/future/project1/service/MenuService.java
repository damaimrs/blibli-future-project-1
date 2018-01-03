package com.blibli.future.project1.service;

import com.blibli.future.project1.model.Menu;
import com.blibli.future.project1.model.MenuStatus;
import com.blibli.future.project1.repository.MenuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class MenuService {

    private MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public List<Menu> findAllMenuByMenuCategoryId(Integer menuCategoryId) {
        return menuRepository.findMenusByMenuCategory_MenuCategoryId(menuCategoryId);
    }

    public Page<Menu> findAllMenuByMenuCategoryId(Integer menuCategoryId, Pageable pageable) {
        return menuRepository.findMenusByMenuCategory_MenuCategoryId(menuCategoryId, pageable);
    }

    public Page<Menu> findAllMenuByMenuStatus(MenuStatus menuStatus, Pageable pageable) {
        return menuRepository.findMenusByMenuStatus(menuStatus, pageable);
    }

    public Page<Menu> searchMenus(String searchText, Pageable pageable) {
        return menuRepository.searchMenus(searchText, pageable);
    }

    public Page<Menu> searchMenus(String searchText, Integer menuCategoryId, Pageable pageable) {
        return menuRepository.searchMenus(searchText, menuCategoryId, pageable);
    }

    public Page<Menu> searchMenus(String searchText, MenuStatus menuStatus, Pageable pageable) {
        return menuRepository.searchMenus(searchText, menuStatus, pageable);
    }

    public Long getMenuCount() {
        return menuRepository.count();
    }

    public Long getMenuCountByMenuCategoryId(Integer menuCategoryId) {
        return menuRepository.countAllByMenuCategory_MenuCategoryId(menuCategoryId);
    }

    public Page<Menu> findAllPageable(Pageable pageable) {
        return menuRepository.findAll(pageable);
    }

    public Menu findMenuById(Integer id) {
        return menuRepository.findOne(id);
    }

    public Menu addMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    public void deleteMenu(Integer menuId) {
        menuRepository.delete(menuId);
    }
}
