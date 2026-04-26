package backend.academy.linktracker.scrapper.schedule.notifier;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.model.TrackedParsedLink;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingLinkUpdateNotifier implements LinkUpdateNotifier {

    @Override
    public void notifyUpdate(LinkUpdateRequest linkUpdateRequest) {
        log.info(
                "Stub update notification: chatId={}, linkId={}, url={}",
                linkUpdateRequest.tgChatIds(),
                linkUpdateRequest.id(),
                linkUpdateRequest.url());
    }
}
