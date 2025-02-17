package com.atoss.idea.management.system.controller;

import java.util.List;
import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.service.AvatarService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Log4j2
@RequestMapping("aims/api/v1/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    /**
     * Constructor for creating an instance of the AvatarController.
     *
     * @param avatarService The AvatarService used by the controller to handle avatar-related operations.
     *
     * @see AvatarController
     * @see AvatarService
     */
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * Retrieves a list of all avatars and returns them as a ResponseEntity with HTTP status 200 (OK).
     *
     * This controller method calls the `getAllAvatars()` method of the AvatarService to fetch a list of all avatars.
     * It then wraps the list in a ResponseEntity and sets the HTTP status code to 200 (OK).
     *
     * @return A ResponseEntity containing a list of AvatarDTO objects representing all avatars in the system.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AvatarDTO>> getAllAvatars() {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieved all avatars");
        }
        List<AvatarDTO> avatars = avatarService.getAllAvatars();
        if (log.isInfoEnabled()) {
            log.info("Retrieved all avatars successfully");
        }

        return new ResponseEntity<>(avatars, HttpStatus.OK);
    }

}
