package backend.academy.linktracker.bot.controller;

import backend.academy.linktracker.bot.dto.LinkUpdateRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatesService {
    private final TelegramBot telegramBot;

    public void handleUpdate(LinkUpdateRequest request) {
        String text = buildMessage(request);

        for (Long chatId : request.tgChatIds()) {
            telegramBot.execute(new SendMessage(chatId, text));
        }
    }

    private String buildMessage(LinkUpdateRequest request) {
        return request.description() + "\n" + request.url();
    }
}
