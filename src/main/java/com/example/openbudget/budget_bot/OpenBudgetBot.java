package com.example.openbudget.budget_bot;

import com.example.openbudget.entity.BotUser;
import com.example.openbudget.entity.enums.BotState;
import com.example.openbudget.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component("botcha")
@RequiredArgsConstructor
public class OpenBudgetBot extends TelegramLongPollingBot {

    // cridentials
    @Value("${telegram_bot_username}")
    String username;
    @Value("${telegram_bot_botToken}")
    String botToken;


    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    private final BotUserRepository botUserRepository;
    private final BotService service;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            // bot action
            SendChatAction action = new SendChatAction();
            action.setAction(ActionType.TYPING);
            action.setChatId(message.getChatId().toString());
            execute(action);


            if (message.hasText()) {
                String text = message.getText();
                String chatId = message.getChatId().toString();
                if (text.equals("/start")) {
                    execute(service.start(update));
                } else {
                    BotUser currentUser = service.findCurrentUser(update);


                    String status = currentUser.getStatus();

                    switch (status) {
                        case BotState.SELECT_PROJECT -> {

                        }

                        case BotState.ASK_QUESTION -> {

                        }

                        case BotState.SHARE_PHONE -> {

                        }
                        case BotState.RESULT -> {

                        }
                        default -> {
                        }


                    }

                    if (currentUser.getStatus().equals(BotState.SELECT_PROJECT)) {
                        execute(service.chooseProject(currentUser, update));
                    }
                }
            }
        }
    }
}
