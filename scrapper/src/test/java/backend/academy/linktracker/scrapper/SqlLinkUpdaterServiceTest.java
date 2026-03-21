package backend.academy.linktracker.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import backend.academy.linktracker.scrapper.client.BotClient;
import backend.academy.linktracker.scrapper.model.Link;
import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import backend.academy.linktracker.scrapper.schedule.LinkUpdateChecker;
import backend.academy.linktracker.scrapper.service.updater.LinkUpdaterService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestPropertySource(properties = "app.database.access-type=SQL")
class SqlLinkUpdaterServiceTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private LinkUpdaterService linkUpdateService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ChatLinkRepository chatLinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private LinkUpdateChecker linkUpdateChecker;

    @MockitoBean
    private BotClient botClient;

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

        when(linkUpdateChecker.getCurrentLastUpdatedAt(any(Link.class)))
                .thenReturn(java.util.Optional.of(newUpdatedAt));

        linkUpdateService.update();

        Link updatedLink = linkRepository.findById(link.id()).orElseThrow();

        assertEquals(newUpdatedAt, updatedLink.lastUpdatedAt());
        verify(botClient, times(1)).sendUpdate(any());
    }
}
