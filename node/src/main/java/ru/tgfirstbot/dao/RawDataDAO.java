package ru.tgfirstbot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tgfirstbot.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
