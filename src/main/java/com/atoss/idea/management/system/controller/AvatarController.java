package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.service.AvatarService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("aims/api/v1/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping
    public AvatarDTO addAvatar(@RequestParam(value = "username") String username,
                               @RequestParam("file") MultipartFile file) throws IOException {
        return avatarService.addAvatar(username, file);
    }

    @DeleteMapping
    public void deleteAvatar(@RequestParam(value = "username") String username) {
        avatarService.deleteAvatar(username);
    }

    @GetMapping
    public AvatarDTO getAvatar(@RequestParam(value = "username") String username) {
        return avatarService.getAvatar(username);
    }

    @PutMapping
    public AvatarDTO updateAvatar(@RequestParam(name = "username") String username,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        return avatarService.updateAvatar(username, file);
    }
}
