package com.atoss.idea.management.system.utils;

import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class InitialDataLoader implements CommandLineRunner {
    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final IdeaRepository ideaRepository;
    private final CommentRepository commentRepository;


    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlValue;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;

    public InitialDataLoader(AvatarRepository avatarRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             ImageRepository imageRepository,
                             CommentRepository commentRepository,
                             IdeaRepository ideaRepository) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
    }


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        if (ddlValue.equals("create")) {
            String[] avatarFileNames = {
                "avatar1.svg",
                "avatar2.svg",
                "avatar3.svg",
                "avatar4.svg",
                "avatar5.svg",
                "avatar6.svg",
                "avatar7.svg"
            };
            ArrayList<Avatar> avatarList = new ArrayList<>();

            int count = 0;
            String avatarFilePath = "image/avatar/";
            for (String fileName : avatarFileNames) {
                System.out.println(avatarFilePath + fileName);
                String filePath = classLoader.getResource(avatarFilePath + fileName).getPath();
                Avatar avatar = avatarRepository.save(createAvatar(filePath, fileName));
                avatarList.add(avatar);
            }

            Avatar avatar1 = avatarList.get(0);

            String emailPrefix = "standarduser";
            String emailDomain = "@gmail.com";
            String usernamePrefix = "Standard";
            String namePrefix = "User Standard";
            String password = BCrypt.hashpw("StandardUser", bcryptSalt);


            ArrayList<User> userList = new ArrayList<>();
            for (int j = 1; j < 10; j++) {
                String email = emailPrefix + String.format("%02d", j) + emailDomain;
                String username = usernamePrefix + String.format("%02d", j);
                String name = namePrefix + " " + j;

                User user = createUser(true, Role.STANDARD, avatar1, email, username, name, password, true);
                userList.add(user);
                userRepository.save(user);
            }


            User user1 = createUser(true, Role.ADMIN, avatar1, "ap6548088@gmail.com", "Adrian22",
                    "Adrian Popescu", BCrypt.hashpw("AtossAdmin123", bcryptSalt), true);
            userRepository.save(user1);

            User user2 = createUser(true, Role.STANDARD, avatar1, "anaburlacu020626@gmail.com", "AnaS26",
                    "Ana Burlacu", BCrypt.hashpw("StandardUser", bcryptSalt), true);
            userRepository.save(user2);

            User user3 = createUser(true, Role.ADMIN, avatar1, "aA12332111114@gmail.com", "Ale009",
                    "Alexandra Moise", BCrypt.hashpw("AleAdmin2676", bcryptSalt), true);
            userRepository.save(user3);

            User user4 = createUser(false, null, null, "andreiuser973@gmail.com", "Andrei09888",
                    null, null, false);
            userRepository.save(user4);

            User user5 = createUser(false, null, null, "usercristian91@gmail.com", "Cosmin4455",
                    null, null, false);
            userRepository.save(user5);

            Category category1 = createCategory("Innovation");
            categoryRepository.save(category1);

            Category category2 = createCategory("Sport");
            categoryRepository.save(category2);

            Category category3 = createCategory("Nature");
            categoryRepository.save(category3);

            Image image1 = imageRepository.save(
                    createImage("img", ".png", classLoader.getResource("image/idea/img.png").getPath()));
            imageRepository.save(image1);

            Image image2 = imageRepository.save(
                    createImage("img3", ".png", classLoader.getResource("image/idea/img3.png").getPath()));
            imageRepository.save(image2);

            List<Category> categories = new ArrayList<>();
            categories.add(category1);
            categories.add(category2);
            categories.add(category3);
            List<Category> categories1 = new ArrayList<>();
            categories1.add(category2);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String staticDateString = "2023-07-26 12:00:00";
            String staticDateString2 = "2023-06-26 12:00:00";
            Date staticDate;
            Date staticDate2;

            try {
                staticDate = sdf.parse(staticDateString);
                staticDate2 = sdf.parse(staticDateString2);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            Idea idea1 = createIdea(user1, Status.OPEN, "Having a clear set of values for your "
                            + "company is another effective way to improve morale and provide guidance to staff. "
                            + "Creating and publishing your company values and morals provides a clear guideline "
                            + "for what the company stands for and is working towards. This can provide guidance "
                            + "for staff on the preferred course of action when faced with a decision during work, "
                            + "and also may provide a positive outlook on what the work they contribute to the company "
                            + "builds toward.", "Create clear company values", image1, staticDate, categories1);

            Idea idea2 = createIdea(user1, Status.OPEN, "Communication within an organization is often one of the most"
                            + " important elements of successful work. Providing staff with both the physical methods "
                            + "of communicating and a company culture that encourages communication can help staff do "
                            + "more efficiently find answers to any questions they have. This can help to increase employees'"
                            + " productive hours.", "Establish lines of communication", image2, staticDate2, categories);

            Idea idea3 = createIdea(user1, Status.IMPLEMENTED, "Creating fair standards for employee performance assessment "
                            + "within your organization can create a more fair and inclusive corporate culture. This can have two "
                            + "important benefits for improving your company. First, by applying standards in a fair and clearly "
                            + "described manner, you minimize the opportunity for employees to feel like a co-worker received benefits "
                            + "they did not. Setting fair standards also helps you to identify your higher-performing employees, who may "
                            + "receive enhanced responsibilities within the organization.", "Apply standards equally",
                    null, new Date(), categories);

            Idea idea4 = createIdea(user2, Status.IMPLEMENTED, "Changing your company's payment structure allows you to make more "
                            + "appealing offers to current and potential staff. Apart from allowing you to maintain your most essential "
                            + "employees by showing that you value their work, this can help you make higher-quality hires as well. "
                            + "Hiring more productive staff can compensate for the increased salary cost in the form of increased profit generation.",
                    "Raise compensation to raise employee quality", null, new Date(), categories);

            ideaRepository.save(idea1);
            ideaRepository.save(idea2);
            ideaRepository.save(idea3);
            ideaRepository.save(idea4);


            List<Idea> ideaList = new ArrayList<>();

            Status status = Status.OPEN;
            String text = "Test";
            Date date = new Date();

            int k = 1;
            for (User user : userList) {
                String title = "New Idea " + k;
                Idea idea = createIdea(user, status, text, title, null, date, categories);
                ideaList.add(idea);
                ideaRepository.save(idea);
                k++;
            }


            Comment comment1 = createComment(staticDate, idea1, null,
                    user1, "It's unnecessary");
            commentRepository.save(comment1);

            Comment comment2 = createComment(staticDate2, idea2, null,
                    user2, "It's a great idea");
            commentRepository.save(comment2);
            ArrayList<Comment> commentList = new ArrayList<>();
            commentList.add(comment1);
            commentList.add(comment2);

            Comment reply1 = createReply(new Date(), comment2, idea2, user1, "I don't think so");
            commentRepository.save(reply1);
            List<Category> categoryList = new ArrayList<>();

            int n = 0;
            int numberOfCommentsPerIdea = 4;
            for (Idea idea : ideaList) {
                for (int i = 0; i < numberOfCommentsPerIdea; i++) {
                    String commentText = "Comment " + n + " for: " + idea.getTitle();
                    Comment comment = createComment(date, idea, null, user1, commentText);
                    commentRepository.save(comment);
                    commentList.add(comment);
                    n++;
                }
            }

            int m = 0;
            int numberOfRepliesPerComment = 2;
            for (Comment comment : commentList) {
                for (int i = 0; i < numberOfRepliesPerComment; i++) {
                    String replyText = "Reply " + m + " for: " + comment.getCommentText();
                    Comment reply = createReply(date, comment1, idea2, user2, replyText);
                    commentRepository.save(reply);
                    m++;
                }
            }
        }
    }

    private static User createUser(Boolean isActive, Role role, Avatar avatar,
                                   String email, String username, String fullName,
                                   String hashPassword, Boolean hasPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setIsActive(isActive);
        user.setRole(role);
        user.setHasPassword(hasPassword);
        user.setAvatar(avatar);
        user.setRole(role);
        return user;
    }

    private static Category createCategory(
            String text
    ) {
        Category category = new Category();
        category.setText(text);
        return category;
    }



    private static Avatar createAvatar(String filePath, String fileName
    ) throws IOException {
        Avatar avatar = new Avatar();
        File file = new File(filePath);
        byte[] imageByte = Files.readAllBytes(file.toPath());
        avatar.setData(imageByte);
        avatar.setFileName(fileName);

        return avatar;
    }


    private static Image createImage(String fileName,
                                     String fileType, String filePath
    ) throws IOException {
        Image image = new Image();
        File file = new File(filePath);
        byte[] imageByte = Files.readAllBytes(file.toPath());
        image.setImage(imageByte);
        image.setFileName(fileName);
        image.setFileType(fileType);
        return image;
    }

    private static Idea createIdea(User user, Status status, String text,
                                   String title, Image image, Date date,
                                   List<Category> categories
    ) {
        Idea idea = new Idea();
        idea.setUser(user);
        idea.setStatus(status);
        idea.setText(text);
        idea.setTitle(title);
        idea.setImage(image);
        idea.setCreationDate(date);
        idea.setCategoryList(categories);
        return idea;
    }


    private static Comment createReply(Date date, Comment commentId, Idea ideaId,
                                       User user, String text) {
        Comment comment = new Comment();
        comment.setCreationDate(date);
        comment.setIdea(ideaId);
        comment.setParent(commentId);
        comment.setUser(user);
        comment.setCommentText(text);
        return comment;
    }

    private static Comment createComment(Date date, Idea ideaId,
                                         Comment parentId, User user, String text

    ) {
        Comment comment = new Comment();
        comment.setCreationDate(date);
        comment.setIdea(ideaId);
        comment.setParent(parentId);
        comment.setUser(user);
        comment.setCommentText(text);
        return comment;
    }

}