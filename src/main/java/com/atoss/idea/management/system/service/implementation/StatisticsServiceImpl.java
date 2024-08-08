package com.atoss.idea.management.system.service.implementation;


import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.CommentStatisticsDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import com.atoss.idea.management.system.service.CommentService;
import com.atoss.idea.management.system.service.IdeaService;
import com.atoss.idea.management.system.service.StatisticsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @PersistenceContext
    // used to interact with the database (an instance of the JPA)
    private EntityManager entityManager;

    private final ModelMapper modelMapper;

    private final IdeaService ideaService;

    private final IdeaRepository ideaRepository;

    private final CommentService commentService;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final HtmlServiceImpl htmlService;


    /**
     * Constructor
     *
     * @param modelMapper ==
     * @param ideaService ==
     * @param ideaRepository ==
     * @param commentService ==
     * @param userRepository ==
     * @param commentRepository ==
     */
    public StatisticsServiceImpl(ModelMapper modelMapper,
                                 IdeaService ideaService,
                                 IdeaRepository ideaRepository,
                                 CommentServiceImpl commentService, UserRepository userRepository, CommentRepository commentRepository, HtmlServiceImpl htmlService) {
        this.modelMapper = modelMapper;
        this.ideaService = ideaService;
        this.ideaRepository = ideaRepository;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.htmlService = htmlService;
    }

    @Override
    public Long getSelectionRepliesNumber(String selectedDateFrom, String selectedDateTo) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDateTo, formatter);
        LocalDate updatedDate = date.plusDays(1);
        String selectedDateToAux = updatedDate.format(formatter);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = cb.createQuery(Comment.class);
        Root<Comment> root = criteriaQuery.from(Comment.class);

        List<Predicate> predicatesList = new ArrayList<>();

        predicatesList.addAll(ideaService.filterByDate(selectedDateFrom, selectedDateToAux, root, cb, "creationDate"));
        predicatesList.add(cb.isNotNull(root.get("parent")));

        criteriaQuery.where(predicatesList.toArray(new Predicate[0]));
        TypedQuery<Comment> repliesQuery = entityManager.createQuery(criteriaQuery);

        Long allReplies = (long) repliesQuery.getResultList().size();

        return allReplies;
    }

    @Override
    public Long getSelectionCommentNumber(String selectedDateFrom, String selectedDateTo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDateTo, formatter);
        LocalDate updatedDate = date.plusDays(1);
        String selectedDateToAux = updatedDate.format(formatter);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = cb.createQuery(Comment.class);

        //represents the entity from which the query starts (Comment)
        Root<Comment> root = criteriaQuery.from(Comment.class);

        // conditions used to filter the data
        List<Predicate> predicatesList = new ArrayList<>();

        // used to incorporate multiple filtering conditions into the predicatesList
        predicatesList.addAll(ideaService.filterByDate(selectedDateFrom, selectedDateToAux, root, cb, "creationDate"));
        predicatesList.add(cb.isNull(root.get("parent")));

        criteriaQuery.where(predicatesList.toArray(new Predicate[0]));

        //a JPA query that returns typed results (a list of Comment)
        TypedQuery<Comment> commentsQuery = entityManager.createQuery(criteriaQuery);

        Long allComments = (long) commentsQuery.getResultList().size();

        return allComments;
    }


    /**
     * we use this function to retrieve the most commented ideas in order to
     * do statistics on them and to send them to be displayed
     *
     * @param mostCommentedIdeasIds list of idea id
     * @return list of most commented ideas
     */
    public List<IdeaResponseDTO> getMostCommentedIdeas(List<Long> mostCommentedIdeasIds) {

        List<IdeaResponseDTO> sortedIdeas = mostCommentedIdeasIds.stream()
                .map(idea_id -> {
                    Idea idea = ideaRepository.findById(idea_id).get();
                    IdeaResponseDTO ideaResponseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    String htmlContent1 = htmlService.markdownToHtml(idea.getTitle());
                    ideaResponseDTO.setTitle(htmlContent1);
                    ideaResponseDTO.setUsername(idea.getUser().getUsername());
                    String htmlContent2 = htmlService.markdownToHtml(idea.getText());
                    ideaResponseDTO.setText(htmlContent2);
                    ideaResponseDTO.setElapsedTime(commentService.getElapsedTime(idea.getCreationDate()));
                    ideaResponseDTO.setCommentsNumber(idea.getCommentList().size());
                    return ideaResponseDTO;
                })
                .toList();

        return sortedIdeas;
    }

    @Override
    public StatisticsDTO getGeneralStatistics() {

        StatisticsDTO statisticsDTO = new StatisticsDTO();

        Long nrOfUsers = userRepository.count();
        Long nrOfIdeas = ideaRepository.count();
        Long implIdeas = ideaRepository.countByStatus(Status.IMPLEMENTED);
        Long draftedIdeas = ideaRepository.countByStatus(Status.DRAFT);

        double ideasPerUser = Math.round((double) nrOfIdeas / (double) nrOfUsers * 100.00) / 100.00;

        statisticsDTO.setIdeasPerUser(ideasPerUser);
        statisticsDTO.setDraftIdeas(draftedIdeas);

        Long openIdeas = nrOfIdeas - implIdeas - draftedIdeas;
        statisticsDTO.setOpenIdeas(openIdeas);

        Long nrOfComments = commentRepository.countComments();
        statisticsDTO.setTotalNrOfComments(nrOfComments);

        Long nrOfReplies = commentRepository.countReplies();
        statisticsDTO.setTotalNrOfReplies(nrOfReplies);

        double draftPercentage = ((double) draftedIdeas / (double) nrOfIdeas * 100);
        double openPercentage = ((double) openIdeas / (double) nrOfIdeas * 100);
        double implPercentage = ((double) implIdeas / (double) nrOfIdeas * 100);

        // we calculate difference in case the sum is not 100%
        double totalP = (int) draftPercentage + (int) openPercentage + (int) implPercentage;
        double diff = 100.00 - totalP;
        draftPercentage = draftPercentage + diff;

        List<IdeaResponseDTO> mostCommentedIdeas = getMostCommentedIdeas(commentRepository.mostCommentedIdeas());


        List<Comment> topComments = commentRepository.findTop5CommentsByLikes();
        List<CommentStatisticsDTO> commentStatisticsDTOList = topComments.stream()
                .map(comment -> {
                    CommentStatisticsDTO dto = new CommentStatisticsDTO();
                    dto.setCommentId(comment.getId());
                    String htmlContent = htmlService.markdownToHtml(comment.getCommentText());
                    dto.setCommentText(htmlContent);
                    dto.setNrLikes(commentRepository.countLikesByCommentId(comment.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        statisticsDTO.setMostCommentedIdeas(mostCommentedIdeas);
        statisticsDTO.setNrOfUsers(nrOfUsers);
        statisticsDTO.setNrOfIdeas(nrOfIdeas);
        statisticsDTO.setImplementedIdeas(implIdeas);
        statisticsDTO.setImplP(implPercentage);
        statisticsDTO.setDraftP(draftPercentage);
        statisticsDTO.setOpenP(openPercentage);
        statisticsDTO.setMostLikedComments(commentStatisticsDTOList);

        return statisticsDTO;
    }

    @Override
    public StatisticsDTO getStatisticsByDate(String selectedDateFrom,
                                             String selectedDateTo) {

        // This code checks if the penultimate character of the string is '-' and if the last character is a digit.
        // This is because the date may be transmitted in the format "2024-08-5" and needs to be standardized to "2024-08-05"
        // when filtering statistics. The condition ensures that a '0' is added before the last digit if it's preceded by a '-'.
        // This helps maintain consistency in date formatting for statistical analysis.
        if (selectedDateTo.length() > 1 && selectedDateTo.charAt(selectedDateTo.length() - 2) == '-') {
            if (Character.isDigit(selectedDateTo.charAt(selectedDateTo.length() - 1))) {
                selectedDateTo = selectedDateTo.substring(0, selectedDateTo.length() - 1)
                        + '0'
                        + selectedDateTo.charAt(selectedDateTo.length() - 1);
            }
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(selectedDateTo, formatter);
        LocalDate updatedDate = date.plusDays(1);
        String selectedDateToAux = updatedDate.format(formatter);


        System.out.println(selectedDateToAux);
        StatisticsDTO filteredStatisticsDTO = new StatisticsDTO();

        List<Long> listOfRepliesAndComments = commentRepository.getRepliesAndCommentsCount(selectedDateFrom, selectedDateToAux);

        Long noOfReplies;
        try {
            noOfReplies = listOfRepliesAndComments.get(1);
        } catch (Exception e) {
            noOfReplies = 0L;
        }
        filteredStatisticsDTO.setTotalNrOfReplies(noOfReplies);

        Long noOfComments;
        try {
            noOfComments = listOfRepliesAndComments.get(0);
        } catch (Exception e) {
            noOfComments = 0L;
        }
        filteredStatisticsDTO.setTotalNrOfComments(noOfComments);

        List<Long> listOfStatusesCount = ideaRepository.countStatusByDate(selectedDateFrom,
                selectedDateToAux);

        Long openIdeasCount;
        Long draftIdeasCount;
        Long implIdeasCount;

        try {
            openIdeasCount = listOfStatusesCount.get(0);
        } catch (Exception e) {
            openIdeasCount = 0L;
        }

        try {
            draftIdeasCount = listOfStatusesCount.get(1);
        } catch (Exception e) {
            draftIdeasCount = 0L;
        }

        try {
            implIdeasCount = listOfStatusesCount.get(2);
        } catch (Exception e) {
            implIdeasCount = 0L;
        }

        Long totalIdeasCount = openIdeasCount + draftIdeasCount + implIdeasCount;

        double draftPercentage = ((double) draftIdeasCount / (double) totalIdeasCount * 100);
        double openPercentage = ((double) openIdeasCount / (double) totalIdeasCount * 100);
        double implPercentage = ((double) implIdeasCount / (double) totalIdeasCount * 100);
        double totalP = (int) draftPercentage + (int) openPercentage + (int) implPercentage;
        double diff = 100.00 - totalP;
        draftPercentage = draftPercentage + diff;

        List<IdeaResponseDTO> mostCommentedIdeas = getMostCommentedIdeas(
                commentRepository.mostCommentedIdeasIdsByDate(selectedDateFrom, selectedDateToAux));

        filteredStatisticsDTO.setImplP(implPercentage);
        filteredStatisticsDTO.setOpenP(openPercentage);
        filteredStatisticsDTO.setDraftP(draftPercentage);
        filteredStatisticsDTO.setNrOfIdeas(totalIdeasCount);
        filteredStatisticsDTO.setDraftIdeas(draftIdeasCount);
        filteredStatisticsDTO.setOpenIdeas(openIdeasCount);
        filteredStatisticsDTO.setImplementedIdeas(implIdeasCount);
        filteredStatisticsDTO.setTotalNrOfComments(noOfComments);
        filteredStatisticsDTO.setTotalNrOfReplies(noOfReplies);
        filteredStatisticsDTO.setMostCommentedIdeas(mostCommentedIdeas);

        return filteredStatisticsDTO;
    }

}
