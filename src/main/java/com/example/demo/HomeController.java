package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

//    @PostMapping("/register")
//    public String processRegistrationPage(@Valid
//                                          @ModelAttribute("user") User user, BindingResult result,
//                                          Model model) {
//        model.addAttribute("user", user);
//        if (result.hasErrors()) {
//            return "registration";
//        } else {
//            userService.saveUser(user);
//            model.addAttribute("message", "User Account Created");
//        }
//        return "index";
//    }

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "list";
    }

    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search")String search){
        model.addAttribute("departments", departmentRepository.findByNameContainingAndLocationContainingAndAndIndustryContaining(search, search, search));
        return "searchlist";
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
    public String processForm(@ModelAttribute User user, @RequestParam("userId") long id, @RequestParam("file")MultipartFile file){

        Department department1 = departmentRepository.findById(id).get();
        user.setDepartments(department1);
        if(file.isEmpty() && user.getImage() == null){
            return "redirect:/add";
        }
        if(!file.isEmpty()){
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                user.setImage(uploadResult.get("url").toString());
                userRepository.save(user);

            } catch (IOException e){
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else {
            userRepository.save(user);
//            return "redirect:/";
        }

        Set<User> userList;

        if (department1.users != null){
            userList = new HashSet<>(department1.users);
        }
        else {
            userList = new HashSet<>();
        }
        userList.add(user);
        department1.setUsers(userList);
        departmentRepository.save(department1);
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

    @RequestMapping("/delete/department/{id}") // must delete connections from the other places where connections
    //
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

    @PostMapping("/processDepartment")
    public String processForm(@Valid Department department,
                              BindingResult result){
        if (result.hasErrors()){
            return "departmentform";
        }
        departmentRepository.save(department);
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
