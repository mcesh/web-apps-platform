package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.entity.About;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessage;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.repo.AboutRepository;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;
import javax.transaction.Transactional;

import java.text.DecimalFormat;
import java.util.Optional;

@Service
public class AboutServiceImpl implements AboutService {

    private static Logger LOGGER = LoggerFactory.getLogger(AboutServiceImpl.class);
    public static final String ABOUT_PAGE = "ABOUT_PAGE";

    @Autowired
    private AboutRepository aboutRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();

    public static Logger getLog() {
        return LOGGER;
    }

    @Override
    public AboutDTO addAboutPage(AboutDTO aboutDTO, String email) {
        UserDto userDto = userService.findByEmail(email);
        if (aboutRepository.findByEmail(email) != null){
            throw new ArticleServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }
        if (aboutDTO.getSkillSets().size() > 0) {
            aboutDTO.getSkillSets().forEach(skillSet -> {
                double ratingPercent = calculateRatingPercent(skillSet.getRating());
                skillSet.setRatingCalc(ratingPercent);
            });
        }
        About about = modelMapper.map(aboutDTO, About.class);
        about.setEmail(userDto.getEmail());
        About savedBio = aboutRepository.save(about);
        AboutDTO dto = modelMapper.map(savedBio, AboutDTO.class);
        getLog().info("Bio persisted successfully: {} ", dto);
        return dto;
    }

    @Transactional
    @Override
    public AboutDTO addImage(Long id, String email, MultipartFile file) {
        UserDto userDto = userService.findByEmail(email);
        UserProfile userProfile = modelMapper.map(userDto, UserProfile.class);
        final AboutDTO[] aboutDTO = new AboutDTO[1];
        Optional<About> about = getById(id);
        about.map(about1 -> {
            ImageUpload imageUpload = utils.uploadImage(file, userProfile, ABOUT_PAGE);
            about1.setBase64StringImage(imageUpload.getFileName());
            About returnValue = aboutRepository.save(about1);
            aboutDTO[0] = modelMapper.map(returnValue, AboutDTO.class);
            return aboutDTO[0];
        });

        return aboutDTO[0];
    }

    private Optional<About> getById(Long id) {
        getLog().info("Getting image by ID: {} ", id);
        Optional<About> about = aboutRepository.findById(id);
        if (!about.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ABOUT_PAGE_NOT_FOUND.getErrorMessage());
        }
        return about;
    }

    @Override
    public AboutDTO findByEmail(String email) {
        return null;
    }

    @Override
    public AboutDTO findById(Long id) {
        return null;
    }

    private double calculateRatingPercent(double rating) {
        utils.validateRatingNumber(rating);
        double ratingPercentage = (rating / 10) * 100;
        getLog().info("Calculated percentage: {} ", ratingPercentage);
        return ratingPercentage;
    }
}
