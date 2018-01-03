package com.blibli.future.project1.controller;

import com.blibli.future.project1.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CashierController {

    @GetMapping("/cashier")
    public String cashierDashboard(HttpSession httpSession, ModelMap modelMap){
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Cashier"))
            return "redirect:/";
        else {
            modelMap.addAttribute("userId", user.getUserId());
            return "cashier/dashboard";
        }
    }

    @GetMapping("/cashier/purchase-progress") // routing saat memencet menu pemesanan pada account role cashier
    public String cashierPurchaseProgress(HttpSession httpSession, ModelMap modelMap){
        User user = (User) httpSession.getAttribute("user");

        if (user == null) //kalau belum login
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Cashier")) //kalau rolenya bukan cashier
            return "redirect:/";
        else {
            modelMap.addAttribute("userId", user.getUserId()); //kalau rolenya cashier
            return "cashier/purchase-progress"; //diarahkan ke halaman cashier/purchase-progress
        }
    }

    @GetMapping("/cashier/purchase-paid")
    public String cashierPurchasePaid(HttpSession httpSession, ModelMap modelMap){
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Cashier"))
            return "redirect:/";
        else {
            modelMap.addAttribute("userId", user.getUserId());
            return "cashier/purchase-paid";
        }
    }

    @GetMapping("/cashier/profile")
    public String cashierProfile(HttpSession httpSession, ModelMap modelMap){
        User user = (User) httpSession.getAttribute("user");

        if (user == null)
            return "redirect:/login";
        else if (!user.getRole().getRoleName().equals("Cashier"))
            return "redirect:/";
        else {
            modelMap.addAttribute("userId", user.getUserId());
            return "cashier/profile";
        }
    }
}
