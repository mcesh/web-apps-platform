package za.co.photo_sharing.app_ws.entity;

import javax.persistence.AttributeConverter;

public class ArticleStatusConverter implements AttributeConverter<Article.Status, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Article.Status status) {
        return status.getCode();
    }

    @Override
    public Article.Status convertToEntityAttribute(Integer integer) {
        return Article.Status.findByCode(integer);
    }
}
