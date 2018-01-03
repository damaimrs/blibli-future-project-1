package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Integer> {

    @Query("select mc from MenuCategory mc where mc.menuCategoryName like %:searchText%")
    public Page<MenuCategory> searchMenuCategories(@Param("searchText") String searchText, Pageable pageable);
}
