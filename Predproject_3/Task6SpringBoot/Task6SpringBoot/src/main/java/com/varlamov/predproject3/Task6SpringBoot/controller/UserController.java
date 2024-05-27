package com.varlamov.predproject3.Task6SpringBoot.controller;

import com.varlamov.predproject3.Task6SpringBoot.model.User;
import com.varlamov.predproject3.Task6SpringBoot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // При переходе на главную страницу "/" загружается список всех пользователей user-info.html.
    // Кнопка "Add New User" на главной странице перенаправляет на страницу добавления нового пользователя /addNewUser. После добавления перенаправляет на главную, подтягивая обновленный список из кеша.
    // Кнопка "Edit" на главной странице перенаправляет на страницу редактирования пользователя /editUser/{id}.
    @GetMapping(path = "/", produces = "text/plain;charset=UTF-8")
    public String getAllUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        return "user-info";
    }


    // Форма на странице addNewUser.html отправляет POST запрос на /saveUser, чтобы сохранить нового пользователя и перенаправить на главную страницу.
    @GetMapping(path = "/addNewUser", produces = "text/plain;charset=UTF-8")
    public String addNewUser(Model model) {
        // Проверяем, содержит ли модель атрибут с именем "user"
        if (!model.containsAttribute("user")) {
            // Если атрибут не содержится в модели, добавляем новый объект User
            model.addAttribute("user", new User());
        }

        return "addNewUser";
    }


    // Транзитное представление для сохранения измененного или нового пользователя.
    @PostMapping(path = "/saveUser", produces = "text/plain;charset=UTF-8")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Invalid user data");

            return "addNewUser";
        }
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "(*>) User added successfully!");
        redirectAttributes.addFlashAttribute("addedUserId", user.getId());

        return "redirect:/";
    }


    // Переход на страницу редактирования пользователя. Кнопка SAVE направляет на страницу /updateUser
    @GetMapping(path = "/editUser/{id}", produces = "text/plain;charset=UTF-8")
    public String showUpdate(@PathVariable long id, Model model) {
        User user = userService.findOne(id);
        model.addAttribute("user", user);

        return "updateUser";
    }


    // Изменяет пользователя и перенаправляет на страницу "/" с уже обновленным списком пользователей
    @PostMapping(path = "/updateUser", produces = "text/plain;charset=UTF-8")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Invalid user data");
            return "updateUser"; // Возвращаем имя шаблона, чтобы отобразить ошибки
        }
        try {
            userService.update(user.getId(), user);
            redirectAttributes.addFlashAttribute("message", "(*>) User updated successfully!");
            redirectAttributes.addFlashAttribute("updatedUserId", user.getId());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "Invalid user data");
        }

        return "redirect:/";
    }


    // Удаление пользователя по ID
    @DeleteMapping(path = "/deleteUser", produces = "text/plain;charset=UTF-8")
    public String deleteUser(@RequestParam long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");

        return "redirect:/";
    }
}


