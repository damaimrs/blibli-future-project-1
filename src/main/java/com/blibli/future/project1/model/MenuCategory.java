package com.blibli.future.project1.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity // Menandakan bahwa class ini merupakan suatu entitas, entitas: objek yang memiliki atribut
@Table(name = "menu_category") // Menandakan bahwa class ini akan dibuat tabel dengan nama yang telah di set
public class MenuCategory implements Serializable {

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama menu_category_id, dan merupakan primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_id")
    private int menuCategoryId;

    /**
     * Menandakan bahwa variabel ini akan dibuat menjadi kolom dengan nama menu_category_name, dan merupakan kolom yang unik dan tidak boleh kososng
     */
    @Column(name = "menu_category_name", unique = true, nullable = false)
    private String menuCategoryName;

    public int getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(int menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    /**
     * menulis ulang fungsi yang telah dideclare
     */
    @Override
    public String toString() {
        return menuCategoryName;
    }
}
