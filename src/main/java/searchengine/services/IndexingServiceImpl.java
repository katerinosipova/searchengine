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
import searchengine.dto.statistics.IndexingResponse;
import searchengine.model.IndexingStatus;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    public IndexingResponse startIndexing() {
        if(stopIndexing) {
            throw new RuntimeException("Индексация уже запущена");
        }
        //удалять все имеющиеся данные по этому сайту (записи из таблиц site и page);
        if(siteRepository != null || pageRepository != null) {
            siteRepository.deleteAll();
            pageRepository.deleteAll();
            log.info("Удалены данные сайта");
        }
        //создавать в таблице site новую запись со статусом INDEXING
        for (Site site : sitesList.getSites()) {
            try {
                SiteEntity siteEntity = new SiteEntity();
                siteEntity.setUrl(site.getUrl());
                siteEntity.setName(site.getName());
                siteEntity.setStatus(IndexingStatus.INDEXING);
                siteEntity.setStatusTime(LocalDateTime.now());
                siteRepository.save(siteEntity);
                log.info("Сохранили " + siteEntity);

                //обходить все страницы, начиная с главной,
                // добавлять их адреса, статусы и содержимое в базу данных в таблицу page;
                String mainPageUrl = site.getUrl().replaceAll("www", "");
                WebCrawlingTask webCrawlingTask = new WebCrawlingTask(mainPageUrl);
                TreeSet<String> parser = new TreeSet<>(pool.invoke(webCrawlingTask));
                ForkJoinTask<Set<String>> task = pool.submit(webCrawlingTask);
                task.join();
                log.info("Сайт индексируется " + site.getName());

                for (String pageUrl : parser) {
                    if(stopIndexing) {
                        break;
                    }
                    if(pageUrl.equals(siteEntity.getUrl())) {
                        continue;
                    }
                    Document doc = Jsoup.connect(pageUrl).get();
                    try {
                        PageEntity pageEntity = new PageEntity();
                        pageEntity.setSite(site);
                        pageEntity.setPath(pageUrl.replaceAll(mainPageUrl, ""));
                        pageEntity.setCode(doc.connection().response().statusCode());
                        pageEntity.setContent(doc.body().html());
                        pageRepository.save(pageEntity);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(!stopIndexing && task.isDone()) {
                    log.info("Индексированиe сайта " + site.getName() + " завершенo");

                    siteEntity.setStatus(IndexingStatus.INDEXED);
                    siteRepository.save(siteEntity);
                }
            } catch (Exception e) {
                log.error("Ошибка индексации");
                SiteEntity siteEntity = siteRepository.findAll().stream()
                        .filter(se -> se.getUrl().equals(site.getUrl()))
                        .findFirst().orElse(null);
                siteEntity.setStatus(IndexingStatus.FAILED);
                siteEntity.setLastError(e.getMessage());
                siteRepository.save(siteEntity);
            }
        }
        return new IndexingResponse();
    }

    @Override
    public IndexingResponse stopIndexing() {
        log.info("Запущен процесс остановки индексации сайтов");
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
        log.info("Индексация успешно остановлена");
        return stopIndexing();
    }
}




