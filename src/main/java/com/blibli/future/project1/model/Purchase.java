package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity // Menandakan bahwa class ini adalah suatu entitas, entitas: objek yang memiliki atribut
@Table(name = "purchase") // Menandakan bahwa class ini akan dibuat menjadi tabel dengan nama purchase
public class Purchase implements Serializable {

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama purchase_id, dan merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private int purchaseId;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom purchase_date, dan kolom ini tidak boleh kosong
     */
    @Column(name = "purchase_date", nullable = false)
    private Date purchaseDate;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom purchase_table, dan kolom ini tidak boleh kososng
     */
    @Column(name = "purchase_table", nullable = false)
    private int purchaseTable;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom purchase_waiting_time, dan kolom ini tidak boleh kosong
     */
    @Column(name = "purchase_waiting_time", nullable = false)
    private int purchaseWaitingTime;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom purchase_total, dan kolom ini tidak boleh kososng
     */
    @Column(name = "purchase_total", nullable = false)
    private int purchaseTotal;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom purchase_status, dan kolom ini tidak boleh kosong
     */
    @Column(name = "purchase_status", nullable = false)
    private PurchaseStatus purchaseStatus;

    /**
     * Menandakan bahwa tabel ini memiliki relasi dengan tabel app_user, dengan foreign key kolom user_id
     * Relasinya adalah many to one
     * Many di purchase, one di user --> satu user bisa memiliki banyak purchase (pembelian)
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User cashier;

    /**
     * Menandakan bahwa tabel ini memiliki relasi dengan tabel purchase_item
     * Relasinya adalah one to many
     * one di purchase many di purchaseItem, satu purchase bisa memiliki banyak purchaseItem
     * ada @Transient list<PurchaseItem> (bidirectional) gunanya untuk mengambil list purchaseItem yang ada pada purchase sekaligus
     */
    @Transient
    private List<PurchaseItem> purchaseItems;

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseTable() {
        return purchaseTable;
    }

    public void setPurchaseTable(int purchaseTable) {
        this.purchaseTable = purchaseTable;
    }

    public int getPurchaseWaitingTime() {
        return purchaseWaitingTime;
    }

    public void setPurchaseWaitingTime(int purchaseWaitingTime) {
        this.purchaseWaitingTime = purchaseWaitingTime;
    }

    public int getPurchaseTotal() {
        return purchaseTotal;
    }

    public void setPurchaseTotal(int purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public PurchaseStatus getPurchaseStatus() {
        return purchaseStatus;
    }

    public void setPurchaseStatus(PurchaseStatus purchaseStatus) {
        this.purchaseStatus = purchaseStatus;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public void setPurchaseItems(List<PurchaseItem> purchaseItems) {
        this.purchaseItems = purchaseItems;
    }

    public List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    @Override
    public String toString() {
        return purchaseId + ". " + purchaseDate;
    }
}
