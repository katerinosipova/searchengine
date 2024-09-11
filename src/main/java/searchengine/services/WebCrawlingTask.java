package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.JsoupConfig;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
public class WebCrawlingTask extends RecursiveTask<Set<String>>{

    private  JsoupConfig jsoupConfig;
    private final String link;
    private static final Set<String> linkSet = ConcurrentHashMap.newKeySet();
    public WebCrawlingTask(String link) {
        this.link = link;
    }

    @Override
    protected Set<String> compute() {
        log.info("Начать индексацию с главной страницы");
        Set<String> links = new TreeSet<>();
        HashSet<WebCrawlingTask> taskList = new HashSet<>();
        try {
            Thread.sleep(150);
            Document document = Jsoup.connect(link)
                    .userAgent(jsoupConfig.getUserAgent())
                    .referrer(jsoupConfig.getReferrer())
                    .get();

            Elements element = document.select("a[href]");
            for (Element e : element) {
                String linkFromAttr = e.absUrl("href").replaceAll("/$", "");
                if (!linkFromAttr.contains(link) || linkSet.add(linkFromAttr) ||
                        linkFromAttr.contains("#") || linkFromAttr.contains(".png")
                        || linkFromAttr.contains(".pdf")) {
                    continue;
                }
                links.add(linkFromAttr);
                WebCrawlingTask task = new WebCrawlingTask(linkFromAttr);
                task.fork();
                taskList.add(task);
            }
        }catch (HttpStatusException he) {
            log.error("Ошибка при получении URL: " + link);
        } catch (Exception e) {
           e.printStackTrace();
        }
        for (WebCrawlingTask task1 : taskList) {
            links.addAll(task1.join());
        }
        return  links;
    }
}






