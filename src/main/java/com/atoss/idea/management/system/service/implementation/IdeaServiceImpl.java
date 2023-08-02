package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.IdeaRepositoryCustom;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.IdeaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;

    private final IdeaRepositoryCustom ideaRepositoryCustom;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final CategoryServiceImpl categoryService;

    private final ModelMapper modelMapper;

    private final CommentServiceImpl commentServiceImpl;

    public IdeaServiceImpl(IdeaRepository ideaRepository,
                           IdeaRepositoryCustom ideaRepositoryCustom,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           CategoryServiceImpl categoryService,
                           ModelMapper modelMapper,
                           CommentServiceImpl commentServiceImpl) {
        this.ideaRepository = ideaRepository;
        this.ideaRepositoryCustom = ideaRepositoryCustom;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.commentServiceImpl = commentServiceImpl;
    }

    @Override
    public IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username) {

        if (idea.getTitle() == null || idea.getTitle().isEmpty()) {
            throw new FieldValidationException("Please enter a valid title for the idea.");
        }

        if (idea.getStatus() == null) {
            throw new FieldValidationException("Please enter a valid status for the idea.");
        }

        if (idea.getCategoryList() == null || idea.getCategoryList().size() <= 0) {
            throw new FieldValidationException("Please enter a valid category for the idea.");
        }

        if (idea.getText() == null || idea.getText().isEmpty()) {
            throw new FieldValidationException("Please enter a valid text for the idea.");
        }

        Idea savedIdea = new Idea();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("No user found by this username."));
        savedIdea.setUser(user);
        savedIdea.setStatus(idea.getStatus());
        savedIdea.setText(idea.getText());
        savedIdea.setTitle(idea.getTitle());
        savedIdea.setCategoryList(new ArrayList<>());
        savedIdea.setCreationDate(new Date());

        if (idea.getImage() != null) {
            Image image = modelMapper.map(idea.getImage(), Image.class);
            savedIdea.setImage(image);
        }

        for (CategoryDTO categoryDTO : idea.getCategoryList()) {
            Category category = categoryRepository.findByText(modelMapper.map(categoryDTO, Category.class).getText());

            if (category == null) {
                savedIdea.getCategoryList().add(modelMapper.map(categoryDTO, Category.class));
            } else {
                savedIdea.getCategoryList().add(category);
            }
        }

        if (user.getIdeas() == null) {
            user.setIdeas(new ArrayList<>());
        }

        user.getIdeas().add(savedIdea);

        IdeaResponseDTO responseDTO = modelMapper.map(ideaRepository.save(savedIdea), IdeaResponseDTO.class);
        responseDTO.setUsername(username);
        return responseDTO;
    }

    @Override
    public IdeaResponseDTO getIdeaById(Long id) throws FieldValidationException {

        if (ideaRepository.findById(id).isPresent()) {
            Idea idea = ideaRepository.findById(id).get();
            IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
            responseDTO.setUsername(ideaRepository.findById(id).get().getUser().getUsername());
            responseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
            responseDTO.setCommentsNumber(idea.getCommentList().size());
            return responseDTO;
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) {

        if (ideaRepository.findById(id).isPresent()) {

            Idea idea = ideaRepository.findById(id).get();

            if (ideaUpdateDTO.getText() != null) {
                idea.setText(ideaUpdateDTO.getText());
            }

            if (ideaUpdateDTO.getStatus() != null) {
                idea.setStatus(ideaUpdateDTO.getStatus());
            }

            if (ideaUpdateDTO.getImage() != null) {
                idea.setImage(ideaUpdateDTO.getImage());
            }

            if (ideaUpdateDTO.getTitle() != null) {
                idea.setTitle(ideaUpdateDTO.getTitle());
            }

            if (ideaUpdateDTO.getCategoryList() != null) {
                idea.setCategoryList(new ArrayList<>());
                for (CategoryDTO category:ideaUpdateDTO.getCategoryList()) {
                    idea.getCategoryList().add(modelMapper.map(category, Category.class));
                }
            }

            IdeaResponseDTO responseDTO = modelMapper.map(ideaRepository.save(idea), IdeaResponseDTO.class);
            responseDTO.setUsername(ideaRepository.findById(id).get().getUser().getUsername());
            responseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
            responseDTO.setCommentsNumber(idea.getCommentList().size());
            return responseDTO;
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public void deleteIdeaById(Long id) {

        if (ideaRepository.existsById(id)) {
            ideaRepository.deleteById(id);
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public IdeaPageDTO getAllIdeas(Pageable pageable) {

        IdeaPageDTO ideaPageDTO = new IdeaPageDTO();

        if (ideaRepository.findAll().size() > 0) {
            ideaPageDTO.setTotal(ideaRepository.findAll().size());
        } else {
            throw new FieldValidationException("No ideas found.");
        }

        Page<Idea> ideas = ideaRepository.findAll(pageable);

        List<IdeaResponseDTO> ideaResponseDTOs = ideas.stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(idea.getUser().getUsername());
                    responseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
                    responseDTO.setCommentsNumber(idea.getCommentList().size());
                    return responseDTO;
                })
                .toList();

        ideaPageDTO.setPagedIdeas(new PageImpl(ideaResponseDTOs, pageable, ideas.getTotalElements()));

        return ideaPageDTO;
    }

    @Override
    public IdeaPageDTO getAllIdeasByUserUsername(String username, Pageable pageable) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User doesn't exist."));

        if (user.getIdeas() == null || user.getIdeas().isEmpty()) {
            throw new FieldValidationException("No ideas found.");
        }

        IdeaPageDTO ideaPageDTO = new IdeaPageDTO();

        ideaPageDTO.setTotal(userRepository.findByUsername(username).get().getIdeas().size());

        List<IdeaResponseDTO> ideaResponseDTOs = ideaRepository.findAllByUserUsername(username, pageable)
                .stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(user.getUsername());
                    responseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
                    responseDTO.setCommentsNumber(idea.getCommentList().size());
                    return responseDTO;
                })
                .toList();
        ideaPageDTO.setPagedIdeas(new PageImpl(ideaResponseDTOs, pageable, ideaResponseDTOs.size()));

        return ideaPageDTO;
    }

    @Override
    public IdeaPageDTO filterIdeasByAll(String title,
                                        String text,
                                        List<Status> statuses,
                                        List<String> categories,
                                        List<String> users,
                                        String selectedDateFrom,
                                        String selectedDateTo,
                                        String sortDirection,
                                        String username,
                                        Pageable pageable) {

        IdeaPageDTO ideaList = ideaRepositoryCustom.findIdeasByParameters(
                title, text, statuses, categories, users, selectedDateFrom, selectedDateTo, sortDirection, username, pageable);

        List<IdeaResponseDTO> result = ideaList.getPagedIdeas().stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(idea.getUser().getUsername());
                    responseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
                    responseDTO.setCommentsNumber(idea.getCommentList().size());
                    return responseDTO;
                })
                .toList();

        return new IdeaPageDTO(ideaList.getTotal(), new PageImpl(result, pageable, result.size()));
    }

    @Override
    public StatisticsDTO getStatistics() {

        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setNrOfUsers(userRepository.count());
        statisticsDTO.setNrOfIdeas(ideaRepository.count());
        statisticsDTO.setIdeasPerUser(Math.round((double) ideaRepository.count() / (double) userRepository.count() * 100) / 100.00);
        statisticsDTO.setImplementedIdeas(ideaRepository.countByStatus(Status.IMPLEMENTED));
        statisticsDTO.setDraftIdeas(ideaRepository.countByStatus(Status.DRAFT));
        statisticsDTO.setOpenIdeas(ideaRepository.countByStatus(Status.OPEN));
        statisticsDTO.setTotalNrOfComments(ideaRepository.countComments());
        statisticsDTO.setTotalNrOfReplies(ideaRepository.countAllReplies());

        return statisticsDTO;
    }
}

