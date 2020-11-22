package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import za.co.photo_sharing.app_ws.constants.BucketName;
import za.co.photo_sharing.app_ws.entity.AboutPage;
import za.co.photo_sharing.app_ws.entity.SkillSet;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.model.response.ImageUpload;
import za.co.photo_sharing.app_ws.repo.AboutRepository;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static za.co.photo_sharing.app_ws.services.impl.UserServiceImpl.*;

@Service
@Slf4j
public class AboutServiceImpl implements AboutService {


    public static final String ABOUT_PAGE = "ABOUT_PAGE";

    @Autowired
    private AboutRepository aboutRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    @Autowired
    private FileStoreService fileStoreService;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public AboutDTO addAboutPage(AboutDTO aboutDTO, String email) {
        UserDto userDto = userService.findByEmail(email);
        verifyIfPageExists(email);
        if (aboutDTO.getSkillSets().size() > 0) {
            aboutDTO.getSkillSets().forEach(skillSet -> {
                double ratingPercent = utils.calculateRatingPercent(skillSet.getRating());
                skillSet.setRatingCalc(ratingPercent);
            });
        }
        AboutPage aboutPage = modelMapper.map(aboutDTO, AboutPage.class);
        aboutPage.setEmail(userDto.getEmail());
        AboutPage savedBio = aboutRepository.save(aboutPage);
        AboutDTO dto = modelMapper.map(savedBio, AboutDTO.class);
        log.info("Bio persisted successfully: {} ", dto);
        return dto;
    }

    private void verifyIfPageExists(String email) {
        if (aboutRepository.findByEmail(email) != null) {
            throw new ArticleServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }
    }

    @Transactional
    @Override
    public AboutDTO addImage(Long id, String email, MultipartFile file) {
        UserDto userDto = userService.findByEmail(email);
        UserProfile userProfile = modelMapper.map(userDto, UserProfile.class);
        final AboutDTO[] aboutDTO = new AboutDTO[1];
        Optional<AboutPage> about = getById(id);
        about.map(aboutPage1 -> {
            ImageUpload imageUpload = utils.uploadImage(file, userProfile, ABOUT_PAGE);
            aboutPage1.setBase64StringImage(imageUpload.getFileName());
            AboutPage returnValue = aboutRepository.save(aboutPage1);
            aboutDTO[0] = modelMapper.map(returnValue, AboutDTO.class);
            return aboutDTO[0];
        });

        return aboutDTO[0];
    }

    private Optional<AboutPage> getById(Long id) {
        log.info("Getting image by ID: {} ", id);
        Optional<AboutPage> about = aboutRepository.findById(id);
        if (!about.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ABOUT_PAGE_NOT_FOUND.getErrorMessage());
        }
        return about;
    }

    @Transactional
    @Override
    public AboutDTO findByEmail(String email) {
        AboutPage aboutPagePageDetails = aboutRepository.findByEmail(email);
        if (Objects.isNull(aboutPagePageDetails)) {
            return new AboutDTO();
        }
        List<SkillSet> skillSets = aboutPagePageDetails.getSkillSets().stream().sorted(Comparator.comparing(SkillSet::getRating)).collect(Collectors.toList());
        return modelMapper.map(aboutPagePageDetails, AboutDTO.class);
    }

    @Override
    public AboutDTO findById(Long id) {
        return null;
    }

    @Override
    public String downloadAboutPageImage(String email) {
        UserDto userDto = userService.findByEmail(email);
        AboutPage aboutPagePageDetails = aboutRepository.findByEmail(email);
        String path = String.format("%s/%s/%s", BucketName.WEB_APP_PLATFORM_FILE_STORAGE_SPACE.getBucketName(),
                ABOUT_PAGE,
                userDto.getUsername());

        if (!StringUtils.isEmpty(aboutPagePageDetails.getBase64StringImage())) {
            String key = aboutPagePageDetails.getBase64StringImage();
            byte[] profilePic = fileStoreService.download(path, key);
            return Base64.getEncoder().encodeToString(profilePic);
        }
        // default-profile-picture
        String defaultPicturePath = String.format("%s/%s", BUCKET_NAME,
                DEFAULT_PROFILE_FOLDER);
        byte[] defaultProfilePic = fileStoreService.download(defaultPicturePath, DEFAULT_PROFILE_KEY);
        return Base64.getEncoder().encodeToString(defaultProfilePic);
    }

    @Override
    public void deleteAboutPageById(Long id) {
        Optional<AboutPage> aboutPage = aboutRepository.findById(id);
        if (!aboutPage.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ABOUT_PAGE_NOT_FOUND.getErrorMessage());
        }
        aboutPage.map(aboutPage1 -> {
            aboutRepository.delete(aboutPage1);
            return true;
        });
    }

}
