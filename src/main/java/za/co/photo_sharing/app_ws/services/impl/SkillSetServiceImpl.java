package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.SkillSet;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.SkillSetRepository;
import za.co.photo_sharing.app_ws.services.SkillSetService;
import za.co.photo_sharing.app_ws.shared.dto.SkillSetDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SkillSetServiceImpl implements SkillSetService {

    @Autowired
    private SkillSetRepository skillSetRepository;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public SkillSetDto findById(Long id) {
        final SkillSetDto[] skillSetDto = {new SkillSetDto()};
        Optional<SkillSet> skillSet = getSkillSet(id);
        skillSet.map(skill -> {
            skillSetDto[0] = (modelMapper.map(skill, SkillSetDto.class));
            return skillSetDto[0];
        });
        return skillSetDto[0];
    }


    @Override
    public SkillSetDto updateSkillSet(Long id, SkillSetDto skillSetDto) {
        Optional<SkillSet> skillSet = getSkillSet(id);
        AtomicReference<SkillSetDto> setDto = new AtomicReference<>(new SkillSetDto());
        skillSet.map(skillSet1 -> {
            double ratingPercent = utils.calculateRatingPercent(skillSetDto.getRating());
            skillSet1.setRating(skillSetDto.getRating());
            skillSet1.setSkill(skillSetDto.getSkill());
            skillSet1.setRatingCalc(ratingPercent);
            SkillSet returnValue = skillSetRepository.save(skillSet1);
            setDto.set(modelMapper.map(returnValue, SkillSetDto.class));
            return setDto;
        });

        return setDto.get();
    }

    private Optional<SkillSet> getSkillSet(Long id) {
        Optional<SkillSet> skillSet = skillSetRepository.findById(id);
        if (!skillSet.isPresent()){
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.SKILL_SET_NOT_FOUND.getErrorMessage());
        }
        return skillSet;
    }
}
