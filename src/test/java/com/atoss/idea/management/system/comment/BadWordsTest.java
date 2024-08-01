package com.atoss.idea.management.system.comment;

import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import com.atoss.idea.management.system.service.implementation.HtmlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

public class BadWordsTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IdeaRepository ideaRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private HtmlServiceImpl htmlService;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setup()
    {
        MockitoAnnotations.openMocks(this);
        commentService=new CommentServiceImpl(commentRepository,ideaRepository,userRepository,modelMapper,htmlService);
    }
    /*
    @Test
    public void testReadBadWordsFromFile() {
        String path = "C:\\Users\\tarisadm\\Desktop\\AIMSServer2024\\src\\main\\resources\\textTerms\\badWords.txt";



        commentService.readBadWordsFromFile(path);

        assertTrue(commentService.getbadWords().contains("unfriendly"));
        assertTrue(commentService.getbadWords().contains("bbw"));
        assertTrue(commentService.getbadWords().contains("bdsm"));

        assertFalse(commentService.getbadWords().contains("friendly"));
        assertFalse(commentService.getbadWords().contains("USA"));
        assertFalse(commentService.getbadWords().contains("Romania"));

    }

    @Test
    public void testFilterBadWords() {
        String path = "C:\\Users\\tarisadm\\Desktop\\AIMSServer2024\\src\\main\\resources\\textTerms\\badWords.txt";
        commentService.readBadWordsFromFile(path);

        String input = "This is a bbw and bdsm";
        String expected = "This is a *** and ****";
        String actual = commentService.filterBadWords(input);

        assertEquals(expected, actual);
    } */



}