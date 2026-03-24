package com.focusvault.auth_service.controller;

import com.focusvault.auth_service.dto.BrowsingEventMessage;
import com.focusvault.auth_service.dto.BrowsingEventRequestDto;
import com.focusvault.auth_service.dto.BrowsingEventResponseDto;
import com.focusvault.auth_service.service.BrowsingEventProducer;
import com.focusvault.auth_service.service.BrowsingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class BrowsingEventController {

    private final BrowsingEventService browsingEventService;
    private final BrowsingEventProducer browsingEventProducer;

    public BrowsingEventController(BrowsingEventService browsingEventService,
                                    BrowsingEventProducer browsingEventProducer) {
        this.browsingEventService = browsingEventService;
        this.browsingEventProducer = browsingEventProducer;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> saveEvent(
            @Valid @RequestBody BrowsingEventRequestDto request,
            Principal principal) {

        // Build the Kafka message
        BrowsingEventMessage message = new BrowsingEventMessage(
                principal.getName(),
                request.getDomain(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCategory()
        );

        // Send to Kafka — returns instantly
        browsingEventProducer.sendEvent(message);

        // Return immediately — don't wait for DB save
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of(
                    "status", "accepted",
                    "message", "Event received and being processed"
                ));
    }

    @GetMapping
    public ResponseEntity<List<BrowsingEventResponseDto>> getMyEvents(
            Principal principal) {
        List<BrowsingEventResponseDto> events =
                browsingEventService.getMyEvents(principal.getName());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary(Principal principal) {
        Map<String, Long> summary =
                browsingEventService.getSummary(principal.getName());
        return ResponseEntity.ok(summary);
    }
}
