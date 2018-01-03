package com.blibli.future.project1.service;

import com.blibli.future.project1.model.MenuSold;
import com.blibli.future.project1.model.PurchaseItem;
import com.blibli.future.project1.repository.PurchaseItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PurchaseItemService {

    private PurchaseItemRepository purchaseItemRepository;

    public PurchaseItemService(PurchaseItemRepository purchaseItemRepository) {
        this.purchaseItemRepository = purchaseItemRepository;
    }

    public List<PurchaseItem> findAll() {
        return purchaseItemRepository.findAll();
    }

    public List<PurchaseItem> findAllPurchaseItemByPurchaseId(Integer purchaseId) {
        return purchaseItemRepository.findPurchaseItemsByPurchase_PurchaseId(purchaseId);
    }

    public List<PurchaseItem> findAllPurchaseItemByMenuId(Integer menuId) {
        return purchaseItemRepository.findPurchaseItemsByMenu_MenuId(menuId);
    }

    public Long getPurchaseItemCount() {
        return purchaseItemRepository.count();
    }



    public Long getPurchaseItemCountByPurchaseId(Integer purchaseId) {
        return purchaseItemRepository.countAllByPurchase_PurchaseId(purchaseId);
    }

    public Long getPurchaseItemCountByMenuId(Integer menuId) {
        return purchaseItemRepository.countAllByMenu_MenuId(menuId);
    }

    public Page<PurchaseItem> findAllPageable(Pageable pageable) {
        return purchaseItemRepository.findAll(pageable);
    }

    public PurchaseItem findPurchaseItemById(int id) {
        return purchaseItemRepository.findOne(id);
    }

    public PurchaseItem addPurchaseItem(PurchaseItem purchaseItem) {
        return purchaseItemRepository.save(purchaseItem);
    }

    public void deletePurchaseItem(Integer purchaseItemId) {
        purchaseItemRepository.delete(purchaseItemId);
    }

    public void deletePurchaseItemByPurchaseId(Integer purchaseId) {
        purchaseItemRepository.deleteAllByPurchase_PurchaseId(purchaseId);
    }

    public Long getTotalPurchaseItemQuantity() {
        return purchaseItemRepository.getTotalPurchaseItemQuantity();
    }

    public List<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity() {
        return purchaseItemRepository.getAllPurchaseItemWithPurchaseItemQuantity();
    }

    public List<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity(Integer menuCategoryId) {
        return purchaseItemRepository.getAllPurchaseItemWithPurchaseItemQuantityByMenuCategoryId(menuCategoryId);
    }

    public Page<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity(Pageable pageable) {
        return purchaseItemRepository.getAllPurchaseItemWithPurchaseItemQuantity(pageable);
    }

    public Page<MenuSold> getAllPurchaseItemWithPurchaseItemQuantity(Integer menuCategoryId, Pageable pageable) {
        return purchaseItemRepository.getAllPurchaseItemWithPurchaseItemQuantity(menuCategoryId, pageable);
    }

    public Page<MenuSold> searchAllPurchaseItemWithPurchaseItemQuantity(String searchText, Pageable pageable) {
        return purchaseItemRepository.searchAllPurchaseItemWithPurchaseItemQuantity(searchText, pageable);
    }

    public Page<MenuSold> searchAllPurchaseItemWithPurchaseItemQuantity(String searchText, Integer menuCategoryId,
                                                                        Pageable pageable) {
        return purchaseItemRepository.searchAllPurchaseItemWithPurchaseItemQuantity(searchText, menuCategoryId, pageable);
    }

    public MenuSold getPurchaseItemWithPurchaseItemQuantityByMenuId(Integer menuId) {
        return purchaseItemRepository.getPurchaseItemWithPurchaseItemQuantityByMenuId(menuId);
    }
}
