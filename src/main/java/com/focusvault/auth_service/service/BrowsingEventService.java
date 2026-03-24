package com.focusvault.auth_service.service;

import com.focusvault.auth_service.dto.BrowsingEventRequestDto;
import com.focusvault.auth_service.dto.BrowsingEventResponseDto;
import com.focusvault.auth_service.entity.BrowsingEvent;
import com.focusvault.auth_service.entity.User;
import com.focusvault.auth_service.repository.BrowsingEventRepository;
import com.focusvault.auth_service.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrowsingEventService {

    private final BrowsingEventRepository browsingEventRepository;
    private final UserRepository userRepository;

    public BrowsingEventService(BrowsingEventRepository browsingEventRepository,
                                 UserRepository userRepository) {
        this.browsingEventRepository = browsingEventRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "summary", key = "#email")
    public BrowsingEventResponseDto saveEvent(BrowsingEventRequestDto request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BrowsingEvent event = BrowsingEvent.builder()
                .domain(request.getDomain())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .category(request.getCategory())
                .user(user)
                .build();

        browsingEventRepository.save(event);

        return toResponseDto(event);
    }

    public List<BrowsingEventResponseDto> getMyEvents(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return browsingEventRepository
                .findByUserOrderByStartTimeDesc(user)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "summary", key = "#email")
    public Map<String, Long> getSummary(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BrowsingEvent> events = browsingEventRepository
                .findByUserOrderByStartTimeDesc(user);

        Map<String, Long> summary = new HashMap<>();
        for (BrowsingEvent event : events) {
            long seconds = Duration.between(
                    event.getStartTime(), event.getEndTime()).getSeconds();
            summary.merge(event.getDomain(), seconds, Long::sum);
        }

        return summary;
    }

    private BrowsingEventResponseDto toResponseDto(BrowsingEvent event) {
        long durationSeconds = Duration.between(
                event.getStartTime(), event.getEndTime()).getSeconds();

        return BrowsingEventResponseDto.builder()
                .id(event.getId())
                .domain(event.getDomain())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .category(event.getCategory())
                .durationSeconds(durationSeconds)
                .build();
    }
}
