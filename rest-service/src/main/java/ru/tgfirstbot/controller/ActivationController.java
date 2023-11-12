package ru.tgfirstbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tgfirstbot.service.UserActivationService;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация прошла успешно");
        }
        return ResponseEntity.internalServerError().build();
    }
}
