package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.Menu;
import com.blibli.future.project1.model.MenuCategory;
import com.blibli.future.project1.model.MenuStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    public Long countAllByMenuCategory_MenuCategoryId(Integer menuCategoryId);
    public List<Menu> findMenusByMenuCategory_MenuCategoryId(Integer menuCategoryId);
    public Page<Menu> findMenusByMenuCategory_MenuCategoryId(Integer menuCategoryId, Pageable pageable);
    public Page<Menu> findMenusByMenuStatus(MenuStatus menuStatus, Pageable pageable);

    @Query("select m from Menu m where m.menuName like %:searchText%")
    public Page<Menu> searchMenus(@Param("searchText") String searchText, Pageable pageable);

    @Query("select m from Menu m where m.menuCategory.menuCategoryId = :menuCategoryId and " +
            "m.menuName like %:searchText%")
    public Page<Menu> searchMenus(@Param("searchText") String searchText,
                                  @Param("menuCategoryId") Integer menuCategoryId,
                                  Pageable pageable);

    @Query("select m from Menu m where m.menuStatus = :menuStatus and " +
            "m.menuName like %:searchText%")
    public Page<Menu> searchMenus(@Param("searchText") String searchText,
                                  @Param("menuStatus") MenuStatus menuStatus,
                                  Pageable pageable);
}
