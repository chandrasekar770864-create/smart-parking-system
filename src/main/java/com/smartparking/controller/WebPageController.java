package com.smartparking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPageController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "forward:/register.html";
    }

    @GetMapping("/parking")
    public String parkingPage() {
        return "forward:/parking.html";
    }

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "forward:/checkout.html";
    }

    @GetMapping("/bookings")
    public String bookingsPage() {
        return "forward:/bookings.html";
    }
}
