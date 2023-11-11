package ru.tgfirstbot.service;

import ru.tgfirstbot.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser, String email);
}
