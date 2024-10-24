package searchengine.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pages", indexes = @Index(name = "path_index", columnList = "path", unique = true)) //, unique = true
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", columnDefinition = "INT", nullable = false)
    private SiteEntity site;


    @Column(name = "path", columnDefinition = "VARCHAR(255)", nullable = false) //, nullable = false
    private String path;


    @Column(name = "code", columnDefinition = "INT", nullable = false)
    private Integer code;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false) // MEDIUMTEXT LONGTEXT
    private String content;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<IndexEntity> indexes;

}
