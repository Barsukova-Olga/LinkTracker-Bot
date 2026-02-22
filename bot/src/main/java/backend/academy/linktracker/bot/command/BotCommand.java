package backend.academy.linktracker.bot.command;

public interface BotCommand {
    String command();

    String handle(long chatId, String text);
}
