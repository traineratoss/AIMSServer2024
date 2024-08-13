package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.DocumentNotFoundException;
import com.atoss.idea.management.system.repository.DocumentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.SubscriptionRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import com.atoss.idea.management.system.repository.entity.Document;
import com.atoss.idea.management.system.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Log4j2
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final IdeaRepository ideaRepository;

    private final SendEmailServiceImpl sendEmailService;

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Constructor
     *
     * @param documentRepository     accessing CRUD Repository for Document Entity
     * @param modelMapper            mapping Entity-DTO relationship
     * @param userRepository         repository of the user entity
     * @param ideaRepository         repository idea entity
     * @param sendEmailService       service used for sending emails
     * @param subscriptionRepository repository of subscription entity
     */
    public DocumentServiceImpl(DocumentRepository documentRepository,
                               ModelMapper modelMapper,
                               UserRepository userRepository,
                               IdeaRepository ideaRepository,
                               SendEmailServiceImpl sendEmailService,
                               SubscriptionRepository subscriptionRepository) {
        this.documentRepository = documentRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
        this.sendEmailService = sendEmailService;
        this.subscriptionRepository = subscriptionRepository;
    }

    //    @Override
    //    public List<DocumentDTO> addDocument(MultipartFile[] files, Long ideaId, Long userId) throws IOException {
    //
    //        List<Document> documents = new ArrayList<>();
    //        for (MultipartFile file : files) {
    //            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    //            Document document = new Document(fileName, file.getContentType(), file.getBytes());
    //            User user = userRepository.findById(userId).orElseThrow(() -> {
    //                if (log.isErrorEnabled()) {
    //                    log.error("User with id {} not found", userId);
    //                }
    //                return new UserNotFoundException("User not found.");
    //            });
    //            document.setUser(user);
    //            Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> {
    //                if (log.isErrorEnabled()) {
    //                    log.error("Idea with id {} not found", ideaId);
    //                }
    //                return new IdeaNotFoundException("Idea not found");
    //            });
    //            document.setIdea(idea);
    //            documents.add(document);
    //            documentRepository.save(document);
    //
    //            log.info("Document {} successfully saved", fileName);
    //
    //        }
    //        List<Long> subscribedUsersIds = subscriptionRepository.findUserIdByIdeaId(ideaId);
    //        List<User> subscribedUsers = new ArrayList<>();
    //        for (Long users : subscribedUsersIds) {
    //            subscribedUsers.add(userRepository.findById(users).get());
    //        }
    //        if (!subscribedUsers.isEmpty()) {
    //            if (log.isInfoEnabled()) {
    //                log.info("Sending email notifications to {} subscribed users about new documents", subscribedUsers.size());
    //            }
    //            sendEmailService.sendEmailIdeaDocuments(subscribedUsers, ideaId, documents);
    //        }
    //        List<DocumentDTO> documentDTOs = documents.stream()
    //                .map(document -> modelMapper.map(document, DocumentDTO.class))
    //                .toList();
    //        if (log.isInfoEnabled()) {
    //            log.info("Successfully processed and mapped {} documents to DTOs", documentDTOs.size());
    //        }
    //
    //        return documentDTOs;
    //
    //    }

    @Transactional
    @Override
    public DocumentDTO getDocument(Long id) throws DocumentNotFoundException {
        if (documentRepository.findById(id).isPresent()) {

            Document document = documentRepository.findById(id).get();
            if (log.isInfoEnabled()) {
                log.info("Document successfully retrieved");
            }
            return modelMapper.map(document, DocumentDTO.class);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Document does not exist");
            }
            throw new DocumentNotFoundException("Document does not exist");
        }
    }


    @Override
    public List<DocumentDTO> getDocumentsByIdeaId(Long ideaId) {
        List<Document> documents = documentRepository.findDocumentsByIdeaId(ideaId);

        if (documents.isEmpty() && log.isWarnEnabled()) {
            log.warn("No documents found for idea");
        }

        List<DocumentDTO> documentDTOs = documents.stream()
                .map(document -> {
                    DocumentDTO responseDTO = modelMapper.map(document, DocumentDTO.class);
                    responseDTO.setIdeaId(document.getIdea().getId());
                    responseDTO.setUserId(document.getUser().getId());
                    return responseDTO;
                })
                .toList();
        if (log.isInfoEnabled()) {
            log.info("Retrieved {} documents", documentDTOs.size());
        }

        return documentDTOs;
    }

    @Override
    public void deleteDocumentById(Long id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            documentRepository.deleteById(id);
            if (log.isInfoEnabled()) {
                log.info("Document was successfully deleted");
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("Document was not found");
                }
            }
        }
    }
}