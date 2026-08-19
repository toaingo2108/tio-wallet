package com.tio.wallet.controller;

import com.tio.wallet.dto.response.ApiResponse;
import com.tio.wallet.dto.response.UserLookupResponse;
import com.tio.wallet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/lookup")
    public ResponseEntity<ApiResponse<UserLookupResponse>>
    getUserLookup(
            @RequestParam String email
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserLookup(email)));
    }
}
