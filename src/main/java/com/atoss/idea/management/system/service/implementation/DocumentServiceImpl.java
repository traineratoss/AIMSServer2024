package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.DocumentNotFoundException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.DocumentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.DocumentDTO;
import com.atoss.idea.management.system.repository.entity.Document;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.DocumentService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final IdeaRepository ideaRepository;

    /**
     * Constructor
     *
     * @param documentRepository accessing CRUD Repository for Document Entity
     * @param modelMapper mapping Entity-DTO relationship
     * @param userRepository repository of the user entity
     * @param ideaRepository repository idea entity
     */
    public DocumentServiceImpl(DocumentRepository documentRepository,
                               ModelMapper modelMapper,
                               UserRepository userRepository,
                               IdeaRepository ideaRepository) {
        this.documentRepository = documentRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
    }

    @Override
    public DocumentDTO addDocument(MultipartFile file, Long ideaId, Long userId) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Document document = new Document(fileName, file.getContentType(), file.getBytes());
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        document.setUser(user);
        Idea idea = ideaRepository.findById(ideaId).orElseThrow(() -> new IdeaNotFoundException("Idea not found"));
//        Long id1 = IdeaServiceImpl.ideaResponseId;
//        System.out.println(id1 + " aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        Idea idea = ideaRepository.findById(id1).orElseThrow(() -> new IdeaNotFoundException("Idea not found."));
        document.setIdea(idea);
        return modelMapper.map(documentRepository.save(document), DocumentDTO.class);
    }

    @Transactional
    @Override
    public DocumentDTO getDocument(Long id) throws DocumentNotFoundException {
        if (documentRepository.findById(id).isPresent()) {
            return modelMapper.map(documentRepository.findById(id).get(), DocumentDTO.class);
        } else {
<<<<<<< HEAD
            throw new UserNotFoundException("user");
=======
            throw new DocumentNotFoundException("Document does not exist");
>>>>>>> d0b03483c62ab70986ef7b1953b0208a1e67a39b
        }
    }


    @Override
    public List<DocumentDTO> getDocumentsByIdeaId(Long ideaId) {
        //return Arrays.asList(modelMapper.map(documentRepository.findByIdeaId(id), Document.class));
        List<Document> documents = documentRepository.findDocumentsByIdeaId(ideaId);
        List<DocumentDTO> documentDTOs = documents.stream()
                .map(document -> {
                    DocumentDTO responseDTO = modelMapper.map(document, DocumentDTO.class);
                    responseDTO.setIdeaId(document.getIdea().getId());
                    responseDTO.setUserId(document.getUser().getId());
                    return responseDTO;
                })
                .toList();
        return documentDTOs;
    }

    @Override
    public void removeDocument(Long ideaId, Long userId) {
        Optional<Document> document = documentRepository.findByIdeaIdAndUserId(ideaId, userId);
        if (document.isPresent()) {
            documentRepository.deleteByIdeaIdAndUserId(ideaId, userId);
        } else {
            throw new DocumentNotFoundException("Document does not exist");
        }
    }
}