package backend.academy.linktracker.scrapper.service.updater;

import java.util.List;

public record LinkUpdateReport(
    int totalProcessed,
    int totalUpdated,
    List<String> failedLinks
) {}
