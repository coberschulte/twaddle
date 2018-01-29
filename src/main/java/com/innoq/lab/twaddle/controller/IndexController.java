package com.innoq.lab.twaddle.controller;

import com.innoq.lab.twaddle.model.User;
import com.innoq.lab.twaddle.repository.UserRepository;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMappingName;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(name = "index", value = "/twaddle")
    public String index(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @GetMapping(name = "user-form", value = "/twaddle/user/add")
    public String addUser() {
        return "user";
    }

    @GetMapping(name = "user-detail", value = "/twaddle/user/{id}")
    public String showUser(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findOne(id);

        if (user != null) {
            model.addAttribute("name", user.getUserName());
        }
        return "userDetails";
    }

    @GetMapping(name = "user-list", value = "/twaddle/user")
    public String listUsers(Model model) {
        Iterable<User> users = userRepository.findAll();

        if (users.iterator().hasNext()) {
            List<User> userList = new ArrayList<>();
            users.iterator().forEachRemaining(userList::add);
            model.addAttribute("users", userList);
        }
        return "userList";
    }

    @PostMapping("/twaddle/user/create")
    public ModelAndView createUser(@ModelAttribute UserForm userForm, RedirectAttributes redirectAttrs) {
        System.out.println("Username: " + userForm.getUserName());
        System.out.println("Passwd:   " + userForm.getPasswd());

        if ((userForm.getUserName() != null && !userForm.getUserName().isEmpty()) &&
                (userForm.getPasswd() != null && userForm.getPasswd().isEmpty() && userForm.getPasswd().equals(userForm.getPasswd2()))) {

            User byUserName = userRepository.findByUserName(userForm.getUserName());
            if (byUserName != null) {
                return new ModelAndView(new RedirectView(fromMappingName("user-form").build()));
            } else {
                User user = userRepository.save(new User(userForm.getUserName(), userForm.getPasswd()));
                return new ModelAndView(new RedirectView(fromMappingName("user-list").build()));
            }
        } else {
            redirectAttrs.addFlashAttribute("message", "Something went wrong!");
            return new ModelAndView(new RedirectView(fromMappingName("user-form").build()));
        }
    }

    public static class UserForm {
        @NotBlank
        private String userName;
        @NotBlank
        private String passwd;
        @NotBlank
        private String passwd2;

        public String getPasswd2() {
            return passwd2;
        }

        public void setPasswd2(String passwd2) {
            this.passwd2 = passwd2;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}