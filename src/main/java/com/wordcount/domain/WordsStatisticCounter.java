package com.wordcount.domain;

import com.wordcount.domain.dto.Word;
import com.wordcount.domain.dto.WordsStatistic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class WordsStatisticCounter {

    private final List<String> _text;
    private final List<String> _stopWords;
    private final List<String> _dictionaryWords;
    private final boolean _includeWordsIndex;

    private List<Word> _words;

    public WordsStatisticCounter(
            List<String> text,
            List<String> stopWords,
            List<String> dictionaryWords,
            boolean includeWordsIndex
    ) {
        _text = text;
        _stopWords = stopWords != null ? stopWords : Collections.emptyList();
        _dictionaryWords = dictionaryWords;
        _includeWordsIndex = includeWordsIndex;
    }

    public WordsStatistic count() {
        List<String> tokens = getTokens();
        return getStatistics(tokens);
    }

    private List<String> getTokens() {
        return new TextParser(_text).parse();
    }

    private WordsStatistic getStatistics(List<String> tokens) {
        setWords(tokens);

        int wordCount = countWords();
        int uniqueWordCount = countUniqueWords();
        float averageWordLength = countAverageWordLength();
        unknowWordsThatAreNotInADictionary();
        Integer unknownWordCount = countUnknownWords();
        List<Word> wordsIndex = getWordsIndex();

        return new WordsStatistic(wordCount, uniqueWordCount, averageWordLength, unknownWordCount, wordsIndex);
    }

    private void setWords(List<String> tokens) {
        _words = tokens.stream()
                .filter(token -> (isStringAWord(token) && isWordAllowed(token)))
                .map(Word::new)
                .collect(Collectors.toList());
    }

    private int countWords() {
        return _words.size();
    }

    private int countUniqueWords() {
        return new HashSet<>(
                _words.stream().map(Word::getValue).collect(Collectors.toList())
        ).size();
    }

    private float countAverageWordLength() {
        long totalWordsLength = _words.stream().mapToLong(word -> word.getValue().length()).sum();
        float averageWordLength = _words.isEmpty() ? 0f : (float) totalWordsLength / _words.size();
        return round(averageWordLength, 2);
    }

    private void unknowWordsThatAreNotInADictionary() {
        if (_dictionaryWords == null || _dictionaryWords.isEmpty()) return;
        _words.stream()
                .filter(word -> !_dictionaryWords.contains(word.getValue()))
                .forEach(Word::makeWordUnknown);
    }

    private Integer countUnknownWords() {
        return _dictionaryWords == null ? null :
                (int) _words.stream()
                        .filter(word -> !word.wordIsKnown())
                        .count();
    }

    private List<Word> getWordsIndex() {
        return _includeWordsIndex ? sortWords(_words) : null;
    }

    private boolean isStringAWord(String rawString) {
        return rawString.matches("^[a-zA-Z-]*$");
    }

    private boolean isWordAllowed(String word) {
        return !_stopWords.contains(word);
    }

    public static float round(float floatValue, int decimalPlace) {
        return BigDecimal.valueOf(floatValue).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private List<Word> sortWords(List<Word> words) {
        words.sort(new WordSorterCaseInsensitive());
        return words;
    }
}
