package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.SiteEntity;

@Setter
@Getter
public class Site extends SiteEntity {
    private String url;
    private String name;
}
