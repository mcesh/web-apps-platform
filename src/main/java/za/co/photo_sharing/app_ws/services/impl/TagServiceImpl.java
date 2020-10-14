package za.co.photo_sharing.app_ws.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.Tag;
import za.co.photo_sharing.app_ws.exceptions.InvalidTagException;
import za.co.photo_sharing.app_ws.repo.TagRepository;
import za.co.photo_sharing.app_ws.services.TagService;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;

@Service
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
                throw new InvalidTagException(
                        "Invalid tag " + violation.getPropertyPath() + ": " + violation.getMessage());
            }
        }
        int incrementArticleCount = tag.getCount() + 1;
        tagRepository.updateArticleCount(incrementArticleCount,tag.getName());
        return tag;
    }
}
