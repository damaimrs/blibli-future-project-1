package com.blibli.future.project1.service;

import com.blibli.future.project1.model.Purchase;
import com.blibli.future.project1.model.PurchaseItem;
import com.blibli.future.project1.model.PurchaseStatus;
import com.blibli.future.project1.repository.PurchaseItemRepository;
import com.blibli.future.project1.repository.PurchaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class PurchaseService {

    private PurchaseRepository purchaseRepository;
    private PurchaseItemRepository purchaseItemRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, PurchaseItemRepository purchaseItemRepository) {
        this.purchaseRepository = purchaseRepository;
        this.purchaseItemRepository = purchaseItemRepository;
    }

    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    public List<Purchase> findAllPurchaseByCashierId(Integer userId) {
        return purchaseRepository.findPurchasesByCashier_UserId(userId);
    }

    public List<Purchase> findAllPurchaseByPurchaseStatus(PurchaseStatus purchaseStatus) {
        return purchaseRepository.findPurchasesByPurchaseStatus(purchaseStatus);
    }

    public Page<Purchase> findAllPurchaseByPurchaseStatus(PurchaseStatus purchaseStatus, Pageable pageable) {
        return purchaseRepository.findPurchasesByPurchaseStatus(purchaseStatus, pageable);
    }

    public Page<Purchase> findAllPurchaseByPurchaseStatus(PurchaseStatus purchaseStatus,
                                                          Date startDate,
                                                          Date endDate,
                                                          Pageable pageable) {
        return purchaseRepository.findPurchasesByPurchaseStatusAndPurchaseDateBetween(purchaseStatus,
                startDate, endDate, pageable);
    }

    public Long getPurchaseCount() {
        return purchaseRepository.count();
    }

    public Long getPurchaseCountByPurchaseStatus(PurchaseStatus purchaseStatus) {
        return purchaseRepository.countAllByPurchaseStatus(purchaseStatus);
    }

    public Long getPurchaseCountByPurchaseDate(Date startDate, Date endDate) {
        return purchaseRepository.countAllByPurchaseDateBetween(startDate, endDate);
    }

    public Long getPurchaseCountByPurchaseStatusAndPurchaseTable(PurchaseStatus purchaseStatus, Integer purchaseTable) {
        return purchaseRepository.countAllByPurchaseStatusAndPurchaseTable(purchaseStatus, purchaseTable);
    }

    public Page<Purchase> findAllPageable(Pageable pageable) {
        return purchaseRepository.findAll(pageable);
    }

    public Purchase findPurchaseById(int id) {
        return purchaseRepository.findOne(id);
    }

    public Purchase addPurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public Purchase addPurchaseWithPurchaseItem(Purchase purchase) {
        Purchase newPurchase = purchase;
        //menghapus item pemesanan di database kalau ada id pemesanan, kalo gaada id pemesanan ga dihapus tapi langsung disimpen
        if (purchase.getPurchaseId() != 0)  //get id, kalau sudah ada id pemesanan, item yg dipesan dihapus (untuk edit)
            purchaseItemRepository.deleteAllByPurchase_PurchaseId(purchase.getPurchaseId());

        purchase = purchaseRepository.save(purchase);

        if (purchase != null) {
            boolean isSuccess = true;
            //untuk save itemnya
            for (PurchaseItem purchaseItem : newPurchase.getPurchaseItems()) {
                if (purchaseItem != null) {
                    purchaseItem.setPurchase(newPurchase);
                    purchaseItem = purchaseItemRepository.save(purchaseItem);

                    isSuccess = purchaseItem != null;

                    if (!isSuccess)
                        break;
                }
            }

            if (!isSuccess) {
                purchase = null;
            }
        }

        return purchase;
    }

    public void deletePurchase(Integer purchaseId) {
        purchaseItemRepository.deleteAllByPurchase_PurchaseId(purchaseId);
        purchaseRepository.delete(purchaseId);
    }

    public Long getTotalPurchaseTotal() {
        return purchaseRepository.getTotalPurchaseTotal();
    }

    public Long getTotalPurchaseTotal(PurchaseStatus purchaseStatus) {
        return purchaseRepository.getTotalPurchaseTotalByPurchaseStatus(purchaseStatus);
    }

    public Long getTotalPurchaseTotal(Date startDate, Date endDate) {
        return purchaseRepository.getTotalPurchaseTotalByPurchaseDateBetween(startDate, endDate);
    }

    public Long getTotalPurchaseTotal(PurchaseStatus purchaseStatus, Date startDate, Date endDate) {
        return purchaseRepository.getTotalPurchaseTotalByPurchaseStatusAndPurchaseDateBetween(purchaseStatus, startDate, endDate);
    }
}
