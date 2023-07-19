package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.IdeaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdeaServiceImpl implements IdeaService {
    @Override
    public IdeaDTO addIdea(Idea idea) throws ValidationException {
        return null;
    }

    @Override
    public IdeaDTO getIdeaById(Long id) throws ValidationException {
        return null;
    }

    @Override
    public IdeaDTO updateIdeaById(IdeaDTO ideaDTO) throws ValidationException {
        return null;
    }

    @Override
    public void deleteIdeaById(Long id) throws ValidationException {

    }

    @Override
    public List<IdeaDTO> getAllIdeas() throws ValidationException {
        return null;
    }
}
