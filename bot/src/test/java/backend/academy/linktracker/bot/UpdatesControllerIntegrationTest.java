package backend.academy.linktracker.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pengrad.telegrambot.TelegramBot;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdatesControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private TelegramBot telegramBot;

    @Test
    void validUpdateRequestReturns200() throws Exception {
        HttpURLConnection connection = createPostConnection("/updates");

        String body = """
            {
              "id": 1,
              "url": "https://github.com/user/repo",
              "description": "Обнаружено обновление",
              "tgChatIds": [123]
            }
            """;

        writeBody(connection, body);

        int statusCode = connection.getResponseCode();

        assertEquals(200, statusCode);
    }

    @Test
    void invalidUpdateRequestReturns400() throws Exception {
        HttpURLConnection connection = createPostConnection("/updates");

        String body = """
            {
              "id": 1,
              "description": "Обнаружено обновление",
              "tgChatIds": []
            }
            """;

        writeBody(connection, body);

        int statusCode = connection.getResponseCode();

        assertEquals(400, statusCode);
    }

    private HttpURLConnection createPostConnection(String path) throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private void writeBody(HttpURLConnection connection, String body) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.getBytes());
        }
    }
}
