package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IdeaService {
    IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username);

    IdeaResponseDTO getIdeaById(Long id);

    IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO);

    void deleteIdeaById(Long id);

    Page<IdeaResponseDTO> getAllIdeas(Pageable pageable);

    Page<IdeaResponseDTO> getAllIdeasByUserId(Long id, Pageable pageable);

    Page<IdeaResponseDTO> filterIdeasByAll(String title,
                                           String text,
                                           List<Status> status,
                                           List<String> categories,
                                           List<String> user,
                                           Pageable pageable);

}