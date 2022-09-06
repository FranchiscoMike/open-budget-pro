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
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BotService {
    private final BotUserRepository botUserRepository;
    private final ProjetRepository projectRepository;
    private final UserRepository userRepository;

    public SendMessage start(BotUser currentUser) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());


        List<Project> all = projectRepository.findAll();

        if (all.isEmpty()) {
            sendMessage.setText("Hozircha loyihalar mavjud emas!  /start");
            return sendMessage;
        }

        // projects : inline keyboard view :
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        sendMessage.setText("Iltimos, ovoz bermoqchi bo'lgan loyihangizni tanlang  ⤵️");
        for (Project project : all) {
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
        if (!byTitleId.isPresent()) {
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

    @SneakyThrows
    public SendPhoto showProject(BotUser currentUser, int data) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(currentUser.getChatId());

//        Optional<User> byBotUser_chatId = userRepository.findByBotUser_ChatId(currentUser.getChatId());
//
//        if (byBotUser_chatId.isPresent()){
//            sendMessage.setText("Siz avval ovoz berib bo'lgansiz!");
//            return sendMessage;
//        }

        Optional<Project> byTitleId = projectRepository.findByTitleId(data);

        if (byTitleId.isPresent()) {
            Project projectCha = byTitleId.get();
            sendPhoto.setProtectContent(true);

            File file = new File("src/main/resources/images/img.png");
            sendPhoto.setPhoto(new InputFile(file));

            String info = "";

            info += ("Loyihaning nomi " + projectCha.getTitle() + "\n");
            sendPhoto.setCaption(info + "\n\n" +
                    "Haqiqatdan ham siz ushbu loyihaga ovoz bermoqchimisiz?");

            Project project = byTitleId.get();

            // doimo userlar qatorida bo'ladi nasib bo'lsa lekin qachon xato beradi nomer jo'natmay qaytadan start bersa
            User user = new User();

            user.setBotUser(currentUser);
            user.setProject(project);

            userRepository.save(user);


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
            sendPhoto.setReplyMarkup(replyKeyboardMarkup);

            currentUser.setStatus(BotState.ASK_QUESTION);
            botUserRepository.save(currentUser); // doim save qilinadi
        }

        return sendPhoto;
    }

    public SendMessage ask_qiestion(BotUser currentUser, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        if (text.equals(Const_Words.YES)) {
            sendMessage.setText("Iltimos, quyidagi tugmani bosish orqali telefon raqamingizni yuboring");

            // reply
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setInputFieldPlaceholder("Masalan: 901234567 \n\n");

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

            sendMessage.setText("Quyidagilardan bironta loyihani tanlang" +
                    "  ⤵️");

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

        Message message = update.getMessage();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());


        // update da telefon raqam kelayapti to'g'rimi ? ha

        String phone = "";
        if (message.hasContact()) {
            phone = message.getContact().getPhoneNumber();
        } else if (message.hasText()) {
            // shu yerda raqamni verify qilish kerak
            String text = message.getText();

            boolean matches = Pattern.matches("^[3789]{1}[013456789]{1}[0-9]{7}$", text);
            if (matches) {
                phone = message.getText();
            } else {
                sendMessage.setText("Iltimos, namunadagidek raqam kiriting!");
                return sendMessage;
            }
        } else {
            sendMessage.setText("Iltimos, sizdan so'ralgan narsani kiriting!");
            return sendMessage;
        }

        // endi userni qidiramiza !!

        Optional<User> byPhoneNumber = userRepository.findByPhoneNumberAndBotUser_ChatId(phone, currentUser.getChatId());

        if (byPhoneNumber.isPresent()) {
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
            sendMessage.setText("Siz allaqachon ovoz berib bo'lgansiz!");
            return sendMessage;
        }


        User user = userRepository.findByBotUser_ChatIdAndPhoneNumberNull(currentUser.getChatId()).get();

        user.setPhoneNumber(phone);
        userRepository.save(user);


        currentUser.setStatus(BotState.WAITING);
        botUserRepository.save(currentUser); // saving user

        userRepository.save(user);


        sendMessage.setText("Sizga tez orada xabar yuborishimizni kuting\uD83D\uDE0A");
        //sendMessage.setText("please enter what is asked!");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        return sendMessage;

    }

//    public SendMessage result(BotUser currentUser, String text) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(currentUser.getChatId());
//
//        Optional<User> byBotUser_chatId = userRepository.findByBotUser_ChatId(currentUser.getChatId());
//
//        User user = byBotUser_chatId.get();
//
//        if (user.getCode() != null && user.getCode().equals(text)) {
//
//            sendMessage.setText("nasib bo'lsa shunda hisobingiz to'ldiriladi");
//
//        } else {
//            sendMessage.setText("Nimadir noto'g'ri bo'layapti");
//            userRepository.delete(user);
//        }
//        return sendMessage;
//    }

    public SendMessage defaultMessage(BotUser currentUser) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        sendMessage.setText("ILtimos sizdan so'ralgan narsani kiriting");

        return sendMessage;
    }

    public SendMessage waiting(BotUser currentUser, String code) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(currentUser.getChatId());

        Optional<User> byBotUser_chatId = userRepository.findByBotUser_ChatIdAndCodeNull(currentUser.getChatId());
        User user = byBotUser_chatId.get();

        if (!user.isCodeSent()) {
            sendMessage.setText("Sizga tez orada xabar yuborishimizni kuting\uD83D\uDE0A");
            return sendMessage;
        }

        user.setCode(code);
        userRepository.save(user);

        // shu yerda websocket bo'lishi kerak front uchun

        sendMessage.setText("Agar yuborgan kodingiz to'g'ri bo'lsa sizni hisobingiz tez orada to'ldiriladi!");

        return sendMessage;

    }
}
