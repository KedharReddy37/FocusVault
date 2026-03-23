package com.focusvault.auth_service.controller;

import com.focusvault.auth_service.dto.BrowsingEventRequestDto;
import com.focusvault.auth_service.dto.BrowsingEventResponseDto;
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

    public BrowsingEventController(BrowsingEventService browsingEventService) {
        this.browsingEventService = browsingEventService;
    }

    // Save a new browsing event
    @PostMapping
    public ResponseEntity<BrowsingEventResponseDto> saveEvent(
            @Valid @RequestBody BrowsingEventRequestDto request,
            Principal principal) {

        BrowsingEventResponseDto response =
                browsingEventService.saveEvent(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all my events
    @GetMapping
    public ResponseEntity<List<BrowsingEventResponseDto>> getMyEvents(
            Principal principal) {

        List<BrowsingEventResponseDto> events =
                browsingEventService.getMyEvents(principal.getName());
        return ResponseEntity.ok(events);
    }

    // Get summary — time spent per domain
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary(Principal principal) {

        Map<String, Long> summary =
                browsingEventService.getSummary(principal.getName());
        return ResponseEntity.ok(summary);
    }
}
