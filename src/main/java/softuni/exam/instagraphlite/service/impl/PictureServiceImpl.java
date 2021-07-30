package softuni.exam.instagraphlite.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.instagraphlite.models.dtos.jsonDtos.PictureSeedDto;
import softuni.exam.instagraphlite.models.entities.Picture;
import softuni.exam.instagraphlite.repository.PictureRepository;
import softuni.exam.instagraphlite.service.PictureService;
import softuni.exam.instagraphlite.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl implements PictureService {
    private final static Double SIZE_FILTER_CRITERIA = 30000.00;
    private final static String PICTURES_FILE_PATH = "src/main/resources/files/pictures.json";
    private final PictureRepository pictureRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public PictureServiceImpl(PictureRepository pictureRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.pictureRepository = pictureRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return pictureRepository.count()> 0;
    }

    @Override
    public String readFromFileContent() throws IOException {
        return Files.readString(Path.of(PICTURES_FILE_PATH));
    }

    @Override
    public String importPictures() throws IOException {
        PictureSeedDto[] pictureSeedDtos = gson.fromJson(readFromFileContent(),PictureSeedDto[].class);
        StringBuilder builder = new StringBuilder();
        Set<Picture> pictures = new HashSet<>();
        Arrays.stream(pictureSeedDtos)
                        .forEach(pictureSeedDto -> {
                        boolean valid = validationUtil.isValid(pictureSeedDto);
                            builder.append(valid ?
                                    String.format("Successfully imported Picture, with size %.2f",pictureSeedDto.getSize()) :
                            "Invalid Picture");
                            builder.append(System.lineSeparator());
                            if (valid){
                                pictures.add(modelMapper.map(pictureSeedDto, Picture.class  ));

                            }
                        });
        pictureRepository.saveAll(pictures);

        return builder.toString();
    }

    @Override
    public String exportPictures() {

        return pictureRepository.findPicturesBySizeGreaterThanOrderBySizeAsc(SIZE_FILTER_CRITERIA).stream()
                .map(Picture::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public Picture findPictureByPath(String picture) {
        return pictureRepository.findPictureByPath(picture);
    }
}
