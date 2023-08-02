package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.IdeaService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> badWords = new ArrayList<>();

    private final IdeaRepository ideaRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    private final CommentServiceImpl commentServiceImpl;

    public IdeaServiceImpl(IdeaRepository ideaRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ModelMapper modelMapper,
                           CommentServiceImpl commentServiceImpl) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.commentServiceImpl = commentServiceImpl;
    }

    private String filterBadWords(String text) {
        for (String word : badWords) {
            text = text.replaceAll("(?i)" + word, "*".repeat(word.length()));
        }
        return text;
    }

    private void readBadWordsFromFile(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String word = line.trim();

                badWords.add(word);
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        readBadWordsFromFile("src/main/resources/textTerms/badWords.txt");

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("No user found by this username."));
        savedIdea.setUser(user);
        savedIdea.setStatus(idea.getStatus());
        String filteredIdeaText = filterBadWords(idea.getText());
        savedIdea.setText(filteredIdeaText);
        String filteredIdeaTitle = filterBadWords(idea.getTitle());
        savedIdea.setTitle(filteredIdeaTitle);
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
        readBadWordsFromFile("src/main/resources/textTerms/badWords.txt");

        if (ideaRepository.findById(id).isPresent()) {

            Idea idea = ideaRepository.findById(id).get();

            if (ideaUpdateDTO.getText() != null) {
                idea.setText(ideaUpdateDTO.getText());
                String filteredCommentText = filterBadWords(idea.getText());
                idea.setText(filteredCommentText);
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

                if (ideaUpdateDTO.getCategoryList().isEmpty()) {
                    throw new RuntimeException("Please select at least one category");
                }

                idea.setCategoryList(new ArrayList<>());
                List<Category> newList = new ArrayList<>();

                for (CategoryDTO category: ideaUpdateDTO.getCategoryList()) {

                    Category newCategory = categoryRepository.findByText(category.getText());
                    if (newCategory == null) {
                        Category addedCategory = new Category();
                        addedCategory.setText(category.getText());
                        categoryRepository.save(addedCategory);
                        newList.add(addedCategory);
                    } else {
                        newList.add(newCategory);
                    }

                }

                idea.setCategoryList(newList);
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

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Idea> criteriaQuery = cb.createQuery(Idea.class);
        Root<Idea> root = criteriaQuery.from(Idea.class);

        List<Predicate> predicatesList = new ArrayList<>();

        if (username != null) {
            predicatesList.add(cb.equal(root.join("user").get("username"), username));
        }

        if (title != null) {
            predicatesList.add(cb.like(root.get("title"), "%" + title + "%"));
        }

        if (text != null) {
            predicatesList.add(cb.like(root.get("text"), "%" + text + "%"));
        }

        if (statuses != null && !statuses.isEmpty()) {
            predicatesList.add(root.get("status").in(statuses));
        }

        if (users != null && !users.isEmpty() && username == null) {
            predicatesList.add(root.join("user").get("username").in(users));
        }

        if (categories != null && !categories.isEmpty()) {
            predicatesList.add(root.join("categoryList").get("text").in(categories));
        }

        predicatesList.addAll(filterByDate(selectedDateFrom, selectedDateTo, root, cb));

        List<Order> orders = new ArrayList<>();

        if (Objects.equals(sortDirection, "ASC")) {
            orders.add(cb.asc(root.get("creationDate")));
        } else {
            orders.add(cb.desc(root.get("creationDate")));
        }

        criteriaQuery.orderBy(orders);
        criteriaQuery.where(predicatesList.toArray(new Predicate[0]));
        TypedQuery<Idea> query = entityManager.createQuery(criteriaQuery);

        int total = query.getResultList().size();

        List<Idea> allIdeas = query.getResultList();

        List<IdeaResponseDTO> allIdeasDTO = new ArrayList<>();

        allIdeasDTO = allIdeas.stream().map(idea -> {
            IdeaResponseDTO ideaResponseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
            ideaResponseDTO.setUsername(idea.getUser().getUsername());
            ideaResponseDTO.setElapsedTime(commentServiceImpl.getElapsedTime(idea.getCreationDate()));
            ideaResponseDTO.setCommentsNumber(idea.getCommentList().size());
            return ideaResponseDTO;
        }).toList();

        if (pageable != null) {

            int firstIndex = pageable.getPageNumber() * pageable.getPageSize();

            List<IdeaResponseDTO> pagedIdeas = new ArrayList<>();
            for (int i = 0; i < pageable.getPageSize(); i++) {
                if (firstIndex < allIdeas.size()) {
                    pagedIdeas.add(allIdeasDTO.get(firstIndex));
                    firstIndex++;
                }
            }
            IdeaPageDTO ideaPageDTO = new IdeaPageDTO();
            ideaPageDTO.setPagedIdeas(new PageImpl<>(pagedIdeas, pageable, total));
            ideaPageDTO.setTotal(total);
            return ideaPageDTO;
        }

        return new IdeaPageDTO(total, new PageImpl<>(allIdeasDTO, Pageable.unpaged(), total));
    }

    @Override
    public FilteredStatisticsDTO getStatisticsByDate(List<Status> statuses,
                                                     List<String> categories,
                                                     List<String> users,
                                                     String selectedDateFrom,
                                                     String selectedDateTo) {

        FilteredStatisticsDTO filteredStatisticsDTO = new FilteredStatisticsDTO();

        return getFilteredStatistics(filterIdeasByAll(
                null, null, statuses, categories, users, selectedDateFrom, selectedDateTo, null, null, null));

    }

    @Override
    public StatisticsDTO getGeneralStatistics() {

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        Long nrOfUsers = userRepository.count();
        Long nrOfIdeas = ideaRepository.count();
        Double ideasPerUser = Math.round((double) nrOfIdeas / (double) nrOfUsers * 100) / 100.00;
        Long implIdeas = ideaRepository.countByStatus(Status.IMPLEMENTED);
        Long draftedIdeas = ideaRepository.countByStatus(Status.DRAFT);
        Long openIdeas = nrOfIdeas - draftedIdeas - implIdeas;
        Long nrOfComments = ideaRepository.countComments();
        Long nrOfReplies = ideaRepository.countAllReplies();


        statisticsDTO.setNrOfUsers(nrOfUsers);
        statisticsDTO.setNrOfIdeas(nrOfIdeas);
        statisticsDTO.setIdeasPerUser(ideasPerUser);
        statisticsDTO.setImplementedIdeas(implIdeas);
        statisticsDTO.setDraftIdeas(draftedIdeas);
        statisticsDTO.setOpenIdeas(openIdeas);
        statisticsDTO.setTotalNrOfComments(nrOfComments);
        statisticsDTO.setTotalNrOfReplies(nrOfReplies);

        return statisticsDTO;
    }

    @Override
    public FilteredStatisticsDTO getFilteredStatistics(IdeaPageDTO ideaPageDTO) {

        List<Idea> ideaList = new ArrayList<>();

        FilteredStatisticsDTO filteredStatisticsDTO = new FilteredStatisticsDTO();


        return filteredStatisticsDTO;
    }

    @Override
    public List<Predicate> filterByDate(String selectedDateFrom, String selectedDateTo, Root<Idea> root, CriteriaBuilder cb) {

        List<Predicate> predicatesList = new ArrayList<>();

        if (selectedDateFrom != null && selectedDateTo == null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = simpleDateFormat.parse(selectedDateFrom);
                predicatesList.add(cb.greaterThanOrEqualTo(root.get("creationDate"), fromDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (selectedDateFrom == null && selectedDateTo != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date toDate = simpleDateFormat.parse(selectedDateTo);
                predicatesList.add(cb.lessThanOrEqualTo(root.get("creationDate"), toDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (selectedDateFrom != null && selectedDateTo != null) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = simpleDateFormat.parse(selectedDateFrom);
                Date toDate = simpleDateFormat.parse(selectedDateTo);
                predicatesList.add(cb.between(root.get("creationDate"), fromDate, toDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return predicatesList;
    }


}

