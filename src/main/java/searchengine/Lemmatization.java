package searchengine;


import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import searchengine.services.LemmaFinder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Lemmatization {

    public static void main(String[] args) throws IOException {
//
//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("домов");
//        wordBaseForms.forEach(System.out::println);

//        LemmaFinder lemmaFinder = new LemmaFinder();
//        System.out.println(LemmaFinder.collectLemmas("Пришла пора жениться и ехать со двора"));
//        HashMap<String, Integer> lemmaFind = (HashMap<String, Integer>) lemmaFinder.collectLemmas("Пришла пора жениться и ехать со двора");
//        lemmaFind.forEach((key, value) -> System.out.println(key + "-" + value));
    }
}
