package backend.academy.linktracker.scrapper.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import backend.academy.linktracker.scrapper.repository.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repository.ChatRepository;
import backend.academy.linktracker.scrapper.repository.LinkRepository;
import backend.academy.linktracker.scrapper.repository.LinkTagRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.database.access-type=SQL", "spring.task.scheduling.enabled=false"})
class SqlLinkTagRepositoryTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private ChatLinkRepository chatLinkRepository;

    @Autowired
    private LinkTagRepository linkTagRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @Test
    void shouldAddAndFindTags() {
        long chatId = 1L;
        chatRepository.add(chatId);

        var link = linkRepository.add("https://github.com/openai/openai-java");
        chatLinkRepository.add(chatId, link.id());

        linkTagRepository.addTag(chatId, link.id(), "work");
        linkTagRepository.addTag(chatId, link.id(), "backend");

        List<String> tags = linkTagRepository.findTags(chatId, link.id());

        assertEquals(List.of("backend", "work"), tags);
    }

    @Test
    void shouldRemoveSingleTag() {
        long chatId = 2L;
        chatRepository.add(chatId);

        var link = linkRepository.add("https://github.com/spring-projects/spring-boot");
        chatLinkRepository.add(chatId, link.id());

        linkTagRepository.addTag(chatId, link.id(), "java");
        linkTagRepository.addTag(chatId, link.id(), "framework");

        linkTagRepository.removeTag(chatId, link.id(), "java");

        List<String> tags = linkTagRepository.findTags(chatId, link.id());

        assertEquals(List.of("framework"), tags);
    }

    @Test
    void shouldRemoveAllTags() {
        long chatId = 3L;
        chatRepository.add(chatId);

        var link = linkRepository.add("https://stackoverflow.com/questions/1/test");
        chatLinkRepository.add(chatId, link.id());

        linkTagRepository.addTag(chatId, link.id(), "study");
        linkTagRepository.addTag(chatId, link.id(), "algorithms");

        linkTagRepository.removeAllTags(chatId, link.id());

        List<String> tags = linkTagRepository.findTags(chatId, link.id());

        assertEquals(List.of(), tags);
    }

    @Test
    void shouldIgnoreDuplicateTag() {
        long chatId = 4L;
        chatRepository.add(chatId);

        var link = linkRepository.add("https://example.com/tag-test");
        chatLinkRepository.add(chatId, link.id());

        linkTagRepository.addTag(chatId, link.id(), "work");
        linkTagRepository.addTag(chatId, link.id(), "work");

        List<String> tags = linkTagRepository.findTags(chatId, link.id());

        assertEquals(List.of("work"), tags);
    }
}
