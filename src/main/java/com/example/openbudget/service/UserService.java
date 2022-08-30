package com.example.openbudget.service;

import com.example.openbudget.budget_bot.OpenBudgetBot;
import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.entity.User;
import com.example.openbudget.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final OpenBudgetBot openBudgetBot;

    public ApiResponse findAll() {
        List<User> all = userRepository.findAll();
        return new ApiResponse(true, "All users", all);
    }

    public ApiResponse findNewUsers() {
        List<User> allByDoneFalse = userRepository.findAllByCodeSentFalse();
        return new ApiResponse(true, "All new registered Users", allByDoneFalse);
    }

    @SneakyThrows
    public ApiResponse askingCode(String phone) {

        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());
        sendMessage.setText("Send code please!");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent");

    }

    public ApiResponse findAllVerifiedUsers() {

        List<User> allByDoneTrueAndPaidFalse = userRepository.findAllByCodeSentTrueAndPaidFalse();

        return new ApiResponse(true, "all", allByDoneTrueAndPaidFalse);
    }

    public ApiResponse paid_users() {

        List<User> allByPaidTrue = userRepository.findAllByPaidTrue();

        return new ApiResponse(true, "All paid users", allByPaidTrue);
    }

    public ApiResponse findOne(Integer id) {
        return new ApiResponse(true,"user found",userRepository.findById(id).get());
    }
}
