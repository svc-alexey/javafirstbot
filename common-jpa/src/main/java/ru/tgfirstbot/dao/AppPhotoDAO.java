package ru.tgfirstbot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tgfirstbot.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
