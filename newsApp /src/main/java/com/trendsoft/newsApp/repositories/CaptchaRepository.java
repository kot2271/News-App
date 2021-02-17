package com.trendsoft.newsApp.repositories;

import com.trendsoft.newsApp.models.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {
    CaptchaCode findBySecretCode(String secretCode);

    /**
     * удаление всех сохраненных каптч, которые были созданы более часа назад от текущего момента
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE from captcha_codes " +
            "WHERE time_to_sec(timediff(now(), time)) > 3600", nativeQuery = true)
    void deleteOld();
}
