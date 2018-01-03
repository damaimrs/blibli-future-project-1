package com.blibli.future.project1.api;

import com.blibli.future.project1.model.*;
import com.blibli.future.project1.service.MenuService;
import com.blibli.future.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class MenuAPI {

    private MenuService menuService;
    private UserService userService;

    private final String[] SORT_PROPERTIES = {
            "menuName",
            "menuCategory.menuCategoryName",
            "menuPrice",
            "menuWaitingTime",
            "menuStatus"
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    private final MenuStatus[] MENU_STATUSES = {
            MenuStatus.AVAILABLE,
            MenuStatus.INAVAILABLE
    };

    @Autowired
    public MenuAPI(MenuService menuService, UserService userService) {
        this.menuService = menuService;
        this.userService = userService;
    }

    /**
     * Method yang dijalankan saat user melakukan request ke route "/api/menu/count"
     *
     * @param userId merupakan query string yang dikirim (userId)
     * @return adalah json dari response dengan data jumlah menu
     */
    @GetMapping("/api/menu/count")
    public ResponseEntity<Response<Long>> menuCount(@RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            message = "Access success";
            count = menuService.getMenuCount();
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/menu/all")
    public ResponseEntity<Response<ArrayList<Menu>>> menuListAll(@RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<Menu> menus;
        String message;

        if (user == null) {
            message = "Access denied";
            menus = null;
        } else {
            message = "Find menus success";
            menus = new ArrayList<>(menuService.findAll());
        }

        Response<ArrayList<Menu>> response = new Response<>(message, menus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API untuk mendapatkan menu yang berada pada kategori menu tertentu
    //bertipe GET
    @GetMapping("/api/menu/all/menu-category/{menuCategoryId}")
    public ResponseEntity<Response<ArrayList<Menu>>> menuListByMenuCategoryId(@PathVariable Integer menuCategoryId,
                                                                 @RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<Menu> menus;
        String message;

        if (user == null) {
            message = "Access denied";
            menus = null;
        } else {
            MenuCategory menuCategory = new MenuCategory();     //membuat object menuCategory
            menuCategory.setMenuCategoryId(menuCategoryId);     //mengeser menuCategoryId yg ada pada object menuCategory dngn parameter menuCategoryId

            message = "Find menus success";
            //mencari menu berdasarkan menuCategoryId melalui menuService
            menus = new ArrayList<>(menuService.findAllMenuByMenuCategoryId(menuCategory.getMenuCategoryId()));
        }

        Response<ArrayList<Menu>> response = new Response<>(message, menus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/menu")
    public ResponseEntity<Response<ArrayList<Menu>>> menuList(@RequestParam Integer userId,
                                                              @RequestParam(required = false) Integer filterBy,
                                                              @RequestParam(required = false) String searchText,
                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false, defaultValue = "1") Integer sort) {
        User user = userService.findUserById(userId); //ngecek user udah login apa belum
        ArrayList<Menu> menus;
        String message;

        if (user == null) {
            message = "Access denied";
            menus = null;
        } else {
            //sortPropertiesIndex = 0 --> sort berdasarkan menuName
            int sortPropertiesIndex = 0;
            //sortDirectionIndex = 0 --> asc
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;

            //ukuran jumlah data per halaman
            if (pageSize <= 0)
                pageSize = 10;
            //jika dilakukan sorting pada saat menampilkan menu
            if (sort >= 1 && sort <= 10) {
                boolean isPrime = sort % 2 == 0;
                sortDirectionIndex = isPrime ? 1 : 0;
                //perhitungan untuk menentukan sortPropertiesIndex
                sortPropertiesIndex = (int) (Math.ceil((double) sort / 2) - 1);
            }

            ArrayList<String> sortProperties = new ArrayList<>();
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);
            //sorting
            Sort sortObj = new Sort(sortDirection, sortProperties);
            //data per page
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);

            //jika dilakukan search dan filter
            if (searchText != null && filterBy != 0) {
                if (filterBy > 0){
                    //jika dilakukan search text dan filter berdasarkan kategori menu tertentu(menuCategoryId makannya bertipe integer)
                    menus = new ArrayList<>(menuService.searchMenus(searchText, filterBy, pageRequest).getContent());}
                else {
                    filterBy = Math.abs(filterBy) - 1;
                    //jika dilakukan search text dan filter berdasarkan status menu
                    menus = new ArrayList<>(menuService.searchMenus(searchText, MENU_STATUSES[filterBy], pageRequest).getContent());
                }
            } else //jika tidak dilakukan search dan filter secara bersamaan (hanya salah satu saja)
                {
                if (searchText != null) {
                    //jika dilakukan search pada menu
                    menus = new ArrayList<>(menuService.searchMenus(searchText, pageRequest).getContent());
                } else {
                    //jika dilakukan filter pada menu
                    if (filterBy != 0) {
                        if (filterBy > 0) {
                            //jika dilakukan filter berdasarkan kategori menu
                            menus = new ArrayList<>(menuService.findAllMenuByMenuCategoryId(filterBy, pageRequest).getContent());
                        } else {
                            //jika dilakukan filter berdasarkan status menu
                            filterBy = Math.abs(filterBy) - 1;

                            menus = new ArrayList<>(menuService.findAllMenuByMenuStatus(MENU_STATUSES[filterBy], pageRequest).getContent());
                        }
                    } else
                        menus = new ArrayList<>(menuService.findAllPageable(pageRequest).getContent());
                }
            }

            message = "Find menus success";
        }

        Response<ArrayList<Menu>> response = new Response<>(message, menus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/menu/{menuId}")
    public ResponseEntity<Response<Menu>> menu(@PathVariable Integer menuId, @RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        Menu menu;
        String message;

        if (user == null) {
            message = "Access denied";
            menu = null;
        } else {
            menu = menuService.findMenuById(menuId);
            message = menu == null ? "Menu not found" : "Menu found";
        }

        Response<Menu> response = new Response<>(message, menu);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakan untuk edit dan add menu
    @PostMapping("/api/menu/{userId}")
    public ResponseEntity<Response<Integer>> addMenu(@PathVariable Integer userId, @RequestBody Menu menu) {
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                //mendapatkan menuId, kalau id ada maka edit kalo gaada maka add
                boolean isEdit = menu.getMenuId() != 0;
                message = isEdit ? "Menu edit " : "Menu add ";

                menu = menuService.addMenu(menu);

                boolean isSuccess = menu != null;
                message += isSuccess ? "success" : "failed";
                data = isSuccess ? 1 : 0;
            } catch (DataIntegrityViolationException e) {
                message = "Menu add failed - Menu already exists";
                data = 0;
            } catch (Exception e) {
                message = "Menu add failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/menu/{menuId}")
    public ResponseEntity<Response<Integer>> deleteMenu(@PathVariable Integer menuId,
                                                        @RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                menuService.deleteMenu(menuId);

                message = "Delete menu success";
                data = 1;
            } catch (EmptyResultDataAccessException e) {
                message = "Delete menu failed - Menu not found";
                data = 0;
            } catch (Exception e) {
                message = "Delete menu failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
