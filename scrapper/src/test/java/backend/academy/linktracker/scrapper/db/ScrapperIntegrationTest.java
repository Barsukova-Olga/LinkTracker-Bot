package backend.academy.linktracker.scrapper.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScrapperIntegrationTest extends AbstractPostgresSpringBootTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("delete from link_tag");
        jdbcTemplate.execute("delete from chat_link");
        jdbcTemplate.execute("delete from links");
        jdbcTemplate.execute("delete from chats");
    }

    @LocalServerPort
    private int port;

    @Test
    void registerChatAddLinkAndGetLinks() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/1");
        assertEquals(200, registerStatus);

        String addBody = addLinkBody("https://github.com/openai/openai-java");

        int addStatus = sendPostWithChatId("/links", 1L, addBody);
        assertEquals(200, addStatus);

        HttpURLConnection getConnection = createConnection("GET", "/links");
        getConnection.setRequestProperty("Tg-Chat-Id", "1");

        int getStatus = getConnection.getResponseCode();
        String responseBody = readResponseBody(getConnection);

        assertEquals(200, getStatus);
        assertTrue(responseBody.contains("https://github.com/openai/openai-java"));
    }

    @Test
    void registerChatAddLinkDeleteLinkAndGetEmptyList() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/2");
        assertEquals(200, registerStatus);

        String addBody = addLinkBody("https://github.com/openai/openai-java");

        int addStatus = sendPostWithChatId("/links", 2L, addBody);
        assertEquals(200, addStatus);

        String deleteBody = removeLinkBody("https://github.com/openai/openai-java");

        int deleteStatus = sendDeleteWithChatId("/links", 2L, deleteBody);
        assertEquals(200, deleteStatus);

        HttpURLConnection getConnection = createConnection("GET", "/links");
        getConnection.setRequestProperty("Tg-Chat-Id", "2");

        int getStatus = getConnection.getResponseCode();
        String responseBody = readResponseBody(getConnection);

        assertEquals(200, getStatus);
        assertTrue(responseBody.contains("\"links\":[]")
                || !responseBody.contains("https://github.com/openai/openai-java"));
    }

    @Test
    void deleteLinkFromNonexistentChatShouldNotAffectExistingChat() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/3");
        assertEquals(200, registerStatus);

        String addBody = addLinkBody("https://github.com/openai/openai-java");

        int addStatus = sendPostWithChatId("/links", 3L, addBody);
        assertEquals(200, addStatus);

        String deleteBody = removeLinkBody("https://github.com/openai/openai-java");

        int deleteStatus = sendDeleteWithChatId("/links", 999L, deleteBody);
        assertTrue(deleteStatus != 200);

        HttpURLConnection getConnection = createConnection("GET", "/links");
        getConnection.setRequestProperty("Tg-Chat-Id", "3");

        int getStatus = getConnection.getResponseCode();
        String responseBody = readResponseBody(getConnection);

        assertEquals(200, getStatus);
        assertTrue(responseBody.contains("https://github.com/openai/openai-java"));
    }

    @Test
    void addLinkToNonexistentChatShouldFail() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/4");
        assertEquals(200, registerStatus);

        String addBody = addLinkBody("https://github.com/openai/openai-java");

        int addStatus = sendPostWithChatId("/links", 5L, addBody);

        assertTrue(addStatus != 200);
    }

    @Test
    void addLinkToDeletedChatShouldFail() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/6");
        assertEquals(200, registerStatus);

        int deleteChatStatus = sendDeleteWithoutBody("/tg-chat/6");
        assertEquals(200, deleteChatStatus);

        String addBody = addLinkBody("https://github.com/openai/openai-java");

        int addStatus = sendPostWithChatId("/links", 6L, addBody);

        assertTrue(addStatus != 200);
    }

    @Test
    void deleteNonexistentChatShouldReturn404() throws Exception {
        int deleteStatus = sendDeleteWithoutBody("/tg-chat/9999");

        assertEquals(404, deleteStatus);
    }

    private int sendPostWithoutBody(String path) throws Exception {
        HttpURLConnection connection = createConnection("POST", path);
        return connection.getResponseCode();
    }

    @Test
    void addInvalidLinkShouldReturn400() throws Exception {
        int registerStatus = sendPostWithoutBody("/tg-chat/7");
        assertEquals(200, registerStatus);

        String addBody = addLinkBody("not-a-valid-link");

        int addStatus = sendPostWithChatId("/links", 7L, addBody);

        assertEquals(400, addStatus);
    }

    private int sendPostWithChatId(String path, long chatId, String body) throws Exception {
        HttpURLConnection connection = createConnection("POST", path);
        connection.setRequestProperty("Tg-Chat-Id", String.valueOf(chatId));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes());
        }

        return connection.getResponseCode();
    }

    private HttpURLConnection createConnection(String method, String path) throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        return connection;
    }

    private String readResponseBody(HttpURLConnection connection) throws Exception {
        try (InputStream is = connection.getInputStream()) {
            return new String(is.readAllBytes());
        }
    }

    private int sendDeleteWithChatId(String path, long chatId, String body) throws Exception {
        HttpURLConnection connection = createConnection("DELETE", path);
        connection.setRequestProperty("Tg-Chat-Id", String.valueOf(chatId));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes());
        }

        return connection.getResponseCode();
    }

    private int sendDeleteWithoutBody(String path) throws Exception {
        HttpURLConnection connection = createConnection("DELETE", path);
        return connection.getResponseCode();
    }

    private String addLinkBody(String url) {
        return """
        {
          "link": "%s",
          "tags": ["work"],
          "filters": []
        }
        """.formatted(url);
    }

    private String removeLinkBody(String url) {
        return """
        {
          "link": "%s"
        }
        """.formatted(url);
    }
}
