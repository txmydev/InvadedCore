package invaded.cc.core.database.tags;

import invaded.cc.core.tags.Tag;

import java.util.List;

public interface TagStorage {

    void save(Tag tag);
    void saveAll();
    void load();
    void remove(Tag tag);

    Tag getById(String id);
    Tag getTagByDisplay(String display);

    List<Tag> getTags();
}
