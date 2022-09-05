package com.example.openbudget.repository;

import com.example.openbudget.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    void deleteByBotUser_ChatId(String chatId);

    Optional<User> findByBotUser_ChatIdAndCodeNull(String chatId);

    /**
     * all new registered users
     */
    List<User> findAllByCodeSentFalseAndPhoneNumberNotNull();

    /**
     * all identified users
     */
    List<User> findAllByVerifiedTrueAndPaidFalse();

    /**
     * all paid users
     */
    List<User> findAllByPaidTrue();

    /**
     * search by phone_number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * search by phone and bot user chatId
     */
    Optional<User> findByPhoneNumberAndBotUser_ChatId(String phone,String chatId);

    /**
     * find new user
     */
    Optional<User> findByBotUser_ChatIdAndPhoneNumberNull(String chatId);
}
