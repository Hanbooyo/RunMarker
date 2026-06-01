package com.stravamate.passport.controller;

import com.stravamate.passport.security.CurrentUserResolver;
import com.stravamate.passport.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final CurrentUserResolver currentUserResolver;

    public AccountController(AccountService accountService, CurrentUserResolver currentUserResolver) {
        this.accountService = accountService;
        this.currentUserResolver = currentUserResolver;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            HttpServletRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        Long userId = currentUserResolver.resolveUserId(request, userIdHeader);
        accountService.deleteAccount(userId);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
