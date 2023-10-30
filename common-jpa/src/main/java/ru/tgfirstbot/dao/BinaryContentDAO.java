package ru.tgfirstbot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tgfirstbot.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
