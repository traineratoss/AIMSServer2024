package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IdeaService {
    IdeaDTO addIdea(IdeaDTO idea) throws ValidationException;

    IdeaDTO getIdeaById(Long id) throws ValidationException;

    IdeaDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) throws ValidationException;

    void deleteIdeaById(Long id) throws ValidationException;

    Page<IdeaDTO> getAllIdeas(Pageable pageable) throws ValidationException;

    List<IdeaDTO> getAllIdeasByUserId(Long id) throws ValidationException;

}