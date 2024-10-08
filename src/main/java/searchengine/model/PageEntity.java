package searchengine.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pages", indexes = @Index(name = "path_index", columnList = "path", unique = true))
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id")
    @NonNull
    private SiteEntity site;

    @Column(name = "path", columnDefinition = "VARCHAR(255)")
    @NonNull
    private String path;

    @Column(name = "code", columnDefinition = "INT")
    @NonNull
    private Integer code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    @NonNull
    private String content;
}
