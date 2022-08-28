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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

            BotUser currentUser = service.findCurrentUser(update);

            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    execute(service.start(currentUser));
                } else {


                    String status = currentUser.getStatus();

                    switch (status) {
                        case BotState.ASK_QUESTION -> { // asking YES or NO
                            execute(service.ask_qiestion(currentUser,text));
                        }
                        case BotState.SHARE_PHONE -> {
                            execute(service.asking_code(currentUser,update));
                        }
                        case BotState.RESULT -> {
                            execute(service.result(currentUser,text));
                        }
                        default -> {
                        }


                    }

                    if (currentUser.getStatus().equals(BotState.SELECT_PROJECT)) {
                        execute(service.chooseProject(currentUser, update));
                    }
                }
            } else if (message.hasContact()){
                if (currentUser.getStatus().equals(BotState.SHARE_PHONE)) {
                    execute(service.asking_code(currentUser, update));
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(currentUser.getChatId());
                    sendMessage.setText("Something went wrong!");
                    execute(sendMessage);
                }
            }
        } else if (update.hasCallbackQuery()) {
            BotUser currentUser = service.findCurrentUser(update);
            CallbackQuery callbackQuery = update.getCallbackQuery();
            int data = Integer.parseInt(callbackQuery.getData());

            // doim bunda project name keladi :
            execute(service.showProject(currentUser,data));
        }
    }
}
