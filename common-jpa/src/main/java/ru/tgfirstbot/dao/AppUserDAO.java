package ru.tgfirstbot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tgfirstbot.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(Long id);
}
