package backend.academy.linktracker.bot.command;

import org.springframework.stereotype.Component;

@Component
public class UnknownCommand {

    public String handle(String command) {
        return "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.";
    }
}
