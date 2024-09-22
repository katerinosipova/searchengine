package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sites")
public class SiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')")
    @NonNull
    private IndexingStatus status;

    @CreationTimestamp
    @Column(name = "status_time", columnDefinition = "DATETIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh-mm")
    @NonNull
    private LocalDateTime statusTime;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)")  //VARCHAR(100)
    @NonNull
    private String url;

    @Column(columnDefinition = "VARCHAR(255)")
    @NonNull
    private String name;

//    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL) //??, fetch = FetchType.LAZY
//    private Set<PageEntity> pages;
}
