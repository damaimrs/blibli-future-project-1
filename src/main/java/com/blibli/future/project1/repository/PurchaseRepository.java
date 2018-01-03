package com.blibli.future.project1.repository;

import com.blibli.future.project1.model.Purchase;
import com.blibli.future.project1.model.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    public Long countAllByPurchaseDateBetween(Date startDate, Date endDate);

    public Long countAllByPurchaseStatus(PurchaseStatus purchaseStatus);

    public Long countAllByPurchaseStatusAndPurchaseTable(PurchaseStatus purchaseStatus, Integer purchaseTable);

    public List<Purchase> findPurchasesByCashier_UserId(Integer cashierId);

    public List<Purchase> findPurchasesByPurchaseStatus(PurchaseStatus purchaseStatus);

    public Page<Purchase> findPurchasesByPurchaseStatus(PurchaseStatus purchaseStatus, Pageable pageable);

    public Page<Purchase> findPurchasesByPurchaseStatusAndPurchaseDateBetween(PurchaseStatus purchaseStatus,
                                                                              Date startDate,
                                                                              Date endDate,
                                                                              Pageable pageable);

    @Query("select sum(p.purchaseTotal) from Purchase p")
    public Long getTotalPurchaseTotal();

    @Query("select sum(p.purchaseTotal) from Purchase p where p.purchaseStatus = :purchaseStatus")
    public Long getTotalPurchaseTotalByPurchaseStatus(@Param("purchaseStatus") PurchaseStatus purchaseStatus);

    @Query("select sum(p.purchaseTotal) from Purchase p where p.purchaseDate between :startDate and :endDate")
    public Long getTotalPurchaseTotalByPurchaseDateBetween(@Param("startDate") Date startDate,
                                                           @Param("endDate") Date endDate);

    @Query("select sum(p.purchaseTotal) from Purchase p where " +
            "p.purchaseStatus = :purchaseStatus and " +
            "p.purchaseDate between :startDate and :endDate")
    public Long getTotalPurchaseTotalByPurchaseStatusAndPurchaseDateBetween(@Param("purchaseStatus") PurchaseStatus purchaseStatus,
                                                                            @Param("startDate") Date startDate,
                                                                            @Param("endDate") Date endDate);
}
