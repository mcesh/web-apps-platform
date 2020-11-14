package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.shared.dto.SkillSetDto;

public interface SkillSetService {

    SkillSetDto findById(Long id);
    SkillSetDto updateSkillSet(Long id, SkillSetDto skillSetDto);
}
