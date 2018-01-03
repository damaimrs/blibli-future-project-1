package com.blibli.future.project1.api;

import com.blibli.future.project1.model.MenuSold;
import com.blibli.future.project1.model.Response;
import com.blibli.future.project1.model.User;
import com.blibli.future.project1.service.PurchaseItemService;
import com.blibli.future.project1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class PurchaseItemAPI {

    private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseItemAPI.class);

    private PurchaseItemService purchaseItemService;
    private UserService userService;

    private final String[] SORT_PROPERTIES = {
            "menuName",
            "menuPrice",
            "menuWaitingTime"
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    @Autowired
    public PurchaseItemAPI(PurchaseItemService purchaseItemService, UserService userService) {
        this.purchaseItemService = purchaseItemService;
        this.userService = userService;
    }

    //fungsi untuk mendapatkan jumlah total produk terjual
    @GetMapping("/api/purchaseitem/quantity/total")
    public ResponseEntity<Response<Long>> purchaseItemTotalQuantity(@RequestParam Integer userId) {
        String message;
        Long totalQuantity;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            totalQuantity = null;
        } else {
            message = "Access success";
            totalQuantity = purchaseItemService.getTotalPurchaseItemQuantity();

            if (totalQuantity == null)
                totalQuantity = 0L;
        }

        Response<Long> response = new Response<>(message, totalQuantity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/purchaseitem/menusold/all")
    public ResponseEntity<Response<ArrayList<MenuSold>>> menuSoldListAll(@RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<MenuSold> menuSolds;
        String message;

        if (user == null) {
            message = "Access denied";
            menuSolds = null;
        } else {
            message = "Find Menu Solds success";
            menuSolds = new ArrayList<>(purchaseItemService.getAllPurchaseItemWithPurchaseItemQuantity());
        }

        Response<ArrayList<MenuSold>> response = new Response<>(message, menuSolds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/purchaseitem/menusold/{menuId}")
    public ResponseEntity<Response<MenuSold>> menuSoldByMenuId(@PathVariable Integer menuId,
                                                                          @RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        MenuSold menuSold;
        String message;

        if (user == null) {
            message = "Access denied";
            menuSold = null;
        } else {
            message = "Find Menu Sold success";
            menuSold = purchaseItemService.getPurchaseItemWithPurchaseItemQuantityByMenuId(menuId);
        }

        Response<MenuSold> response = new Response<>(message, menuSold);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakan untuk menampilkan menu yang terjual
    @GetMapping("/api/purchaseitem/menusold")
    public ResponseEntity<Response<ArrayList<MenuSold>>> menuSoldList(@RequestParam Integer userId,
                                                              @RequestParam(required = false) Integer filterBy,
                                                              @RequestParam(required = false) String searchText,
                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false, defaultValue = "1") Integer sort) {
        User user = userService.findUserById(userId);
        ArrayList<MenuSold> menuSolds;  //membuat list menuSold
        String message;

        if (user == null) {
            message = "Access denied";
            menuSolds = null;
        } else {
            //set sortPropertiesIndex = 0 --> default sorting berdasarkan menuName
            int sortPropertiesIndex = 0;
            //set sortDirectionIndex = 0 --> default arah sorting asc
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;

            //setting ukuran data per halaman
            if (pageSize <= 0)
                pageSize = 10;
            //jika ada sorting
            if (sort >= 1 && sort <= 6) {
                //perhitungan untuk menentukan patokan kolom yg akan disort dan arah sorting
                boolean isPrime = sort % 2 == 0;
                sortDirectionIndex = isPrime ? 1 : 0;

                sortPropertiesIndex = (int) (Math.ceil((double) sort / 2) - 1);
            }

            ArrayList<String> sortProperties = new ArrayList<>();
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);
            //sorting berdasarkan sortProperties dan sortDirection
            Sort sortObj = new Sort(sortDirection, sortProperties);
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);

            if (searchText != null && filterBy != 0) {
                //kondisi jika dilakukan filter dan search
                menuSolds = new ArrayList<>(purchaseItemService.searchAllPurchaseItemWithPurchaseItemQuantity(searchText, filterBy, pageRequest).getContent());
            } else {
                if (searchText != null) {
                    //kondisi jika dilakukan search saja
                    menuSolds = new ArrayList<>(purchaseItemService.searchAllPurchaseItemWithPurchaseItemQuantity(searchText, pageRequest).getContent());
                } else {
                    if (filterBy != 0){
                        //kondisi jika dilakukan flter saja
                        menuSolds = new ArrayList<>(purchaseItemService.getAllPurchaseItemWithPurchaseItemQuantity(filterBy, pageRequest).getContent());}
                    else{
                        //kondisi jika tidak dilakukan search dan filter (getAll data)
                        menuSolds = new ArrayList<>(purchaseItemService.getAllPurchaseItemWithPurchaseItemQuantity(pageRequest).getContent());}
                }
            }

            message = "Find menu solds success";
        }

        Response<ArrayList<MenuSold>> response = new Response<>(message, menuSolds);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
