package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.entity.Tag;

public interface TagService {
    Tag findOrCreateByName(String name);
}
