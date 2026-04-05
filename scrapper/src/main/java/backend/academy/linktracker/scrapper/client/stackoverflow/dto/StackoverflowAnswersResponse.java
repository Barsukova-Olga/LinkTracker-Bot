package backend.academy.linktracker.scrapper.client.stackoverflow.dto;

import java.util.List;

public record StackoverflowAnswersResponse(List<StackoverflowAnswerResponse> items) {}
