package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity // Menandakan bahwa class ini merupakan entitas, entitas: objek yang memiliki atribut
@Table(name = "purchase_item") //  Menandakan bahwa class ini akan dibuat tabelnya dengan nama purchase_item
public class PurchaseItem implements Serializable {

    /**
     * Menandakan bahwa variabel ini akan dibuat kolomnya dengan nama purchase_item_id, dan kolom ini merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_item_id")
    private int purchaseItemId;

    /**
     * Menandakan bahwa variabel ini akan dibuat kolomnya dengan nama purchase_item_quantity, dan kolom ini tidak boleh kososng
     */
    @Column(name = "purchase_item_quantity", nullable = false)
    private int purchaseItemQuantity;

    /**
     * Menandakan bahwa tabel ini memiliki relasi dengan tabel purchase, dengan foreign key purchase_id
     * Relasinya adalah many to one
     * many di purchaseItem one di purchase --> satu purchase bisa memiliki banyak purchaseItem
     */
    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    /**
     * Menandakan bahwa tabel ini memiliki relasi dengan tabel menu, dengan foreign key menu_id
     * Relasinya adalah many to one
     * many di purchaseItem one di menu --> satu menu bisa ada di banyak purchaseItem
     */
    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public int getPurchaseItemId() {
        return purchaseItemId;
    }

    public void setPurchaseItemId(int purchaseItemId) {
        this.purchaseItemId = purchaseItemId;
    }

    public int getPurchaseItemQuantity() {
        return purchaseItemQuantity;
    }

    public void setPurchaseItemQuantity(int purchaseItemQuantity) {
        this.purchaseItemQuantity = purchaseItemQuantity;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return menu.toString() + " : " + purchaseItemQuantity;
    }
}
