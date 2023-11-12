package ru.tgfirstbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tgfirstbot.CryptoTool;
import ru.tgfirstbot.dao.AppUserDAO;
import ru.tgfirstbot.service.UserActivationService;

@RequiredArgsConstructor
@Service
public class UserActivationSeviceImpl implements UserActivationService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
