package com.me.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleLogParser {

    private static final String KEYWORD = "HTTP/1.1";
    private static final String URL_REGEXP = "\\s+([\\S]*)\\s+" + KEYWORD;
    private static final int TOP_LIMIT = 3;

    public static void main(String[] args) throws IOException {
        var filename = "programming-task-example-data.log";

        try (Stream<String> ipAddresses = extractIpAddresses(filename)) {
            System.out.println("Number of unique IP Addresses: " + ipAddresses.distinct().count());
        }

        System.out.println("---");

        System.out.printf("Top %d most active URLs:\n", TOP_LIMIT);
        try (Stream<String> urls = extractURLs(filename)) {
            getTopOccurrences(urls, TOP_LIMIT).forEach((e -> System.out.printf("%s %d%n", e.getKey(), e.getValue())));
        }
        System.out.println("---");

        System.out.printf("Top %d most active IP Addresses:\n", TOP_LIMIT);
        try (Stream<String> ipAddresses = extractIpAddresses(filename)) {
            getTopOccurrences(ipAddresses, TOP_LIMIT).forEach((e -> System.out.printf("%s %d%n", e.getKey(),
                                                                                      e.getValue())));
        }
    }

    private static Stream<String> extractIpAddresses(String filename) throws IOException {
        return Files.lines(Path.of(filename)).flatMap(line -> Stream.of(line.split(" ")[0]));
    }

    private static Stream<String> extractURLs(String filename) throws IOException {
        var scanner = new Scanner(Path.of(filename));
        return scanner.findAll(URL_REGEXP).map(mr -> mr.group(1));
    }

    private static Stream<Map.Entry<String, Long>> getTopOccurrences(Stream<String> stream, int limit) {
        return stream.collect(Collectors.groupingBy(Function.identity(),
                                                    Collectors.counting()))
                     .entrySet()
                     .stream()
                     .sorted(Comparator.comparing(Map.Entry::getValue,
                                                  Comparator.reverseOrder()))
                     .limit(limit);
    }
}