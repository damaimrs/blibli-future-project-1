package com.blibli.future.project1.api;

import com.blibli.future.project1.model.Response;
import com.blibli.future.project1.model.Role;
import com.blibli.future.project1.model.User;
import com.blibli.future.project1.service.RoleService;
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
public class RoleAPI {

    /**
     * Merupakan object untuk melakukan akses data role di database
     */
    private RoleService mRoleService;

    /**
     * Merupakan object untuk melakukan akses data user di database
     */
    private UserService mUserService;

    private final String[] SORT_PROPERTIES = {
            "roleName"
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    /**
     * Merupakan constructor dari class RoleAPI
     *
     * @param roleService merupakan object mRoleService yang secara otomatis di inject oleh spring
     * @param userService merupakan object mUserService yang secara otomatis di inject oleh spring
     */
    @Autowired
    public RoleAPI(RoleService roleService, UserService userService) {
        mRoleService = roleService;
        mUserService = userService;
    }

    /**
     * Method yang dijalankan saat user melakukan request ke route "/api/role/count"
     *
     * @param userId merupakan query string yang dikirim (userId)
     * @return adalah json dari response dengan data jumlah role
     */
    //fungsi yang digunakan untuk mendapatkan jumlah role terdaftar
    @GetMapping("/api/role/count")
    public ResponseEntity<Response<Long>> roleCount(@RequestParam Integer userId) {
        String message;
        Long count;
        User user = mUserService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            message = "Access success";
            count = mRoleService.getRoleCount();
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/role/all")
    public ResponseEntity<Response<ArrayList<Role>>> roleListAll(@RequestParam Integer userId) {
        User user = mUserService.findUserById(userId);
        ArrayList<Role> roles;
        String message;

        if (user == null) {
            message = "Access denied";
            roles = null;
        } else {
            message = "Find roles success";
            roles = new ArrayList<>(mRoleService.findAll());
        }

        Response<ArrayList<Role>> response = new Response<>(message, roles);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/role")
    public ResponseEntity<Response<ArrayList<Role>>> roleList(@RequestParam Integer userId,
                                                              @RequestParam(required = false) String searchText,
                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false, defaultValue = "1") Integer sort) {
        //default page = 1, kalau pagenya cuma ada 1 -> halaman yang dituju saat pagination
        //default pagesize = 10, jumlah halaman yang tersedia untuk menampilkan role
        //default sorting = 1
        User user = mUserService.findUserById(userId); //ngecek user udah login apa belum
        ArrayList<Role> roles;
        String message;

        if (user == null) {
            message = "Access denied";
            roles = null;
        } else {
            //sort properties disetting jadi 0, data role akan ditampilkan urut sesuai dengan sortPropertiesIndex -> ada diatas
            int sortPropertiesIndex = 0;
            //sort direction disetting jadi 0, data role akan disorting secara ASC
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;
            //untuk membatasi jumlah data per halaman
            if (pageSize <= 0)
                pageSize = 10;
            //kondisi jika ada sorting secara ASC atau DSC
            if (sort >= 1 && sort <= 2)
                sortDirectionIndex = sort - 1;

            ArrayList<String> sortProperties = new ArrayList<>();
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);
            //request data role berdasarkan sorting dan ukuran data yang akan ditampilkan pada halaman
            Sort sortObj = new Sort(sortDirection, sortProperties);
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);

            if (searchText != null){
                //jika ada search
                roles = new ArrayList<>(mRoleService.searchRoles(searchText, pageRequest).getContent());}
            else{
                //jika tidak ada search
                roles = new ArrayList<>(mRoleService.findAll(pageRequest).getContent());}

            message = "Find roles success";
        }

        Response<ArrayList<Role>> response = new Response<>(message, roles);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/role/{roleId}")
    public ResponseEntity<Response<Role>> role(@PathVariable Integer roleId, @RequestParam Integer userId) {
        User user = mUserService.findUserById(userId);
        Role role;
        String message;

        if (user == null) {
            message = "Access denied";
            role = null;
        } else {
            role = mRoleService.findRoleById(roleId);
            message = role == null ? "Role not found" : "Role found";
        }

        Response<Role> response = new Response<>(message, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API yang digunakan untuk menambah atau mengedit role
    @PostMapping("/api/role/{userId}")
    public ResponseEntity<Response<Integer>> addRole(@PathVariable Integer userId, @RequestBody Role role) {
        User user = mUserService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            //kondisi jika getRoleId didapatkan / roleId sudah terdaftar
            if (role.getRoleId() != 0) {
                //menghitung jumlah user berdasarkan id role user
                long usersCount = mUserService.getUserCountByRoleId(role.getRoleId());
                //jika tidak ada user yang menggunakan role tersebut maka role bisa diedit
                if (usersCount == 0) {
                    role = mRoleService.addRole(role);

                    boolean isSuccess = role != null;
                    message = isSuccess ? "Role edit success" : "Role edit failed";
                    data = isSuccess ? 1 : 0;
                } else {
                    message = "Role edit failed - Role in use";
                    data = 0;
                }
            } else {
                //kondisi jika role belum terdaftar sebelumnya
                try {
                    role = mRoleService.addRole(role);

                    boolean isSuccess = role != null;
                    message = isSuccess ? "Role add success" : "Role add failed";
                    data = isSuccess ? 1 : 0;
                } catch (DataIntegrityViolationException e) {
                    message = "Role add failed - Role already exists";
                    data = 0;
                } catch (Exception e) {
                    message = "Role add failed - Internal Server Error";
                    data = 0;
                }
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/role/{roleId}")
    public ResponseEntity<Response<Integer>> deleteRole(@PathVariable Integer roleId, @RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        User user = mUserService.findUserById(userId);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                mRoleService.deleteRole(roleId);

                message = "Delete role success";
                data = 1;
            } catch (EmptyResultDataAccessException e) {
                message = "Delete role failed - Role not found";
                data = 0;
            } catch (DataIntegrityViolationException e) {
                message = "Delete role failed - Role in use";
                data = 0;
            } catch (Exception e) {
                message = "Delete role failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
