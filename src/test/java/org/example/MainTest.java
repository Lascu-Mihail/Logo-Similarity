package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
//    @Test
//    void testLevenshtein() {
//        assertEquals(0, Main.levenshtein("test", "test"));
//        assertEquals(1, Main.levenshtein("test", "tent"));
//        assertEquals(3, Main.levenshtein("kitten", "sitting"));
//        assertEquals(5, Main.levenshtein("spark", ""));
//    }

    @Test
    void testSimilarity() {
        assertEquals(1.0, Main.similarity("abc", "abc"), 0.001);
        assertTrue(Main.similarity("test", "tent") > 0.7);
        assertTrue(Main.similarity("apple", "orange") < 0.5);
    }

    @Test
    void testGroupBySimilarity() {
        List<String> domains = List.of("google.com", "goggle.com", "example.com", "samplesite.com", "samplesit.com");

        Map<String, List<String>> groups = Main.groupBySimilarity(domains, 0.85);


        boolean googleGroupFound = groups.values().stream().anyMatch(group ->
                group.contains("google.com") && group.contains("goggle.com"));

        boolean sampleGroupFound = groups.values().stream().anyMatch(group ->
                group.contains("samplesite.com") && group.contains("samplesit.com"));

        assertTrue(googleGroupFound);
        assertTrue(sampleGroupFound);
    }

    @Test
    public void testLevenshtein() {

        //Stringuri identici
        assertEquals(0,Main.levenshtein("test","test"));

        //Stringuri Goale

        assertEquals(0,Main.levenshtein("",""));
        assertEquals(4,Main.levenshtein("","test"));
        assertEquals(4,Main.levenshtein("test",""));

        //Cazuri simple

        assertEquals(3, Main.levenshtein("kitten", "sitting"));
        assertEquals(1, Main.levenshtein("cat", "cats"));
        assertEquals(1, Main.levenshtein("cat", "cut"));
        assertEquals(2, Main.levenshtein("flaw", "lawn"));

        //Sensibilitate UpperCase
        assertEquals(1, Main.levenshtein("Test", "test"));

        assertEquals(4, Main.levenshtein("abcd", "wxyz"));
    }
}