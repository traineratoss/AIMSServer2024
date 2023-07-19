package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import java.util.List;

public interface IdeaService {
    IdeaDTO addIdea(IdeaDTO idea) throws ValidationException;

    IdeaDTO getIdeaById(Long id) throws ValidationException;

    IdeaDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) throws ValidationException;

    void deleteIdeaById(Long id) throws ValidationException;

    List<IdeaDTO> getAllIdeas() throws ValidationException;
}