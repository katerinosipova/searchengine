package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.PageEntity;
import searchengine.repository.PageRepository;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public class WebCrawlingTask extends RecursiveTask<Set<String>>{

    private final String url;
//    private final PageRepository pageRepository;
    private static final Set<String> linkSet = ConcurrentHashMap.newKeySet();

    public WebCrawlingTask(String url) {
        this.url = url;
    }


    @Override
    protected Set<String> compute() {
        log.info("Начать индексацию с главной страницы");
        Set<String> links = new TreeSet<>();
        HashSet<WebCrawlingTask> taskList = new HashSet<>();
        try {
            Thread.sleep(150);
            Document document = Jsoup.connect("https://www.lenta.ru")
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) " +
                            "Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();

            Elements element = document.select("a[href]");
            for (Element e : element) {
                String linkFromAttr = e.absUrl("href").replaceAll("/$", "");
                if (!linkFromAttr.contains(url) || !linkSet.add(linkFromAttr) ||
                        linkFromAttr.contains("#") || linkFromAttr.contains(".png")
                        || linkFromAttr.contains(".pdf")) {
                    continue;
                }
//                PageEntity pageEntity = new PageEntity();
//                pageEntity.setContent(e.text());
//                pageEntity.setCode(200);
//                pageEntity.setSite(pageEntity.getSite());
//                pageEntity.setPath(url);
//                pageRepository.save(pageEntity);

                links.add(linkFromAttr);
                WebCrawlingTask task = new WebCrawlingTask(linkFromAttr);
                task.fork();
                taskList.add(task);
            }
        }catch (HttpStatusException he) {
            log.error("Ошибка при получении URL: "+ url);
        } catch (Exception e) {
           e.printStackTrace();
        }
        for (WebCrawlingTask task1 : taskList) {
            links.addAll(task1.join());
        }
        return  links;
    }
}






