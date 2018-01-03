package com.blibli.future.project1.api;

import com.blibli.future.project1.model.Response;
import com.blibli.future.project1.model.User;
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
public class UserAPI {

    /**
     * Object untuk mengakses database
     */
    private UserService userService;

    private final String[] SORT_PROPERTIES = {
            "userName",
            "userEmail",
            "userAddress",
            "role.roleName"
    };

    private final String[] SORT_DIRECTION = {
            "asc",
            "desc"
    };

    @Autowired
    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method ini akan dijalankan saat user melakukan request ke route "/api/user/email"
     * Method ini akan mengambil user dari database sesuai email yang dikirim
     *
     * @param userEmail adalah query string yang dikirim (email)
     * @return adalah json dari response dengan data user tersebut
     */
    @GetMapping("/api/user/email")
    public ResponseEntity<Response<User>> findUserByEmail(@RequestParam String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        String message = user != null ? "Find user success" : "Find user failed";
        Response<User> response = new Response<>(message, user);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method untuk mendapatan semua user tanpa pagination
     **/
    @GetMapping("/api/user/all")
    public ResponseEntity<Response<ArrayList<User>>> userListAll(@RequestParam Integer userId) {
        User user = userService.findUserById(userId);
        ArrayList<User> users;
        String message;

        if (user == null) {
            message = "Access denied";
            users = null;
        } else {
            message = "Find users success";
            users = new ArrayList<>(userService.findAll());
        }

        Response<ArrayList<User>> response = new Response<>(message, users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Method ini akan dijalankan saat user melakukan request ke route "/api/user/count"
     *
     * @param userId merupakan query string yang dikirim (userId)
     * @return adalah json dari response dengan data jumlah user
     */
    //fungsi yang digunakan untuk mendapatkan jumlah user
    @GetMapping("/api/user/count")
    public ResponseEntity<Response<Long>> userCount(@RequestParam Integer userId) {
        String message;
        Long count;
        User user = userService.findUserById(userId);

        if (user == null) {
            message = "Access denied";
            count = null;
        } else {
            message = "Access success";
            count = userService.getUserCount();
        }

        Response<Long> response = new Response<>(message, count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/user")
    public ResponseEntity<Response<ArrayList<User>>> userList(@RequestParam Integer userId,
                                                              @RequestParam(required = false) Integer roleId,
                                                              @RequestParam(required = false) String searchText,
                                                              @RequestParam(required = false, defaultValue = "1") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                              @RequestParam(required = false, defaultValue = "1") Integer sort) {
        User user = userService.findUserById(userId);

        ArrayList<User> users;
        String message;

        if (user == null) {
            message = "Access denied";
            users = null;
        } else {
            //sortPropertiesIndex = 0 --> sorting berdasarkan username
            int sortPropertiesIndex = 0;
            // sortDirectionIndex = 0 --> asc
            int sortDirectionIndex = 0;

            if (page <= 0)
                page = 0;

            if (page > 0)
                page -= 1;

            if (pageSize <= 0)
                pageSize = 10;
            //kalau ada sorting
            if (sort >= 1 && sort <= 8) {
                //untuk mencari direction sorting dimod 2, ganjil asc genap desc
                boolean isPrime = sort % 2 == 0;
                sortDirectionIndex = isPrime ? 1 : 0;
                //untuk mencari sortPropertiesIndex diagi 2 di kurangi 1
                sortPropertiesIndex = (int) (Math.ceil((double) sort / 2) - 1);
            }

            ArrayList<String> sortProperties = new ArrayList<>();
            sortProperties.add(SORT_PROPERTIES[sortPropertiesIndex]);

            Sort.Direction sortDirection = Sort.Direction.fromString(SORT_DIRECTION[sortDirectionIndex]);

            Sort sortObj = new Sort(sortDirection, sortProperties);
            PageRequest pageRequest = new PageRequest(page, pageSize, sortObj);

            //kalau ada search dan filter berdasarkan roleId
            if (searchText != null && roleId != 0){
            users = new ArrayList<>(userService.searchUsers(searchText, roleId, pageRequest).getContent());}
            else {
                //kalau disearch
                if (searchText != null){
                    users = new ArrayList<>(userService.searchUsers(searchText, pageRequest).getContent());}
                else {
                    //kalau difilter berdasarkan roleId
                    if (roleId != 0){
                        users = new ArrayList<>(userService.findAllByRoleId(roleId, pageRequest).getContent());}
                    //kalau get all (tidak ada filter berdasarkan role id ataupun search)
                    else{
                        users = new ArrayList<>(userService.findAllPageable(pageRequest).getContent());}
                }
            }

            message = "Find users success";
        }

        Response<ArrayList<User>> response = new Response<>(message, users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/user/{userId}")
    public ResponseEntity<Response<User>> user(@PathVariable Integer userId, @RequestParam Integer userIdLogin) {
        User user = userService.findUserById(userIdLogin);
        String message;

        if (user == null) {
            message = "Access denied";
        } else {
            user = userService.findUserById(userId);
            message = user == null ? "User not found" : "User found";
        }

        Response<User> response = new Response<>(message, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //fungsi untuk add atau edit user
    @PostMapping("/api/user/{userId}")
    public ResponseEntity<Response<Integer>> addUser(@PathVariable Integer userId, @RequestBody User user) {
        User userLogin = userService.findUserById(userId);
        Integer data;
        String message;

        if (userLogin == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                boolean isEdit = user.getUserId() != 0;
                message = isEdit ? "User edit " : "User add ";
                //untuk mengencode password
                if (isEdit) {
                    User oldData = userService.findUserById(user.getUserId());

                    if (!user.getUserPassword().isEmpty())
                        user.setUserPassword(User.passwordEncoder(user.getUserPassword()));
                    else
                        user.setUserPassword(oldData.getUserPassword());
                } else
                    user.setUserPassword(User.passwordEncoder(user.getUserPassword()));

                user = userService.addUser(user);

                boolean isSuccess = user != null;
                message += isSuccess ? "success" : "failed";
                data = isSuccess ? 1 : 0;
            } catch (DataIntegrityViolationException e) {
                message = "User add failed - User already exists";
                data = 0;
            } catch (Exception e) {
                message = "User add failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/api/user/{userId}")
    public ResponseEntity<Response<Integer>> deleteUser(@PathVariable Integer userId, @RequestBody Map<String, Integer> params) {
        Integer userIdLogin = params.get("userId");
        User user = userService.findUserById(userIdLogin);
        Integer data;
        String message;

        if (user == null) {
            message = "Access denied";
            data = null;
        } else {
            try {
                userService.deleteUser(userId);

                message = "Delete user success";
                data = 1;
            } catch (EmptyResultDataAccessException e) {
                message = "Delete user failed - User not found";
                data = 0;
            } catch (Exception e) {
                message = "Delete user failed - Internal Server Error";
                data = 0;
            }
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
