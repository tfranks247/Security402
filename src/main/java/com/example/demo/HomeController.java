package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid
                                          @ModelAttribute("user") User user, BindingResult result,
                                          Model model) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            return "registration";
        } else {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Created");
        }
        return "index";
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/login")
    public String login(){
        return "login";
    }



    @Autowired
    UserRepository userRepository;

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
    String username = principal.getName();
    model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    @GetMapping("/add")
    public String departmentForm(Model model){
        model.addAttribute("department", new Department());


        model.addAttribute("users", userRepository.findAll());

        return "departmentform";
    }

    @PostMapping("/process")
    public String processForm(@ModelAttribute Department department, @RequestParam("userId") long id){

        User user = userRepository.findById(id).get();

        Set<User> userList;

        if (department.users != null){
            userList = new HashSet<>(department.users);
        }
        else {
            userList = new HashSet<>();
        }
        userList.add(user);
        department.setUsers(userList);
        departmentRepository.save(department);
        return "redirect:/";
    }


    @RequestMapping("/details/department/{id}")
    public String createDepartment(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("department", departmentRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/department/{id}")
    public String updateDepartment(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("department", departmentRepository.findById(id).get());
        return "departmentform";
    }

    @RequestMapping("/delete/department/{id}")
    public String delDepartment(@PathVariable("id") long id){
        departmentRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/addUser")
    public String userForm(Model model){
        model.addAttribute("user", new User());

        model.addAttribute("departments", departmentRepository.findAll());
        return "userform";
    }

    @PostMapping("/processUser")
    public String processForm(@Valid User user,
                              BindingResult result){
        if (result.hasErrors()){
            return "userform";
        }
        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping("/details/{id}")
    public String createUser(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("user", userRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, Model model)
    {
        model.addAttribute("user", userRepository.findById(id).get());
        return "userform";
    }

    @RequestMapping("/delete/{id}")
    public String delUser(@PathVariable("id") long id){
        departmentRepository.deleteById(id);
        return "redirect:/";
    }
}
