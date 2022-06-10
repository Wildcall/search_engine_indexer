package ru.malygin.indexer.indexer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class Lemmantisator {

    /**
     * Метод обрабатывает контент страницы, подсчитывая коэффициенты для отдельных лем, согласно весовым коэффициентам
     *
     * @param content        исходная страница
     * @param selectorWeight весовые коэффициенты
     * @return Map<String, Double>
     */
    public static Map<String, Double> parsePage(String content,
                                                Map<String, Double> selectorWeight,
                                                AlphabetType alphabetType) {
        try {
            Map<String, Double> wordRankMap = new HashMap<>();
            Document document = Jsoup.parse(content);
            selectorWeight.forEach((field, weight) -> Lemmantisator
                    .getWordsRank(document
                                          .getElementsByTag(field)
                                          .toString(), weight, alphabetType)
                    .forEach((word, rank) -> wordRankMap.compute(word,
                                                                 (key, val) -> (val == null) ? rank : val + rank)));
            return wordRankMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Метод разбивает исходный текст на отдельные слова(леммы), считает количество одинаковых слов(лемм) и умножает количество на весовой коэффициент.
     * Возвращает пары слова и её суммарного веса в данном тексте
     *
     * @param text   исходный текст
     * @param weight весовой коэффициент для данного текста
     * @return Map<String, Double>
     */
    private static Map<String, Double> getWordsRank(String text,
                                                    Double weight,
                                                    AlphabetType alphabetType) {
        Map<String, Double> lemmas = new HashMap<>();
        try {
            LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
            getCyrillicText(text).forEach(item -> {
                if (item.isEmpty() || item.isBlank()) return;
                luceneMorphology
                        .getNormalForms(item)
                        .forEach(word -> {
                            List<String> info = luceneMorphology.getMorphInfo(word);
                            boolean skipWord = false;
                            for (String tmp : info) {
                                if (tmp.contains("СОЮЗ") || tmp.contains("МЕЖД") || tmp.contains("ПРЕДЛ")) {
                                    skipWord = true;
                                    break;
                                }
                            }
                            if (!skipWord) {
                                lemmas.compute(word, (key, val) -> (val == null) ? weight : val + weight);
                            }
                        });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lemmas;
    }

    /**
     * Выполняет удаление из теста все символов отличных от кириллицы
     *
     * @param text исходный текст
     * @return Stream
     */
    private static Stream<String> getCyrillicText(String text) {
        return Arrays.stream(text
                                     .replaceAll("[^А-Яа-я\\s]", "")
                                     .replaceAll("(\\s)+", " ")
                                     .toLowerCase(Locale.ROOT)
                                     .split(" "));
    }
}
