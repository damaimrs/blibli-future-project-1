package com.blibli.future.project1.controller;

import com.blibli.future.project1.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/admin")
    public String adminDashboard(HttpSession httpSession, ModelMap modelMap) {
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Admin"))
            return "redirect:/";
        else {
            //mengambil atribut userId untuk ditampilkan di halaman yg dituju (di html yang $(userId)
            //di html modenya dibuat hidden
            modelMap.addAttribute("userId", user.getUserId());
            return "admin/dashboard";
        }
    }

    @GetMapping("/admin/role")
    public String adminRole(HttpSession httpSession, ModelMap modelMap) {
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Admin"))
            return "redirect:/";
        else {
            //mengambil atribut userId untuk ditampilkan di halaman yg dituju
            modelMap.addAttribute("userId", user.getUserId());
            return "admin/role";
        }
    }

    @GetMapping("/admin/user")
    public String adminUser(HttpSession httpSession, ModelMap modelMap) {
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Admin"))
            return "redirect:/";
        else {
            modelMap.addAttribute("userId", user.getUserId());
            return "admin/user";
        }
    }
}
