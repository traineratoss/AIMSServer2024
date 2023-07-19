package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.entity.Idea;

import java.util.List;

public interface IdeaService {
    public IdeaDTO addIdea(Idea idea) throws ValidationException;

    public IdeaDTO getIdeaById(Long id) throws ValidationException;

    public IdeaDTO updateIdeaById(Long id, IdeaDTO ideaDTO) throws ValidationException;

    public void deleteIdeaById(Long id) throws ValidationException;

    public List<IdeaDTO> getAllIdeas() throws ValidationException;
}