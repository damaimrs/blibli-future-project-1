package com.blibli.future.project1.controller;

import com.blibli.future.project1.model.Response;
import com.blibli.future.project1.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class IndexController {

    private final static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    private final static String BASE_URL = "http://localhost:9090/api/";

    private ObjectMapper mObjectMapper;
    private RestTemplate mRestTemplate;

    public IndexController() {
        mObjectMapper = new ObjectMapper();
        mRestTemplate = new RestTemplate();
    }

    /**
     * Method yang akan dijalankan saat user request ke route "/"
     * Method ini akan mengecek role user, dan menggenerate halaman sesuai role usernya
     *
     * @param httpSession adalah object yang mengatur session
     * @return adalah halaman yang akan ditampilkan
     */
    @GetMapping("/")
    public String index(HttpSession httpSession) {
        /**
         * mendapatkan object user dari session lalu disimpan pada object user
         */
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else {
            switch (user.getRole().getRoleName()) {
                case "Admin":
                    return "redirect:/admin";
                case "Cashier":
                    return "redirect:/cashier";
                case "Manager":
                    return "redirect:/manager";
                default:
                    return "redirect:/error404";
            }
        }
    }

    /**
     * Method ini akan dijalankan saat user request ke route "/login"
     * Method ini akan mengembalikan halaman login apabila user belum login
     *
     * @param httpSession adalah object yang mengatur session
     * @return adalah halaman yang akan ditampilkan
     */
    @GetMapping("/login")
    public String login(HttpSession httpSession) {
        /**
         * mendapatkan object user dari session lalu disimpan pada object user
         */
        User user = (User) httpSession.getAttribute("user");
        return user != null ? "redirect:/" : "index/login";
    }

    /**
     * Method saat user login (masukin email dan password lalu klik login)
     * Method ini akan dijalankan saat user request post ke route "/login"
     * Method ini akan meminta data user berdasarkan email ke route "/api/user/email"
     * Lalu mengecek password dari data user yang telah didapatkan
     *
     * @param params      adalah json yang dikirim (email, password)
     * @param httpSession adalah object dari session
     * @return merupakan json hasil dari validasi email dan password
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Response<User>> login(@RequestBody Map<String, String> params, HttpSession httpSession) {
        String url = BASE_URL + "user/email";

        //mendapatkan userEmail dari parameter lalu disimpan pada variabel userEmail
        String userEmail = params.get("userEmail");
        //mendapatkan userPassword dari parameter lalu disimpan pada variabel userPassword
        String userPassword = params.get("userPassword");
        String messsage = null;
        //untuk ngasih userEmail ke variabel url
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParam("userEmail", userEmail);
        //untuk parse data
        ResponseEntity<String> response = mRestTemplate.exchange(uriComponentsBuilder.toUriString(),
                HttpMethod.GET, null, String.class);

        Response<User> myResponse;
        User user = null;

        try {
            myResponse = mObjectMapper.readValue(response.getBody(), new TypeReference<Response<User>>() {
            });
            //get user
            user = myResponse.getData();

            //kondisi jika user null
            if (user == null)
                messsage = "Invalid user email";
            else {
                //ngeencode password dari data yg didapat
                userPassword = User.passwordEncoder(userPassword);
                //kalo encodernya sama dengan encoder yg di password database
                if (userPassword.equals(user.getUserPassword())) {
                    messsage = "Login success";
                    //kalo berhasil login, httpSessionnya disetting usernya jadi user
                    httpSession.setAttribute("user", user);

                    LOGGER.info("User " + user.getUserEmail() + " logged in");
                } else {
                    messsage = "Invalid user password";
                    user = null;
                }
            }
        } catch (Exception e) {
            messsage = "Internal server Error";
            LOGGER.error(e.getMessage());
        } finally {
            myResponse = new Response<>(messsage, user);
        }

        return new ResponseEntity<>(myResponse, HttpStatus.OK);
    }


    /**
     * Method ini akan dijalankan saat user melakukan request post ke route "/logout"
     * Method ini akan mengecek apabila session terdapat attribute user yang menandakan ada user yang sedang login
     * Maka session tersebut akan dihapus
     *
     * @param httpSession adalah object yang mengatur session
     * @return adalah json dari response dengan tanpa data
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<Response<Integer>> logout(HttpSession httpSession) {
        String message;
        Integer data;
        User user = (User) httpSession.getAttribute("user");

        if (user != null) {
            //kalau logout user dari sessionnya dihapus
            httpSession.removeAttribute("user");
            message = "Logout success";
            data = 1;

            LOGGER.info("User " + user.getUserName() + " logged out");
        } else {
            message = "Logout failed";
            data = 0;
        }

        Response<Integer> response = new Response<>(message, data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/error404")
    public String error404() {
        return "index/404";
    }
}
