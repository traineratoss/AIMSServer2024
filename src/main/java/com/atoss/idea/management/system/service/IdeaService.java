package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IdeaService {
    IdeaRequestDTO addIdea(IdeaRequestDTO idea) throws ValidationException;

    IdeaRequestDTO getIdeaById(Long id) throws ValidationException;

    IdeaRequestDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) throws ValidationException;

    void deleteIdeaById(Long id) throws ValidationException;

    Page<IdeaRequestDTO> getAllIdeas(Pageable pageable) throws ValidationException;

    Page<IdeaRequestDTO> getAllIdeasByUserId(Long id, Pageable pageable) throws ValidationException;

}