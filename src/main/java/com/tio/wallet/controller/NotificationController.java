package com.tio.wallet.controller;

import com.tio.wallet.dto.response.ApiResponse;
import com.tio.wallet.dto.response.NotificationResponse;
import com.tio.wallet.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>>
    getNotificationsOfUser(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotificationsOfUser(auth.getName())));
    }
}
