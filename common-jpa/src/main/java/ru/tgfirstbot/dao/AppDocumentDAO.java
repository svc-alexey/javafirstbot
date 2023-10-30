package ru.tgfirstbot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tgfirstbot.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
