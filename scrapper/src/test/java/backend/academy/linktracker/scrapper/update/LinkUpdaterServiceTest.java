package backend.academy.linktracker.scrapper.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.db.AbstractPostgresSpringBootTest;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdateProcessor;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class LinkUpdaterServiceTest extends AbstractPostgresSpringBootTest {

    @Autowired
    protected LinkUpdaterService linkUpdateService;

    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected LinkRepository linkRepository;

    @Autowired
    protected ChatLinkRepository chatLinkRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockitoBean
    protected LinkUpdateProcessor linkUpdateProcessor;

    @MockitoBean
    protected BotClient botClient;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @Test
    void shouldUpdateLastUpdatedAtAndNotifyBot() {
        long chatId = 1L;
        String url = "https://github.com/openai/openai-java";
        Instant newUpdatedAt = Instant.parse("2026-03-20T10:00:00Z");

        chatRepository.add(chatId);
        Link link = linkRepository.add(url);
        chatLinkRepository.add(chatId, link.id());

        when(linkUpdateProcessor.process(any(Link.class)))
                .thenReturn(Optional.of(new LinkUpdateEvent(
                        link.id(), URI.create(url), newUpdatedAt, "Issue title", "alice", "preview")));

        linkUpdateService.update();

        Link updatedLink = linkRepository.findById(link.id()).orElseThrow();

        assertEquals(newUpdatedAt, updatedLink.lastUpdatedAt());
        verify(botClient, times(1)).sendUpdate(any());
    }
}
