package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.MenuSold;
import com.blibli.future.project1.model.PurchaseItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Integer> {

    public Long countAllByPurchase_PurchaseId(Integer purchaseId);
    public Long countAllByMenu_MenuId(Integer menuId);
    public List<PurchaseItem> findPurchaseItemsByPurchase_PurchaseId(Integer purchaseId);
    public List<PurchaseItem> findPurchaseItemsByMenu_MenuId(Integer menuId);
    public void deleteAllByPurchase_PurchaseId(Integer purchaseId);

    @Query("select sum(p.purchaseItemQuantity) from PurchaseItem p")
    public Long getTotalPurchaseItemQuantity();

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public List<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity();

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where m.menuCategory.menuCategoryId = :menuCategoryId and p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public List<MenuSold> getAllPurchaseItemWithPurchaseItemQuantityByMenuCategoryId(@Param("menuCategoryId") Integer menuCategoryId);

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public Page<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity(Pageable pageable);

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where m.menuCategory.menuCategoryId = :menuCategoryId and p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public Page<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity(@Param("menuCategoryId") Integer menuCategoryId,
                                                                     Pageable pageable);

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where m.menuName like %:searchText% and p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public Page<MenuSold> searchAllPurchaseItemWithPurchaseItemQuantity(@Param("searchText") String searchText,
                                                                        Pageable pageable);

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where m.menuName like %:searchText% and m.menuCategory.menuCategoryId = :menuCategoryId and p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public Page<MenuSold> searchAllPurchaseItemWithPurchaseItemQuantity(@Param("searchText") String searchText,
                                                                        @Param("menuCategoryId") Integer menuCategoryId,
                                                                        Pageable pageable);

    @Query("select new com.blibli.future.project1.model.MenuSold(m , sum(p.purchaseItemQuantity)) " +
            "from Menu m, PurchaseItem p " +
            "where m.menuId = :menuId and p.menu.menuId = m.menuId " +
            "group by m.menuId")
    public MenuSold getPurchaseItemWithPurchaseItemQuantityByMenuId(@Param("menuId") Integer menuId);

}
