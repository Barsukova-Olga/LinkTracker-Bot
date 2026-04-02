package backend.academy.linktracker.bot.controller;

import backend.academy.linktracker.bot.dto.LinkUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdatesController {

    private final UpdatesService updatesService;

    @PostMapping("/updates")
    public ResponseEntity<Void> handleUpdate(@Valid @RequestBody LinkUpdateRequest request) {
        updatesService.handleUpdate(request);
        return ResponseEntity.ok().build();
    }
}
