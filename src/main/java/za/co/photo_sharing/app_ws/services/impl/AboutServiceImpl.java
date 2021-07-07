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
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.AboutRepository;
import za.co.photo_sharing.app_ws.services.AboutService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AboutDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static za.co.photo_sharing.app_ws.services.impl.UserServiceImpl.*;

@Service
@Slf4j
@Transactional
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
                int ratingPercent = utils.calculateRatingPercent(skillSet.getRating());
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

    @Override
    public AboutDTO updateAboutInfo(Long id, String email, AboutDTO aboutDTO) {
        userService.findByEmail(email);
        AtomicReference<AboutDTO> map = new AtomicReference<>(new AboutDTO());
        Optional<AboutPage> aboutPage = aboutRepository.findById(id);
        if (!aboutPage.isPresent()){
            throw new UserServiceException(HttpStatus.NOT_FOUND, ErrorMessages.ABOUT_PAGE_NOT_FOUND.getErrorMessage());
        }
        aboutPage.map(aboutPage1 -> {
            aboutPage1.setDescription(aboutDTO.getDescription());
            if (aboutDTO.getSkillSets()!=null && aboutDTO.getSkillSets().size() > 0){
                aboutPage1.setSkillSets(updateSkillSet(aboutDTO));
            }
            aboutRepository.save(aboutPage1);
             map.set(modelMapper.map(aboutPage1, AboutDTO.class));
            return true;
        });

        return map.get();
    }

    private Set<SkillSet> updateSkillSet(AboutDTO aboutDTO) {
        Set<SkillSet> skillSets = new HashSet<>();
        aboutDTO.getSkillSets().forEach(skillSet -> {
            int ratingPercent = utils.calculateRatingPercent(skillSet.getRating());
            skillSet.setRatingCalc(ratingPercent);
            SkillSet skillSet1 = modelMapper.map(skillSet, SkillSet.class);
            skillSets.add(skillSet1);
        });
        return skillSets;
    }

    private void verifyIfPageExists(String email) {
        if (aboutRepository.findByEmail(email) != null) {
            throw new ArticleServiceException(HttpStatus.BAD_REQUEST, ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        }
    }

    @Override
    public AboutDTO addImage(Long id, String email, MultipartFile file) {
        userService.findByEmail(email);
        final AboutDTO[] aboutDTO = new AboutDTO[1];
        Optional<AboutPage> about = getById(id);
        about.map(aboutPage1 -> {
            String url = "";
            try {
                url = utils.uploadToCloudinary(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            aboutPage1.setBase64StringImage(url);
            AboutPage returnValue = aboutRepository.save(aboutPage1);
            aboutDTO[0] = modelMapper.map(returnValue, AboutDTO.class);
            return true;
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
