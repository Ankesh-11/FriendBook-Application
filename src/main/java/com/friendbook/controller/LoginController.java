package com.friendbook.controller;

import com.friendbook.entities.UserModel;
import com.friendbook.repository.UserRepository;
import com.friendbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/signin")
public class LoginController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RecaptchaService recaptchaService;

    public LoginController(UserService userService, UserRepository userRepository, RecaptchaService recaptchaService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.recaptchaService = recaptchaService;
    }

    @ModelAttribute("user")
    public UserModel user() {
        return new UserModel();
    }

    @GetMapping()
    public String loginPage() {
        return "login";
    }

    @PostMapping
    public String userLogin(@ModelAttribute("user") UserModel userDto,
                            @RequestParam(name = "g-recaptcha-response") String recaptchaResponse,
                            HttpSession session,
                            Model model) {

        // Verify reCAPTCHA
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(recaptchaResponse);
        if (!isRecaptchaValid) {
            model.addAttribute("error", "Captcha verification failed. Please try again.");
            return "login";
        }

        Optional<UserModel> users = userRepository.findByEmail(userDto.getEmail());
        if (users.isEmpty()) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
            return "login";
        } else {
            UserModel user = users.get();
            if (!user.getPassword().equals(userDto.getPassword())) {
                model.addAttribute("error", "Invalid username or password. Please try again.");
                return "login";
            }
            session.setAttribute("loggedInUser", user);
            model.addAttribute("user", user);
            return "redirect:/home";
        }
    }
}
