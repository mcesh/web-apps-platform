package za.co.photo_sharing.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import za.co.photo_sharing.app_ws.entity.SkillSet;
import za.co.photo_sharing.app_ws.exceptions.ArticleServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.SkillSetRepository;
import za.co.photo_sharing.app_ws.services.SkillSetService;
import za.co.photo_sharing.app_ws.shared.dto.SkillSetDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class SkillSetServiceImpl implements SkillSetService {

    @Autowired
    private SkillSetRepository skillSetRepository;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();

    @Transactional
    @Override
    public SkillSetDto findById(Long id) {
        final SkillSetDto[] skillSetDto = {new SkillSetDto()};
        Optional<SkillSet> skillSet = getSkillSet(id);
        skillSet.map(skill -> {
            skillSetDto[0] = (modelMapper.map(skill, SkillSetDto.class));
            return true;
        });
        return skillSetDto[0];
    }


    @Transactional
    @Override
    public SkillSetDto updateSkillSet(Long id, SkillSetDto skillSetDto) {
        Optional<SkillSet> skillSet = getSkillSet(id);
        AtomicReference<SkillSetDto> setDto = new AtomicReference<>(new SkillSetDto());
        skillSet.map(skillSet1 -> {
            int ratingPercent = utils.calculateRatingPercent(skillSetDto.getRating());
            skillSet1.setRating(skillSetDto.getRating());
            skillSet1.setSkill(skillSetDto.getSkill());
            skillSet1.setRatingCalc(ratingPercent);
            SkillSet returnValue = skillSetRepository.save(skillSet1);
            setDto.set(modelMapper.map(returnValue, SkillSetDto.class));
            return true;
        });

        return setDto.get();
    }

    @Transactional
    @Override
    public List<SkillSetDto> findAllSkillSets(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<SkillSetDto> skillSetDtos = new ArrayList<>();
        Page<SkillSet> skillSetPage = skillSetRepository.findAll(pageable);
        List<SkillSet> skillSets = skillSetPage.getContent();
        if (CollectionUtils.isEmpty(skillSets)) {
            return skillSetDtos;
        }
        skillSets.stream()
                .sorted(Comparator.comparing(SkillSet::getRatingCalc))
                .forEach(skillSet -> {
                    SkillSetDto skillSetDto = modelMapper.map(skillSet, SkillSetDto.class);
                    skillSetDtos.add(skillSetDto);
                });
        return skillSetDtos;
    }

    @Transactional
    @Override
    public void deleteSkillSetById(Long id) {
        Optional<SkillSet> skillSet = getSkillSet(id);
        skillSet.map(skillSet1 -> {
            skillSetRepository.delete(skillSet1);
            return true;
        });

    }

    private Optional<SkillSet> getSkillSet(Long id) {
        Optional<SkillSet> skillSet = skillSetRepository.findById(id);
        if (!skillSet.isPresent()) {
            throw new ArticleServiceException(HttpStatus.NOT_FOUND, ErrorMessages.SKILL_SET_NOT_FOUND.getErrorMessage());
        }
        return skillSet;
    }
}
