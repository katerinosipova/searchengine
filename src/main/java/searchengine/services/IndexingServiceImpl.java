package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.IndexingStatus;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;


@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingServiceImpl implements IndexingService{

    @Autowired
    private final SitesList sitesList;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    private  final ForkJoinPool pool = new ForkJoinPool();

    private volatile boolean stopIndexing = false;


    @Override
    public void startIndexing() {
        if(stopIndexing) {
            return;
        }
        if(siteRepository != null || pageRepository != null) {
            siteRepository.deleteAll();
            pageRepository.deleteAll();
        }
        for (Site site : sitesList.getSites()) {
            try {
                SiteEntity siteEntity = new SiteEntity();
                siteEntity.setUrl(site.getUrl());
                siteEntity.setName(site.getName());
                siteEntity.setStatus(IndexingStatus.INDEXING);
                siteEntity.setStatusTime(LocalDateTime.now());
                siteRepository.save(siteEntity);

                String mainPageUrl = site.getUrl().replaceAll("www", "");
                WebCrawlingTask webCrawlingTask = new WebCrawlingTask(mainPageUrl);
                TreeSet<String> parser = new TreeSet<>(pool.invoke(webCrawlingTask));
                ForkJoinTask<Set<String>> task = pool.submit(webCrawlingTask);

                System.out.println("Сайт индексируется " + site.getName());

                for (String pageUrl : parser) {
                    if(stopIndexing) {
                        break;
                    }
                    if(pageUrl.equals(siteEntity.getUrl())) {
                        continue;
                    }
                    Document doc = Jsoup.connect(pageUrl).get();
                    String content = doc.body().html();
                    try {
                        PageEntity pageEntity = new PageEntity();
                        pageEntity.setSite(siteEntity);
                        pageEntity.setPath(pageUrl.replaceAll(mainPageUrl, ""));
                        pageEntity.setCode(doc.connection().response().statusCode());
                        pageEntity.setContent(content);
                        pageRepository.save(pageEntity);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(stopIndexing && task.isDone()) {
                    System.out.println("Индексирование сайта " + site.getName() + " завершена");

                    siteEntity.setStatus(IndexingStatus.INDEXED);
                    siteRepository.save(siteEntity);
                }
            } catch (HttpStatusException he) {
                he.printStackTrace();
            }catch (Exception e) {
                System.out.println("Ошибка индексации");
                SiteEntity siteEntity = siteRepository.findAll().stream()
                        .filter(se -> se.getUrl().equals(site.getUrl()))
                        .findFirst().orElse(null);
                siteEntity.setStatus(IndexingStatus.FAILED);
                siteEntity.setLastError(e.getMessage());
                siteRepository.save(siteEntity);
            }
        }
    }

    @Override
    public void stopIndexing() {
        System.out.println("Зпущен процесс остановки индексации сайтов");
        try {
            stopIndexing = true;
            if(pool != null) {
                pool.shutdownNow();
            }
            List<SiteEntity> siteEntities = siteRepository.findAll();
            for (SiteEntity siteEntity : siteEntities) {
                if(siteEntity.getStatus() == IndexingStatus.INDEXING) {
                    siteEntity.setStatus(IndexingStatus.FAILED);
                    siteEntity.setLastError("Индексация остановлена пользователем");
                    siteRepository.save(siteEntity);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Индексация успешно остановлена");
    }
}




