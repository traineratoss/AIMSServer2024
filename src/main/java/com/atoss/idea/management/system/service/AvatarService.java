package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import java.util.List;

public interface AvatarService {
    /**
     * Retrieves a list of all avatars and maps them to a list of AvatarDTO objects.
     *
     * This method fetches all avatars from the AvatarRepository and uses the ModelMapper to convert each Avatar entity
     * to an AvatarDTO object. The resulting list of AvatarDTO objects is then returned.
     *
     * @return A list of AvatarDTO objects representing all avatars in the system.
     */
    List<AvatarDTO> getAllAvatars();
}
