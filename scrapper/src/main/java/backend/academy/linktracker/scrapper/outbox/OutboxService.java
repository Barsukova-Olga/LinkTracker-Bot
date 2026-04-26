package backend.academy.linktracker.scrapper.outbox;

import backend.academy.linktracker.scrapper.dto.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final JdbcOutboxRepository outboxRepository;

    public void saveLinkUpdate(String topic, LinkUpdateRequest request) {
        outboxRepository.save(
            topic,
            String.valueOf(request.id()),
            request
        );
    }
}
