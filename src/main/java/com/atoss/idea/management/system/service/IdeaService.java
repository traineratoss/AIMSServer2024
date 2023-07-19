package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import java.util.List;

public interface IdeaService {
    IdeaDTO addIdea(Idea idea) throws ValidationException;

    IdeaDTO getIdeaById(Long id) throws ValidationException;

    IdeaDTO updateIdeaById(IdeaDTO ideaDTO) throws ValidationException;

    void deleteIdeaById(Long id) throws ValidationException;

    List<IdeaDTO> getAllIdeas() throws ValidationException;
}