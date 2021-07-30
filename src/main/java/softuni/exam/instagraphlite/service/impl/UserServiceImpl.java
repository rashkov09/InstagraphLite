package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.jsonDtos.UserSeedDto;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.models.entities.User;
import softuni.exam.instagraphlite.repository.UserRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.service.UserService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final static String USERS_FILE_PATH = "src/main/resources/files/users.json";
    private  final UserRepository userRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PictureService pictureService;

    public UserServiceImpl(UserRepository userRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil, PictureService pictureService) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.pictureService = pictureService;
    }

    @Override
    public boolean areImported() {
        return userRepository.count()>0 ;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(USERS_FILE_PATH));
    }

    @Override
    public String importUsers() throws IOException {
        UserSeedDto[] userSeedDtos = gson.fromJson(readFromFileContent(),UserSeedDto[].class);
        StringBuilder builder = new StringBuilder();
        List<User> users = new ArrayList<>();
        Arrays.stream(userSeedDtos).forEach(userSeedDto -> {
            boolean valid = validationUtil.isValid(userSeedDto);

            if (valid){
                User user = modelMapper.map(userSeedDto,User.class);
               Picture picture = pictureService.findPictureByPath(userSeedDto.getProfilePicture());
               if (picture != null){
                   user.setPicture(picture);
//                   users.add(user);
                   userRepository.save(user);
                  builder.append(String.format("Successfully imported User: %s",userSeedDto.getUsername()));
               } else {
                   builder.append("Invalid User");
               }
               builder.append(System.lineSeparator());
            } else {
                builder.append("Invalid User");
            }
        });
        return builder.toString();
    }

    @Override
    public String exportUsersWithTheirPosts() {
        return null;
    }

    @Override
    public User findUserByUsername(String name) {
        return userRepository.findUserByUsername(name);
    }
}
