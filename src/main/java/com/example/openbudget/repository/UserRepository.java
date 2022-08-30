package com.example.openbudget.repository;

import com.example.openbudget.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    void deleteByBotUser_ChatId(String chatId);

    Optional<User> findByBotUser_ChatId(String chatId);
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * all new registered users
     */
    List<User> findAllByCodeSentFalse();

    /**
     * all identified users
     */
    List<User> findAllByCodeSentTrueAndPaidFalse();

    /**
     * all paid users
     */
    List<User> findAllByPaidTrue();
}
