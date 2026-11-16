package com.beobase.beospring.app.web;

import com.beobase.beospring.app.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// The home page is public, so GET /apps is public too (see SecurityConfig)
@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
class AppController {

    private final AppService appService;

    @GetMapping
    AppsResponse getApps() {
        return new AppsResponse(appService.findAllApps());
    }
}
