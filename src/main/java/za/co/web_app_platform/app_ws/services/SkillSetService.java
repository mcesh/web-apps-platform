package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.shared.dto.SkillSetDto;

import java.util.List;

public interface SkillSetService {

    SkillSetDto findById(Long id);
    SkillSetDto updateSkillSet(Long id, SkillSetDto skillSetDto);
    List<SkillSetDto> findAllSkillSets(int page, int size);
    void deleteSkillSetById(Long id);
}
