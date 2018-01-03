package com.blibli.future.project1.controller;

import com.blibli.future.project1.model.MenuStatus;
import com.blibli.future.project1.model.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManagerController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);

    @GetMapping("/manager")
    public String managerDashboard(HttpSession httpSession, ModelMap modelMap) {
        //mengambil objek user dari session, disimpan dalam object user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //menampilkan userId pada halaman html tapi modenya dihidden di htmlnya (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            return "manager/dashboard";
        }
    }

    @GetMapping("/manager/menu-category")
    public String managerMenuCategory(HttpSession httpSession, ModelMap modelMap) {
        //mengambil objek user dari session, disimpan dalam objek user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //menampilkan userId pada halaman html tapi modenya dihidden di htmlnya (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            return "manager/menu-category";
        }
    }

    @GetMapping("/manager/menu")
    public String managerMenu(HttpSession httpSession, ModelMap modelMap) {
        //mengambil objek user dari sesion lalu disimpan dalam objek user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //menampilkan userId pada halaman html yang diambil dari objek user tapi dihidden di htmlnya (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            //menampilkan isi dari menuStatus pada halaman html (yang $(menuStatus))
            modelMap.addAttribute("menuStatus", MenuStatus.values());
            return "manager/menu";
        }
    }

    @GetMapping("/manager/purchase-paid")
    public String managerPurchasePaid(HttpSession httpSession, ModelMap modelMap){
        //mengambil objek user dari session dan disimpan pada objek user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //menampilkan userId pada halaman html tapi modenya dihidden pada halaman html (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            return "manager/purchase-paid";
        }
    }

    @GetMapping("/manager/product-sold")
    public String managerProductSold(HttpSession httpSession, ModelMap modelMap){
        //mengambil objek user dari session lalu disimpan pada objek user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //manampilkan userId pada halaman html tapi modenya dihidden pada halaman htmlnya (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            //menampilkan menuStatus pada halaman html (yang $(menuStatus))
            modelMap.addAttribute("menuStatus", MenuStatus.values());
            return "manager/product-sold";
        }
    }

    @GetMapping("/manager/profile")
    public String managerProfile(HttpSession httpSession, ModelMap modelMap) {
        //mengambil objek user dari session lalu disimpan pada objek user
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Manager"))
            return "redirect:/";
        else {
            //menampilkan userId pada html tapi modenya dihidden pada htmlnya (yang $(userId))
            modelMap.addAttribute("userId", user.getUserId());
            return "manager/profile";
        }
    }

}
