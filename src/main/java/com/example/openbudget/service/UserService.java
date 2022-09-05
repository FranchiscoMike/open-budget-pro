package com.example.openbudget.service;

import com.example.openbudget.budget_bot.OpenBudgetBot;
import com.example.openbudget.dto.ApiResponse;
import com.example.openbudget.entity.User;
import com.example.openbudget.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
        List<User> allByDoneFalse = userRepository.findAllByCodeSentFalseAndPhoneNumberNotNull();
        return new ApiResponse(true, "All new registered Users", allByDoneFalse);
    }

    @SneakyThrows
    public ApiResponse askingCode(String phone) {

        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());
        user.setCodeSent(true);
        userRepository.save(user);
        sendMessage.setText("Qurilmangizga kod yuborildi shuni yuboring va biz kodni tekshirgan holda sizga xabar yuboramiz!");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent");

    }

    public ApiResponse findAllVerifiedUsers() {

        List<User> allByDoneTrueAndPaidFalse = userRepository.findAllByVerifiedTrueAndPaidFalse();

        return new ApiResponse(true, "all", allByDoneTrueAndPaidFalse);
    }

    public ApiResponse paid_users() {

        List<User> allByPaidTrue = userRepository.findAllByPaidTrue();

        return new ApiResponse(true, "All paid users", allByPaidTrue);
    }

    public ApiResponse findOne(Integer id) {
        return new ApiResponse(true, "user found", userRepository.findById(id).get());
    }

    @SneakyThrows
    public ApiResponse verify_user(String phone) {
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());
        user.setVerified(true);
        userRepository.save(user);
        sendMessage.setText("Siz yuborgan kod tasidiqlandi! Tez orada hisobingiz to'liriladi!\n" + "Ovozingiz uchun raxmat!!!\uD83D\uDE0A ");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent");
    }

    @SneakyThrows
    public ApiResponse resend_code(String phone) {
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());
        user.setCodeSent(false);
        user.setVerified(false);
        user.setCode("Kod hali kelmagan");
        userRepository.save(user);
        sendMessage.setText("Sizning kodingiz hali tasdiqlanmagan \n\n" + "Iltimos kodni qaytadan jo'nating!");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent to user :) " + user.getPhoneNumber());
    }

    @SneakyThrows
    public ApiResponse code_not_received(String phone) {
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

//        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());

        userRepository.delete(user);  // user is deleted there
        sendMessage.setText("Siz kodni o'z vaqtida jo'nata olmadiz qaytadan  /start tugmasini bosing ");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent to user :) " + user.getPhoneNumber());
    }

    @SneakyThrows
    public ApiResponse user_is_paid(String phone) {
        Optional<User> byPhoneNumber = userRepository.findByPhoneNumber(phone);

        System.out.println("byPhoneNumber = " + byPhoneNumber);

        User user = byPhoneNumber.get();

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(user.getBotUser().getChatId());
        user.setPaid(true);
        userRepository.save(user);
        sendMessage.setText("Hisobingiz to'ldirildi!");

        openBudgetBot.execute(sendMessage);

        return new ApiResponse(true, "message is sent to user :) " + user.getPhoneNumber());

    }
}
