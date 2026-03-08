package backend.academy.linktracker.bot.contoller;

import backend.academy.linktracker.bot.dto.LinkUpdateRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdatesController {

    private final TelegramBot telegramBot;

    @PostMapping("/updates")
    public ResponseEntity<Void> handleUpdate(@Valid @RequestBody LinkUpdateRequest request) {
        String text = buildMessage(request);

        for (Long chatId : request.tgChatIds()) {
            telegramBot.execute(new SendMessage(chatId, text));
        }

        return ResponseEntity.ok().build();
    }

    private String buildMessage(LinkUpdateRequest request) {
        return request.description() + "\n" + request.url();
    }
}
