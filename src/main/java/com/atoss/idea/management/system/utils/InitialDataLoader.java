package com.atoss.idea.management.system.utils;

import com.atoss.idea.management.system.repository.*;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.SecureRandom;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Component
public class InitialDataLoader implements CommandLineRunner {
    private static final SecureRandom random = new SecureRandom();

    private final AvatarRepository avatarRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final IdeaRepository ideaRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final IdeaServiceImpl ideaService;


    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlValue;

    @Value("${aims.app.bcrypt.salt}")
    private String bcryptSalt;

    /**
     * CONSTRUCTOR - save entity in repository
     *
     * @param avatarRepository   for saving a new entity of Avatar
     * @param userRepository     for saving a new entity of User
     * @param categoryRepository for saving a new entity of Category
     * @param imageRepository    for saving a new entity of Image
     * @param commentRepository  for saving a new entity of Comment
     * @param ideaRepository     for saving a new entity of Idea
     * @param ratingRepository   for saving the rating for entity of Idea
     * @param ideaService        for saving a new entity of Idea
     */

    public InitialDataLoader(AvatarRepository avatarRepository,
                             UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             ImageRepository imageRepository,
                             CommentRepository commentRepository,
                             IdeaRepository ideaRepository,
                             RatingRepository ratingRepository,
                             IdeaServiceImpl ideaService) {
        this.avatarRepository = avatarRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
        this.ratingRepository = ratingRepository;
        this.ideaService = ideaService;
    }


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // so that we don't have to insert manually the absolute path
        ClassLoader classLoader = getClass().getClassLoader();

        // Avatar CONSTRUCTOR
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

            String avatarFilePath = "image/avatar/";
            for (String fileName : avatarFileNames) {
                URL resourceUrl = classLoader.getResource(avatarFilePath + fileName);
                if (resourceUrl != null) {
                    try {
                        String filePath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");
                        Avatar avatar = avatarRepository.save(createAvatar(filePath, fileName));
                        avatarList.add(avatar);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            Avatar avatar1 = avatarList.get(0);


            //User CONSTRUCTOR
            ArrayList<User> userList = new ArrayList<>();

            String password = BCrypt.hashpw("StandardUser", bcryptSalt);

            User user1 = createUser(true, Role.ADMIN, avatar1, "ap6548088@gmail.com", "adrian22",
                    "Adrian Popescu", BCrypt.hashpw("AtossAdmin123", bcryptSalt), true, false);
            userRepository.save(user1);

            User user2 = createUser(true, Role.STANDARD, avatar1, "userlorena7@gmail.com", "lorena16",
                    "Ana Burlacu", BCrypt.hashpw("StandardUser", bcryptSalt), true, false);
            userRepository.save(user2);

            User user3 = createUser(true, Role.ADMIN, avatar1, "aA12332111114@gmail.com", "ale009",
                    "Alexandra Moise", BCrypt.hashpw("AleAdmin2676", bcryptSalt), true, false);
            userRepository.save(user3);

            User user4 = createUser(false, null, null, "andreiuser973@gmail.com", "andrei09888",
                    null, null, false, true);
            userRepository.save(user4);

            User user5 = createUser(false, null, null, "usercristian91@gmail.com", "cosmin4455",
                    null, null, false, true);
            userRepository.save(user5);

            User user6 = createUser(true, Role.STANDARD, avatar1, "anadan@yahoo.com", "anadan",
                    "Ana Dan", BCrypt.hashpw("Ana4455@", bcryptSalt), true, false);
            userRepository.save(user6);

            User user7 = createUser(true, Role.STANDARD, avatar1, "clupascu003@gmail.com", "callinu",
                    "Calin Lupascu", BCrypt.hashpw("calinlupascu", bcryptSalt), true, false);
            userRepository.save(user7);

            User user8 = createUser(true, Role.STANDARD, avatar1, "alex1234@gmail.com", "alex",
                    "Alex Pop", BCrypt.hashpw("alexpop", bcryptSalt), true, false);
            userRepository.save(user8);

            User user9 = createUser(true, Role.STANDARD, avatar1, "ioana12@gmail.com", "ioana",
                    "Ioana Popescu", BCrypt.hashpw("popescu", bcryptSalt), true, false);
            userRepository.save(user9);

            User user10 = createUser(true, Role.STANDARD, avatar1, "andreea@gmail.com", "andreea",
                    "Andreea Duma", BCrypt.hashpw("andreaduma", bcryptSalt), true, false);
            userRepository.save(user10);

            User user11 = createUser(true, Role.STANDARD, avatar1, "matei@gmail.com", "matei",
                    "Matei Ion", BCrypt.hashpw("matei", bcryptSalt), true, false);
            userRepository.save(user11);

            User user12 = createUser(true, Role.ADMIN, avatar1, "biancarusu181@gmail.com", "biancarusu",
                    "Bianca Rusu", BCrypt.hashpw("AdminBianca12", bcryptSalt), true, false);
            userRepository.save(user12);

            //Category CONSTRUCTOR
            Category category1 = createCategory("Innovation");
            categoryRepository.save(category1);

            Category category2 = createCategory("Sport");
            categoryRepository.save(category2);

            Category category3 = createCategory("Nature");
            categoryRepository.save(category3);

            List<Category> categories = new ArrayList<>();
            categories.add(category1);
            categories.add(category2);
            categories.add(category3);
            List<Category> categories1 = new ArrayList<>();
            categories1.add(category2);

            ArrayList<ArrayList<Category>> categoryMatrix = new ArrayList<>();
            categoryMatrix.add(new ArrayList<>());
            categoryMatrix.add(new ArrayList<>());
            categoryMatrix.add(new ArrayList<>());

            categoryMatrix.get(0).add(category1);
            categoryMatrix.get(1).add(category3);
            categoryMatrix.get(2).add(category2);

            //Image CONSTRUCTOR
            String[] imageFileNames = {
                "img.png",
                "img2.png",
                "img3.png",
                "img4.png",
                "img5.png",
                "img6.png",
                "img7.png"
            };

            ArrayList<Image> imageList = new ArrayList<>();
            String imageFilePath = "image/idea/";
            for (String fileName : imageFileNames) {
                URL resourceUrl = classLoader.getResource(imageFilePath + fileName);
                if (resourceUrl != null) {
                    try {
                        String filePath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");
                        Image image = imageRepository.save(createImage(filePath, fileName, filePath));
                        imageList.add(image);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            final Image image1 = imageList.get(0);
            final Image image2 = imageList.get(1);
            final Image image3 = imageList.get(2);
            final Image image4 = imageList.get(3);
            final Image image5 = imageList.get(4);
            final Image image6 = imageList.get(5);
            final Image image7 = imageList.get(6);

            //static date CONSTRUCTOR
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String staticDateString1 = "2023-07-26 12:00:00";
            String staticDateString2 = "2023-06-26 12:00:00";
            String staticDateString3 = "2024-03-27 17:50:00";
            String staticDateString4 = "2024-02-10 10:00:00";
            Date staticDate1;
            Date staticDate2;
            Date staticDate3;
            Date staticDate4;

            try {
                staticDate1 = sdf.parse(staticDateString1);
                staticDate2 = sdf.parse(staticDateString2);
                staticDate3 = sdf.parse(staticDateString3);
                staticDate4 = sdf.parse(staticDateString4);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            //Idea CONSTRUCTOR
            Idea idea1 = createIdea(user1, Status.OPEN, "Having a clear set of values for your "
                            + "company is another effective way to improve morale and provide guidance to staff. "
                            + "Creating and publishing your company values and morals provides a clear guideline "
                            + "for what the company stands for and is working towards.",
                    "Create **clear** company values", image1, staticDate1, categories1);

            Idea idea2 = createIdea(user1, Status.OPEN, "Communication within an organization is often one of the most"
                            + " important elements of ***successful work***. Providing staff with both the physical methods "
                            + "of communicating and a company culture that encourages communication can help staff do "
                            + "more efficiently find answers to any questions they have. ",
                    "Establish lines of communication", image2, staticDate2, categories);

            Idea idea3 = createIdea(user1, Status.IMPLEMENTED, "Creating **fair** standards for employee performance assessment "
                            + "within your organization can create a more fair and inclusive corporate culture. This can have two "
                            + "important benefits for improving your company. First, by applying standards in a fair and clearly "
                            + "described manner, you minimize the opportunity for employees to feel like a co-worker received benefits "
                            + "they did not.",
                    "Apply standards equally", image3, staticDate1, categories);

            Idea idea4 = createIdea(user2, Status.IMPLEMENTED, "Changing your company's payment structure allows you to make more "
                            + "appealing offers to current and potential staff. Apart from allowing you to maintain your most essential "
                            + "employees by showing that you value their work, this can help you make higher-quality hires as well. "
                            + "Hiring more productive staff can compensate for the increased salary cost in the form of increased profit generation.",
                    "Raise compensation to raise employee quality", image4, new Date(), categories);

            Idea idea5 = createIdea(user3, Status.OPEN, "Offering professional development **opportunities** for employees can significantly "
                            + "increase both their job satisfaction and their productivity. By investing in your employees' skills and knowledge, "
                            + "you also invest in the company's overall performance.",
                    "Promote *professional* development", image5, staticDate4, categories1);

            Idea idea6 = createIdea(user2, Status.IMPLEMENTED, "Flexible working hours can ***improve*** work-life balance for employees, which "
                            + "in turn can enhance their overall well-being and productivity. Allowing employees to have a say in their work "
                            + "schedules can reduce stress and burnout, leading to a happier and more effective workforce.",
                    "Implement flexible work schedules", image6, staticDate3, categories);

            Idea idea7 = createIdea(user4, Status.OPEN, "Providing a clear career path within your company is essential for retaining talent. "
                            + "Employees are more likely to stay long-term if they see potential for growth and advancement. This not only "
                            + "motivates them to perform better but also creates a sense of loyalty to the company.",
                    "Define clear career progression paths", image7, new Date(), categories);

            Idea idea8 = createIdea(user3, Status.OPEN, "![Wellness](https://static.vecteezy.com/system/resources/previews/004/413/150/non_2x/simple"
                            + "-illustration-that-prevent-heart-attack-heart-health-cardiology-outlined-icon-free-vector.jpg)"
                            + "Introducing wellness programs in the workplace can have a positive impact on employee"
                            + "health and reduce absenteeism. Wellness initiatives like fitness challenges, mental health support, and "
                            + "healthy eating programs can lead to a more energized and engaged workforce.",
                    "Introduce wellness programs", image3, staticDate2, categories1);

            ideaRepository.save(idea1);
            ideaRepository.save(idea2);
            ideaRepository.save(idea3);
            ideaRepository.save(idea4);
            ideaRepository.save(idea5);
            ideaRepository.save(idea6);
            ideaRepository.save(idea7);
            ideaRepository.save(idea8);

            List<Idea> ideaList = new ArrayList<>();

            Rating rating1 = addRating(idea1, user1, 2d);
            ratingRepository.save(rating1);

            Rating rating2 = addRating(idea1, user2, 5d);
            ratingRepository.save(rating2);

            Rating rating3 = addRating(idea1, user3, 4d);
            ratingRepository.save(rating3);

            Rating rating4 = addRating(idea1, user4, 2d);
            ratingRepository.save(rating4);

            Rating rating5 = addRating(idea2, user1, 3d);
            ratingRepository.save(rating5);

            Rating rating6 = addRating(idea1, user5, 1d);
            ratingRepository.save(rating6);

            Rating rating7 = addRating(idea1, user6, 3d);
            ratingRepository.save(rating7);


            Rating rating8 = addRating(idea2, user2, 5d);
            ratingRepository.save(rating8);

            Rating rating9 = addRating(idea2, user1, 4d);
            ratingRepository.save(rating9);

            Rating rating10 = addRating(idea2, user4, 2d);
            ratingRepository.save(rating10);

            Rating rating11 = addRating(idea2, user5, 1d);
            ratingRepository.save(rating11);

            Rating rating12 = addRating(idea2, user6, 5d);
            ratingRepository.save(rating12);

            Rating rating13 = addRating(idea3, user1, 3d);
            ratingRepository.save(rating13);

            Rating rating14 = addRating(idea3, user2, 4d);
            ratingRepository.save(rating14);

            Rating rating15 = addRating(idea3, user3, 2d);
            ratingRepository.save(rating15);

            Rating rating16 = addRating(idea3, user4, 5d);
            ratingRepository.save(rating16);

            Rating rating17 = addRating(idea3, user5, 4d);
            ratingRepository.save(rating17);

            Rating rating18 = addRating(idea3, user6, 2d);
            ratingRepository.save(rating18);

            Rating rating19 = addRating(idea4, user1, 5d);
            ratingRepository.save(rating19);

            Rating rating20 = addRating(idea4, user2, 4d);
            ratingRepository.save(rating20);

            Rating rating21 = addRating(idea4, user3, 2d);
            ratingRepository.save(rating21);

            Rating rating22 = addRating(idea4, user4, 4d);
            ratingRepository.save(rating22);

            Rating rating23 = addRating(idea4, user5, 1d);
            ratingRepository.save(rating23);

            Rating rating24 = addRating(idea4, user6, 2d);
            ratingRepository.save(rating24);

            Rating rating25 = addRating(idea5, user1, 4d);
            ratingRepository.save(rating25);

            Rating rating26 = addRating(idea5, user2, 5d);
            ratingRepository.save(rating26);

            Rating rating27 = addRating(idea5, user3, 3d);
            ratingRepository.save(rating27);

            idea1.setRatingAvg(ideaService.getAverage(idea1.getId()));
            idea2.setRatingAvg(ideaService.getAverage(idea2.getId()));
            idea3.setRatingAvg(ideaService.getAverage(idea3.getId()));
            idea4.setRatingAvg(ideaService.getAverage(idea4.getId()));
            idea5.setRatingAvg(ideaService.getAverage(idea5.getId()));


            //Comment CONSTRUCTOR
            Comment comment1 = createComment(staticDate1, idea1, null,
                    user1, "It's unnecessary");
            commentRepository.save(comment1);

            Comment comment2 = createComment(staticDate2, idea2, null,
                    user2, "It's a great idea");
            commentRepository.save(comment2);

            Comment comment3 = createComment(staticDate4, idea2, null,
                    user3, "It's unnecessary");
            commentRepository.save(comment3);

            Comment comment4 = createComment(new Date(), idea3, null,
                    user4, "It's a ***great*** idea");
            commentRepository.save(comment4);

            Comment comment5 = createComment(staticDate4, idea1, null,
                    user4, "I think this might be unnecessary at the moment.");
            commentRepository.save(comment5);

            Comment comment6 = createComment(staticDate3, idea1, null,
                    user4, "This idea could greatly benefit our team.");
            commentRepository.save(comment6);

            Comment comment7 = createComment(new Date(), idea2, null,
                    user4, "I'm not sure if this aligns with our *current goals*.");
            commentRepository.save(comment7);

            Comment comment8 = createComment(staticDate4, idea5, null,
                    user2, "I think this might be unnecessary at the moment.");
            commentRepository.save(comment8);

            Comment comment9 = createComment(staticDate3, idea6, null,
                    user1, "This could really help our team");
            commentRepository.save(comment9);

            Comment comment10 = createComment(new Date(), idea7, null,
                    user3, "Could use more details");
            commentRepository.save(comment10);

            Comment comment11 = createComment(staticDate2, idea8, null,
                    user5, "Love this idea! ![Love](https://icons.iconarchive.com/icons/designbolts/free-valentine-heart/256/Heart-icon.png)");
            commentRepository.save(comment11);

            Comment comment12 = createComment(staticDate3, idea6, null,
                    user4, "This ***suggestion*** could use more specific examples.");
            commentRepository.save(comment12);

            Comment comment13 = createComment(new Date(), idea4, null,
                    user4, "I absolutely **love** this idea! It could make a big difference.");
            commentRepository.save(comment13);

            Comment comment14 = createComment(staticDate4, idea5, null,
                    user5, "I have some concerns about how this will be implemented.");
            commentRepository.save(comment14);


            Comment reply1 = createReply(staticDate3, comment2, idea2,
                    user1, "I don't think so");
            commentRepository.save(reply1);

            Comment reply2 = createReply(new Date(), comment3, idea2,
                    user3, "We should evaluate the resources and prioritize accordingly.");
            commentRepository.save(reply2);

            Comment reply3 = createReply(staticDate4, comment3, idea2,
                    user4, "I think it is");
            commentRepository.save(reply3);

            Comment reply4 = createReply(staticDate3, comment5, idea2,
                    user4, "I believe with the right planning, we can implement it effectively.");
            commentRepository.save(reply4);

            Comment reply5 = createReply(new Date(), comment5, idea2,
                    user4, "Good point, adding more details could strengthen the proposal.");
            commentRepository.save(reply5);

            Comment reply6 = createReply(new Date(), comment8, idea5,
                    user4, "I understand your concern, but I think it's essential for growth.");
            commentRepository.save(reply6);

            Comment reply7 = createReply(new Date(), comment9, idea6,
                    user2, "Absolutely, it could make a huge difference.");
            commentRepository.save(reply7);

            Comment reply8 = createReply(staticDate1, comment11, idea7,
                    user1, "Good point, adding more details could strengthen the proposal.");
            commentRepository.save(reply8);

            Comment reply9 = createReply(staticDate2, comment12, idea6,
                    user3, "I believe with the right planning, we can implement it *effectively*.");
            commentRepository.save(reply9);

            Comment reply10 = createReply(new Date(), comment13, idea6,
                    user2, "We should evaluate the resources and **prioritize** accordingly.");
            commentRepository.save(reply10);


            addLike(comment2, user1);
            addLike(comment2, user3);
            addLike(comment2, user4);
            addLike(comment2, user5);

            addLike(comment5, user4);
            addLike(comment7, user6);

            addLike(comment7, user1);
            addLike(comment7, user2);
            addLike(comment7, user4);
            addLike(comment7, user6);

            addLike(comment9, user3);
            addLike(comment9, user4);
            addLike(comment9, user5);

            addLike(comment10, user1);
            addLike(comment10, user2);
            addLike(comment10, user4);
            addLike(comment10, user5);
            addLike(comment10, user6);

            addLike(comment14, user1);
            addLike(comment14, user2);
            addLike(comment14, user6);


            addLike(reply1, user3);
            addLike(reply1, user6);

            addLike(reply3, user1);
            addLike(reply3, user2);
            addLike(reply3, user5);

            addLike(reply6, user2);
            addLike(reply6, user3);

            addLike(reply7, user1);
            addLike(reply7, user4);

            addLike(reply8, user1);
            addLike(reply8, user3);
            addLike(reply8, user5);

            addLike(reply10, user2);
            addLike(reply10, user3);
            addLike(reply10, user5);
            addLike(reply10, user6);


            addReport(reply8, user2);
            addReport(reply8, user6);

            addReport(comment14, user6);
            addReport(reply6, user2);

            //initialize CommentList for using it in another Constructor (for replies)
            ArrayList<Comment> commentList = new ArrayList<>();
            commentList.add(comment1);
            commentList.add(comment2);

            //dummy user
            String emailPrefix = "standarduser";
            String emailDomain = "@gmail.com";
            String usernamePrefix = "standard";
            String namePrefix = "User Standard";
            int numberOfUsers = 12;
            for (int j = 1; j < numberOfUsers; j++) {
                String email = emailPrefix + String.format("%02d", j) + emailDomain;
                String username = usernamePrefix + String.format("%02d", j);
                String name = namePrefix + " " + j;

                User user = createUser(true, Role.STANDARD, randomElementFromList(avatarList), email, username, name, password, true, false);
                userList.add(user);
                userRepository.save(user);

                addLike(comment3, user);

                if (j % 2 == 1) {
                    addLike(comment13, user);
                    addLike(comment11, user);

                    addReport(reply5, user);
                    addReport(comment11, user);

                }

                addReport(reply2, user);
                addReport(comment6, user);
                addReport(comment7, user);
            }

            // Dummy ideas
            int numberOfIdeasToCreate = 50;
            int k = 1;
            for (int j = 1; j <= numberOfIdeasToCreate; j++) {
                Date ideaDate = randomDateFromList(randomDateList(30));
                String title = "New Idea " + k;
                String text = "World changing idea number " + k + ". Waiting on opinions!";
                Idea idea = createIdea(randomElementFromList(userList), randomEnum(Status.class), text, title,
                        image5, randomDateFromList(randomDateList(30)), randomElementFromList(categoryMatrix));
                ideaList.add(idea);
                ideaRepository.save(idea);
                k++;
            }

            // Dummy comments and replies
            int n = 0;
            int m = 0;
            for (Idea idea : ideaList) {
                // Because we want a random number of comments per idea
                int numberOfCommentsPerIdea = givenList_shouldReturnARandomElement();
                for (int i = 0; i < numberOfCommentsPerIdea; i++) {
                    Date commentDate = randomDateAfter(idea.getCreationDate());
                    String commentText = "Comment " + n;
                    Comment comment = createComment(commentDate, idea, null,
                            randomElementFromList(userList), commentText);
                    commentRepository.save(comment);
                    commentList.add(comment);
                    n++;

                    // Dummy replies
                    // Because we want a random number of replies per comment
                    int numberOfRepliesPerComment = givenList_shouldReturnARandomElement();
                    for (int b = 0; b < numberOfRepliesPerComment; b++) {
                        String replyText = "Reply " + m;
                        Date replyDate = randomDateAfter(commentDate); // Generate reply date after parent comment date
                        Comment reply = createReply(replyDate, comment, idea,
                                randomElementFromList(userList), replyText);
                        commentRepository.save(reply);
                        m++;
                    }
                }
            }
            for (Idea idea : ideaList) {
                for (long i = 1; i < 19; i++) {
                    double rating = Math.floor(Math.random() * 5) + 1;
                    Rating ratingT = addRating(idea, userRepository.findById(i).get(), rating);
                    ratingRepository.save(ratingT);
                    idea.setRatingAvg(ideaService.getAverage(idea.getId()));
                }
            }
        }
    }

    private void addLike(Comment comment, User user) {
        user.getLikedComments().add(comment);
        comment.getUserList().add(user);

        userRepository.save(user);
        commentRepository.save(comment);
    }

    private void addReport(Comment comment, User user) {
        user.getReportedComments().add(comment);
        comment.getListOfUsers().add(user);

        userRepository.save(user);
        commentRepository.save(comment);
    }


    /**
     * generate child date after parent date
     *
     * @param minDate date of parent (need to be earlier that child date)
     * @return randomDate after parent randomDate
     */
    private Date randomDateAfter(Date minDate) {
        long minMillis = minDate.getTime();
        long maxMillis = System.currentTimeMillis(); // Use current time as upper bound
        long randomMillis = ThreadLocalRandom.current().nextLong(minMillis, maxMillis);

        Date randomDate = new Date(randomMillis);
        return randomDate;
    }

    /*
      because we want to test statistics with more comments in the same day, we need to use a list
      where are stocked a static number of dates
      if we want more ideas/comments/replies in the same day, we need a small number
    */

    /**
     * we want to generate a random list of dates in interval
     *
     * @param numberOfDateC - the number of dates that we want to create
     * @return list of dates
     */
    public ArrayList<Date> randomDateList(int numberOfDateC) {
        ArrayList<Date> randomDate = null;
        LocalDate start = LocalDate.of(2023, 7, 3);
        LocalDate end = LocalDate.now();
        for (int j = 1; j < numberOfDateC; j++) {
            randomDate = new ArrayList<>();
            long randomEpochDay = ThreadLocalRandom.current().nextLong(start.toEpochDay(), end.toEpochDay());
            Date dateR = Date.from(LocalDate.ofEpochDay(randomEpochDay).atStartOfDay(ZoneId.systemDefault()).toInstant());
            randomDate.add(dateR);
        }
        return randomDate;
    }

    /**
     * return random number - for date
     *
     * @return random number
     */

    //we use it for choose a random number for entities in constructor (random number of replies per comment, of comments per idea)
    public int givenList_shouldReturnARandomElement() {
        List<Integer> givenList = Arrays.asList(1, 2, 3);
        Random rand = new Random();
        int randomElement = givenList.get(rand.nextInt(givenList.size()));
        return randomElement;
    }

    /**
     * random generator from choosing one date from randomDateList
     *
     * @param randomList - dateList
     * @return one random date
     */

    public Date randomDateFromList(List<Date> randomList) {
        Random rand = new Random();
        List<Date> givenList = randomList;

        int numberOfElements = givenList_shouldReturnARandomElement();

        Date randomElement = null;
        for (int i = 0; i < numberOfElements; i++) {
            int randomIndex = rand.nextInt(givenList.size());
            randomElement = givenList.get(randomIndex);
        }
        return randomElement;
    }

    /**
     * random generic
     *
     * @param randomList - generic list
     * @param <T>        generic type
     * @return random generic entity
     */

    public static <T> T randomElementFromList(List<T> randomList) {
        if (randomList == null || randomList.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }

        Random rand = new Random();
        T randomElement = randomList.get(rand.nextInt(randomList.size()));
        return randomElement;
    }


    /**
     * CONSTRUCTOR - create new User
     *
     * @param isActive     - describe if user have or not an active account
     * @param role         - admin or standard
     * @param avatar       - profile photo
     * @param email        - unique entity
     * @param username     - unique entity
     * @param fullName     - name of user
     * @param hashPassword - encrypted password
     * @param hasPassword  - describes the difference between accepted/declined request for create an account
     *                     (if user was declined, he doesn't have a password)
     * @param isFirstLogin - check if the user's login is for the first time
     * @return user
     */
    private static User createUser(Boolean isActive, Role role, Avatar avatar,
                                   String email, String username, String fullName,
                                   String hashPassword, Boolean hasPassword, Boolean isFirstLogin) {
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
        user.setIsFirstLogin(isFirstLogin);
        return user;
    }

    /**
     * random function for status (create idea)
     *
     * @param clazz enum class
     * @param <T>   one entity of enum
     * @return one entity from that enum
     */

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * CONSTRUCTOR - create new Category
     *
     * @param text - name of category
     * @return category
     */


    private static Category createCategory(
            String text
    ) {
        Category category = new Category();
        category.setText(text);
        return category;
    }


    /**
     * CONSTRUCTOR - create new Avatar
     *
     * @param filePath - path for finding image for avatar
     * @param fileName - name of file (for image-avatar)
     * @return avatar
     * @throws IOException - can't read input file
     */

    private static Avatar createAvatar(String filePath, String fileName
    ) throws IOException {
        Avatar avatar = new Avatar();
        File file = new File(filePath);
        byte[] imageByte = Files.readAllBytes(file.toPath());
        avatar.setData(imageByte);
        avatar.setFileName(fileName);

        return avatar;
    }


    /**
     * CONSTRUCTOR - create new Image
     *
     * @param fileName - name of file (for image)
     * @param fileType - type of file-image
     * @param filePath - path for finding image
     * @return image
     * @throws IOException - can't read input file
     */
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

    /**
     * CONSTRUCTOR - create new Idea
     *
     * @param user       -author of idea
     * @param status     - DRAFT/OPEN/IMPLEMENTED
     * @param text       - text of idea
     * @param title      - title of idea
     * @param image      - image name
     * @param date       - create date
     * @param categories - list of categories
     * @return idea
     */

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

    /**
     * CONSTRUCTOR - create new Reply
     *
     * @param date      - create date
     * @param commentId - id the unique identifier of the comment (parent of reply)
     * @param ideaId    - id the unique identifier of the reply
     * @param user      - author of comment
     * @param text      - reply text
     * @return reply
     */


    private static Comment createReply(Date date, Comment commentId, Idea ideaId,
                                       User user, String text) {
        Comment comment = new Comment();
        comment.setCreationDate(date);
        comment.setIdea(null);
        comment.setParent(commentId);
        comment.setUser(user);
        comment.setCommentText(text);
        return comment;
    }

    /**
     * CONSTRUCTOR - create new Comment
     *
     * @param date     - create date
     * @param ideaId   - id the unique identifier of the idea
     * @param parentId - just reply can have this parameter not null
     * @param user     - author of reply
     * @param text     - reply text
     * @return comment
     */

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

    private static Rating addRating(Idea idea, User user, Double rating) {
        Rating newRating = new Rating();
        newRating.setIdea(idea);
        newRating.setUser(user);
        newRating.setRating(rating);
        return newRating;

    }

}
