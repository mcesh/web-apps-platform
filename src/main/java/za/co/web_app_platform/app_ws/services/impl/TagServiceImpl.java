package za.co.web_app_platform.app_ws.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.web_app_platform.app_ws.entity.Tag;
import za.co.web_app_platform.app_ws.exceptions.InvalidTagException;
import za.co.web_app_platform.app_ws.repo.TagRepository;
import za.co.web_app_platform.app_ws.services.TagService;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag findOrCreateByName(String name) {
        Tag tag = tagRepository.findByName(name);
        if (Objects.isNull(tag)) {
            try {
                tag = new Tag();
                tag.setName(name);
                tag.setCount(0);
                tagRepository.save(tag);
            } catch (ConstraintViolationException exception) {
                ConstraintViolation<?> violation = exception.getConstraintViolations().iterator().next();
                throw new InvalidTagException(HttpStatus.BAD_REQUEST,
                        "Invalid tag " + violation.getPropertyPath() + ": " + violation.getMessage());
            }
        }
        int incrementArticleCount = tag.getCount() + 1;
        tagRepository.updateArticleCount(incrementArticleCount, tag.getName());
        return tag;
    }
}
