package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity // Menandakan bahwa class ini merupakan sebuah entitas, entitas : objek yang memiliki atribut
@Table(name = "menu") // Menandakan bahwa class ini akan dibuat tabelnya di database dengan nama yang telah diset
public class Menu implements Serializable {

    /**
     * Menandakan bahwa kolom ini merupakan kolom menu_id dan merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private int menuId;

    /**
     * Menandakan bahwa kolom ini merupakan kolom menu_name, dan kolom ini unik juga tidak boleh kosong
     */
    @Column(name = "menu_name", unique = true, nullable = false)
    private String menuName;

    /**
     * Menandakan bahwa kolom ini merupakan kolom menu_price, dan kolom ini tidak boleh kosong
     */
    @Column(name = "menu_price", nullable = false)
    private int menuPrice;

    /**
     * Menandakan bahwa kolom ini merupakan kolom menu_waiting_time, dan kolom ini tidak boleh kosong
     */
    @Column(name = "menu_waiting_time", nullable = false)
    private int menuWaitingTime;

    /**
     * Menandakan bahwa kolom ini merupakan kolom menu_status, dan kolom ini tidak boleh kosong
     */
    @Column(name = "menu_status", nullable = false)
    private MenuStatus menuStatus;

    /**
     * Menandakan bahwa tabel ini memiliki relasi ke tabel menu_category dengan foreign_key menu_category_id
     * Relasi tabel ini adalah many to one
     * Many di menu, one di kategori menu --> satu kategori menu bisa memiliki banyak menu
     */
    @ManyToOne
    @JoinColumn(name = "menu_category_id")
    private MenuCategory menuCategory;

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public int getMenuWaitingTime() {
        return menuWaitingTime;
    }

    public void setMenuWaitingTime(int menuWaitingTime) {
        this.menuWaitingTime = menuWaitingTime;
    }

    public MenuStatus getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(MenuStatus menuStatus) {
        this.menuStatus = menuStatus;
    }

    public MenuCategory getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.menuCategory = menuCategory;
    }

    /**
     * menulis ulang fungsi yang telah dideclare
     */
    @Override
    public String toString() {
        return menuName;
    }
}
