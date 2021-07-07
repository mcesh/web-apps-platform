package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.entity.Tag;

public interface TagService {
    Tag findOrCreateByName(String name);
}
