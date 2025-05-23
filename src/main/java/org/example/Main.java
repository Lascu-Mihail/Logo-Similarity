package org.example;

import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String file = "E:\\Logo-Similarity\\src\\main\\resources\\logos.snappy.parquet";

        try {
            List<String> domains = loadDomains(file);
            if (domains.isEmpty()) {
                System.out.println("❗ Nu s-au găsit domenii în fișier.");
                return;
            }

            Map<String, List<String>> groups = groupBySimilarity(domains, 0.75);
            printGroups(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Citim din fișierul Parquet
    private static List<String> loadDomains(String path) throws IOException {
        List<String> domains = new ArrayList<>();

        try (ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(new Path(path)).build()) {
            GenericRecord record;
            while ((record = reader.read()) != null) {
                domains.add(record.get("domain").toString());
            }
        }

        return domains;
    }

    // Grupam domenii
    public static Map<String, List<String>> groupBySimilarity(List<String> list, double threshold) {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        boolean[] grouped = new boolean[list.size()];

        for (int i = 0; i < list.size(); i++) {
            if (grouped[i]) continue;

            List<String> group = new ArrayList<>();
            group.add(list.get(i));
            grouped[i] = true;

            for (int j = i + 1; j < list.size(); j++) {
                if (!grouped[j] && similarity(list.get(i), list.get(j)) >= threshold) {
                    group.add(list.get(j));
                    grouped[j] = true;
                }
            }

            groups.put("Group " + (groups.size() + 1), group);
        }

        return groups;
    }

    // Calculam similaritatea între două stringuri (1.0 = identic)
    public static double similarity(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 1.0; // daca ambele sunt goale
        return 1.0 - (double) levenshtein(a, b) / maxLen;
    }

    // Distanța Levenshtein (număr de modificări minime pentru a transforma un string în altul)
    public static int levenshtein(String s1, String s2) {
        int[] prev = new int[s2.length() + 1];
        int[] curr = new int[s2.length() + 1];

        for (int j = 0; j <= s2.length(); j++) prev[j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(Math.min(
                                curr[j - 1] + 1,
                                prev[j] + 1),
                        prev[j - 1] + cost);
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[s2.length()];
    }

    // Afișam grupurile rezultate
    private static void printGroups(Map<String, List<String>> groups) {
        for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String site : entry.getValue()) {
                System.out.println("  - " + site);
            }
            System.out.println();
        }
    }
}