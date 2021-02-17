package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

@Service
@AllArgsConstructor
public class AuthService {
    /**
     * коллекция для хранения сессий с авторизовавшимися пользователями
     */
    private final Map<String, Integer> authorizedUsers;

    /**
     * сохранение сессии пользователя при логине
     */
    public void saveSession(Integer userId){
        String sessionId1 = RequestContextHolder.currentRequestAttributes().getSessionId();
        authorizedUsers.put(sessionId1, userId);
    }

    /**
     * Получение айди юзера по айди сессии
     */
    public Integer getUserIdOnSessionId(){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (authorizedUsers.containsKey(sessionId)){
            return authorizedUsers.get(sessionId);
        }
        return null;
    }

    /**
     * удаление сессии из коллекции авторизованных пользователей, используется при логауте
     */
    public void deleteSession(){
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        authorizedUsers.remove(sessionId);
    }

    /**
     * метод проверяет айди юзера, если он не существует - выбрасывает 401 ошибку. Используется в методах контроллеров,
     * в которых необходима авторизация
     */
    public void checkAuth(Integer userId){
        if (userId == null){
            throw new UnauthorizedException();
        }
    }
}
