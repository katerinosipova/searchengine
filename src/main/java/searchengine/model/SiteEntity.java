package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sites")
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)//, nullable = false
    private IndexingStatus status;

    @CreationTimestamp
    @Column(name = "status_time", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh-mm")
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)",nullable = false)  //VARCHAR(100)
    private String url;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL) //??, fetch = FetchType.LAZY
    private List <PageEntity> pages;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List <LemmaEntity> lemmas;
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "index_search", joinColumns = {@JoinColumn(name = "page_id")})
//    private List <SiteEntity> sites;

}
