package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.PageEntity;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {
//    @Modifying
//    @Query("DELETE FROM Page p WHERE p. sites.id = :site")
//    void deleteBySiteEntityId(int site);
}
