package com.blibli.future.project1.api;

import com.blibli.future.project1.model.MenuCategory;
import com.blibli.future.project1.model.Response;
import com.blibli.future.project1.model.User;
import com.blibli.future.project1.service.MenuCategoryService;
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
public class MenuCategoryAPI {

    /**
     * Merupakan object untuk melakukan akses data kategori menu di database
     */
    private MenuCategoryService menuCategoryService;

    /**
     * Merupakan object untuk melakukan akses data user di database
     */
    private UserService userService;

    private MenuService menuService;

    private final String[] SORT_PROPERTIES = {
            "menuCategoryName",
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    /**
     * Merupakan constructor dari class MenuCategory API
     *
     * @param menuCategoryService merupakan object menuCategoryService yang secara otomatis di inject oleh spring
     * @param userService merupakan object userService yang secara otomatis di inject oleh spring
     */
    @Autowired
    public MenuCategoryAPI(MenuCategoryService menuCategoryService, MenuService menuService, UserService userService){
        this.menuCategoryService = menuCategoryService;
        this.menuService = menuService;
        this.userService = userService;
    }

    //fungsi yang digunakan untuk menghitung jumlah menu kategori terdaftar
    @GetMapping("/api/menu-category/count")
    public ResponseEntity<Response<Long>> menuCategoryCount(@RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            message = "Access success";
            //menyimpan hasil perhitungan jumlah kategori menu pada variabel
            count = menuCategoryService.getMenuCategoryCount();
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakan untuk menampilkan menu kategori
    @GetMapping("/api/menu-category")
    public ResponseEntity<Response<ArrayList<MenuCategory>>> menuCategoryList(@RequestParam Integer userId,
                                                                              @RequestParam(required = false) String searchText,
                                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                              @RequestParam(required = false, defaultValue = "1") Integer sort) {
        User user = userService.findUserById(userId); //ngecek user udah login apa belum
        ArrayList<MenuCategory> menuCategories;
        String message;

        if (user == null) {
            message = "Access denied";
            menuCategories = null;
        } else {
            //sortPropertiesIndex = 0 --> sorting berdasarkan nama
            int sortPropertiesIndex = 0;
            //sortDirectionIndex = 0 --> asc
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;
            //ukuran data dalam satu halaman
            if (pageSize <= 0)
                pageSize = 10;
            //jika ada sorting
            if (sort >= 1 && sort <= 2)
                sortDirectionIndex = sort - 1;

            ArrayList<String> sortProperties = new ArrayList<>();
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);
            //sorting berdasarkan sortDirection dan sortProperties
            Sort sortObj = new Sort(sortDirection, sortProperties);
            //mendapatkan data sesuai dengan halaman ke, jumlah data per halaman, dan object yang telah disort
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);
            //kondisi jika sedang dilakukan search pada kategori menu
            if (searchText != null)
                menuCategories = new ArrayList<>(menuCategoryService.searchMenuCategories(searchText, pageRequest).getContent());
            else{
                //kondisi jika tidak ada pencarian pada kategori menu
                menuCategories = new ArrayList<>(menuCategoryService.findAllPageable(pageRequest).getContent());}

            message = "Find menu categories success";
        }

        Response<ArrayList<MenuCategory>> response = new Response<>(message, menuCategories);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakan untuk mendapatkan seluruh kategori menu yang ada di database tanpa paging
    @GetMapping("/api/menu-category/all")
    public ResponseEntity<Response<ArrayList<MenuCategory>>> menuCategoryListAll(@RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<MenuCategory> menuCategories;
        String message;

        if (user == null) {
            message = "Access denied";
            menuCategories = null;
        } else {
            message = "Find menu categories success";
            //menyimpan menu kategori yg diambil dari db ke dalam menuCategories
            menuCategories = new ArrayList<>(menuCategoryService.findAll());
        }

        Response<ArrayList<MenuCategory>> response = new Response<>(message, menuCategories);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/menu-category/{menuCategoryId}")
    public ResponseEntity<Response<MenuCategory>> menuCategory(@PathVariable Integer menuCategoryId, @RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        MenuCategory menuCategory;
        String message;

        if (user == null) {
            message = "Access denied";
            menuCategory = null;
        } else {
            menuCategory = menuCategoryService.findMenuCategoryById(menuCategoryId);
            message = menuCategory == null ? "Menu Category not found" : "Menu Category found";
        }

        Response<MenuCategory> response = new Response<>(message, menuCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakna untuk menambah atau mengedit kategori menu
    @PostMapping("/api/menu-category/{userId}")
    public ResponseEntity<Response<Integer>> addMenuCategory(@PathVariable Integer userId, @RequestBody MenuCategory menuCategory) {
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            //kondisi jika menuCategoryId !=0 atau id kategori menu sudah ada
            if (menuCategory.getMenuCategoryId() != 0) {
                //menghitung jumlah menu yang terdaftar dengan menggunakan id kategori menu tersebut
                long menusCount = menuService.getMenuCountByMenuCategoryId(menuCategory.getMenuCategoryId());
                //kondisi jika tidak ada menu yang terdaftar dengan menggunakan kategori menu tersebut
                if (menusCount == 0) {
                    //melakukan edit terhadap kategori menu
                    menuCategory = menuCategoryService.addMenuCategory(menuCategory);

                    boolean isSuccess = menuCategory != null;
                    message = isSuccess ? "Menu category edit success" : "Menu category edit failed";
                    data = isSuccess ? 1 : 0;
                } else {
                    //kondisi jika ada menu yang terdaftar dengan menggunakan kategori menu tersebut
                    message = "Menu category edit failed - Menu category in use";
                    data = 0;
                }
            } else {
                //kondisi jika id kategori menu tidak ditemukan atau masih belum ada kategori menu tersebut di database
                try {
                    //melakukan add
                    menuCategory = menuCategoryService.addMenuCategory(menuCategory);

                    boolean isSuccess = menuCategory != null;
                    message = isSuccess ? "Menu category add success" : "Menu category add failed";
                    data = isSuccess ? 1 : 0;
                } catch (DataIntegrityViolationException e) {
                    //kondisi jika kategori menu sudah ada
                    message = "Menu category add failed - Menu category already exists";
                    data = 0;
                } catch (Exception e) {
                    message = "Menu category add failed - Internal Server Error";
                    data = 0;
                }
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi yang digunakan untuk menghapus kategori menu
    @DeleteMapping("/api/menu-category/{menuCategoryId}")
    public ResponseEntity<Response<Integer>> deleteMenuCategory(@PathVariable Integer menuCategoryId, @RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = userService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                //menghapus kategori menu dari database
                menuCategoryService.deleteMenuCategory(menuCategoryId);

                message = "Delete menu category success";
                data = 1;
            } catch (EmptyResultDataAccessException e) {
                //kondisi jika menu kategori tidak ditemukan
                message = "Delete menu category failed - Menu category not found";
                data = 0;
            } catch (DataIntegrityViolationException e) {
                //kondisi jika menu kategori sedang digunakan oleh menu
                message = "Delete menu category failed - Menu category in use";
                data = 0;
            } catch (Exception e) {
                message = "Delete menu category failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
