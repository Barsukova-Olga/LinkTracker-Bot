package backend.academy.linktracker.scrapper.schedule;

import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingLinkUpdateNotifier implements LinkUpdateNotifier {

    @Override
    public void notifyUpdate(long chatId, TrackedParsedLink link) {
        log.info(
                "Stub update notification: chatId={}, linkId={}, url={}",
                chatId,
                link.id(),
                link.parsedLink().uri());
    }
}
