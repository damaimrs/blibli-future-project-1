package com.blibli.future.project1.model;

import java.io.Serializable;

public class MenuSold implements Serializable {

    private Menu menu;
    private long quantity;

    public MenuSold() {

    }

    public MenuSold(Menu menu, long quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
