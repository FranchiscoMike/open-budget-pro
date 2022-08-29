package com.example.openbudget.budget_bot;

import com.example.openbudget.budget_bot.consts.Const_Words;
import com.example.openbudget.entity.BotUser;
import com.example.openbudget.entity.Project;
import com.example.openbudget.entity.User;
import com.example.openbudget.entity.enums.BotState;
import com.example.openbudget.repository.BotUserRepository;
import com.example.openbudget.repository.ProjetRepository;
import com.example.openbudget.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotService {
    private final BotUserRepository botUserRepository;
    private final ProjetRepository projectRepository;
    private final UserRepository userRepository;

    public SendMessage start(BotUser currentUser) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());
        sendMessage.setText("Please choose one project for voting  ⤵️");

        // projects : inline keyboard view :
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (Project project : projectRepository.findAll()) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(project.getTitle());
            inlineKeyboardButton.setCallbackData(project.getTitleId().toString());

            row.add(inlineKeyboardButton);

            rowList.add(row);

        }

        currentUser.setStatus(BotState.SHOW_PROJECT);
        botUserRepository.save(currentUser);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public BotUser findCurrentUser(Update update) {
        Long chatId = 0l;
        if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatId = update.getMessage().getChatId();
        }
        Optional<BotUser> optionalBotUser = botUserRepository.findByChatId(chatId.toString());
        if (optionalBotUser.isPresent()) {
            return optionalBotUser.get();
        } else {
            BotUser botUser = new BotUser();
            botUser.setChatId(chatId.toString());
            botUser.setStatus(BotState.SELECT_PROJECT);

            return botUserRepository.save(botUser);
        }
    }


    public SendMessage chooseProject(BotUser currentUser, Update update) {
        Integer id;
        try {
            id = Integer.valueOf(update.getMessage().getText());
        } catch (NumberFormatException e) {
            return new SendMessage(currentUser.getChatId(), "Id must be a number");
        }
        Optional<Project> byTitleId = projectRepository.findByTitleId(id);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());
        if (byTitleId.isEmpty()) {
            sendMessage.setText("project not found");
            return sendMessage;
        }
        Project project = byTitleId.get();
        currentUser.setStatus(BotState.ASK_QUESTION);
        botUserRepository.save(currentUser);

        String project_info = "\uD83D\uDCCA Project_name : " + project.getTitle();
        sendMessage.setText(project_info);

        // reply
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("Enter something");

        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button1 = new KeyboardButton("✅ Yes");
        KeyboardButton button2 = new KeyboardButton("❌ No");

        row.add(button1);
        row.add(button2);

        rowList.add(row);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;
    }

    public SendMessage showProject(BotUser currentUser, int data) {
        Optional<Project> byTitleId = projectRepository.findByTitleId(data);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        if (byTitleId.isPresent()) {
            sendMessage.setText("Project bo'ladi nasib bo'lsa bu yerda\n\n " + byTitleId.get());

            Project project = byTitleId.get();

            if (userRepository.findByBotUser_ChatId(currentUser.getChatId()).isEmpty()) {

                User user = new User();

                user.setBotUser(currentUser);
                user.setProject(project);

                userRepository.save(user);
            }

            // reply
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setInputFieldPlaceholder("Please choose from below");

            List<KeyboardRow> rowList = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();

            KeyboardButton button1 = new KeyboardButton(Const_Words.YES);
            KeyboardButton button2 = new KeyboardButton(Const_Words.NO);

            row.add(button1);
            row.add(button2);

            rowList.add(row);
            replyKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            currentUser.setStatus(BotState.ASK_QUESTION);
            botUserRepository.save(currentUser); // doim save qilinadi
        } else {
            sendMessage.setText("Project not found may be deleted!");
        }

        return sendMessage;
    }

    public SendMessage ask_qiestion(BotUser currentUser, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        if (text.equals(Const_Words.YES)) {
            sendMessage.setText("Please share your phone_number");

            // reply
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setInputFieldPlaceholder("Please share your phone \n\n");

            List<KeyboardRow> rowList = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();

            KeyboardButton button1 = new KeyboardButton(Const_Words.SHARE_PHONE);
            button1.setRequestContact(true);

            row.add(button1);

            rowList.add(row);
            replyKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            currentUser.setStatus(BotState.SHARE_PHONE);
            botUserRepository.save(currentUser); // doim save qilinadi

        } else if (text.equals(Const_Words.NO)) {

            //delete from users :
            userRepository.deleteByBotUser_ChatId(currentUser.getChatId());

            sendMessage.setText("Please choose one project for voting  ⤵️");

            // projects : inline keyboard view :
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

            for (Project project : projectRepository.findAll()) {
                List<InlineKeyboardButton> row = new ArrayList<>();

                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(project.getTitle());
                inlineKeyboardButton.setCallbackData(project.getTitleId().toString());

                row.add(inlineKeyboardButton);

                rowList.add(row);

            }

            currentUser.setStatus(BotState.SHOW_PROJECT);
            botUserRepository.save(currentUser);


            inlineKeyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        }
        return sendMessage;
    }

    public SendMessage asking_code(BotUser currentUser, Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());



        Contact contact = update.getMessage().getContact();


        if (contact != null) {
            Optional<User> byBotUser_chatId = userRepository.findByBotUser_ChatId(currentUser.getChatId());

            User user = byBotUser_chatId.get();

            user.setPhoneNumber(contact.getPhoneNumber());
            System.out.println("contact.getPhoneNumber() = " + contact.getPhoneNumber());


            currentUser.setStatus(BotState.WAITING);
            botUserRepository.save(currentUser); // saving user

            userRepository.save(user);

            sendMessage.setText("please wait till get message to your phone!");


        } else {

            sendMessage.setText("please enter what is asked!");

        }

        return sendMessage;
    }

    public SendMessage result(BotUser currentUser, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        Optional<User> byBotUser_chatId = userRepository.findByBotUser_ChatId(currentUser.getChatId());

        User user = byBotUser_chatId.get();

        if (user.getCode()!= null && user.getCode().equals(text)){

            sendMessage.setText("nasib bo'lsa shunda hisobingiz to'ldiriladi");

        } else {
            sendMessage.setText("Something went wrong!");
            userRepository.delete(user);
        }
        return sendMessage;
    }
}
