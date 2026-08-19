package com.tio.wallet.controller;

import com.tio.wallet.dto.request.SavedRecipientRequest;
import com.tio.wallet.dto.response.ApiResponse;
import com.tio.wallet.dto.response.RecipientResponse;
import com.tio.wallet.service.RecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
public class RecipientController {
    private final RecipientService recipientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecipientResponse>>>
    getMySavedRecipient(
            Authentication auth
    ) {
        return ResponseEntity.ok(ApiResponse.success(recipientService.list(auth.getName())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecipientResponse>>
    upsert(
            Authentication auth,
            @Valid @RequestBody SavedRecipientRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success(recipientService.upsert(auth.getName(), req)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>>
    delete(
            Authentication auth,
            @RequestParam String email
    ) {
        recipientService.delete(auth.getName(), email);
        return ResponseEntity.ok(ApiResponse.success("Đã xoá thành công!"));
    }

}
