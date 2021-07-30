package softuni.exam.instagraphlite.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.xmlDtos.PostSeedRootDto;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.models.entities.Post;
import softuni.exam.instagraphlite.models.entities.User;
import softuni.exam.instagraphlite.repository.PostRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.PostService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;
import softuni.exam.instagraphlite.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final static String POSTS_PATH_FILE = "src/main/resources/files/posts.xml";
    private final PostRepository postRepository;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;
    private final UserService userService;

    public PostServiceImpl(PostRepository postRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil, PictureService pictureService, UserService userService) {
        this.postRepository = postRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
        this.userService = userService;
    }

    @Override
    public boolean areImported() {
        return postRepository.count() > 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(POSTS_PATH_FILE));
    }

    @Override
    public String importPosts() throws IOException, JAXBException {
        PostSeedRootDto postSeedRootDto = xmlParser.fromFile(POSTS_PATH_FILE, PostSeedRootDto.class);
        StringBuilder builder = new StringBuilder();
        List<Post> posts = new ArrayList<>();
        postSeedRootDto.getPosts()
                .forEach(postSeedDto -> {
                    boolean valid = validationUtil.isValid(postSeedDto);


                    if (valid) {
                        Post post = modelMapper.map(postSeedDto, Post.class);
                        Picture picture = pictureService.findPictureByPath(postSeedDto.getPicture().getPath());
                        User user = userService.findUserByUsername(postSeedDto.getUser().getUsername());
                        if (user != null && picture != null) {
                            post.setUser(user);
                            post.setPicture(picture);
                            postRepository.save(post);
                            builder.append(String.format("Successfully imported Post, made by %s", postSeedDto.getUser().getUsername()));
                        } else {
                            builder.append("Invalid Post");
                        }
                    } else {
                        builder.append("Invalid Post");
                    }
                    builder.append(System.lineSeparator());
                });

        return builder.toString();
    }
}
