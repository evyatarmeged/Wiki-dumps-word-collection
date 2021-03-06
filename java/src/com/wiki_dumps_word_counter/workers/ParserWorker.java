package com.wiki_dumps_word_counter.workers;

import com.wiki_dumps_word_counter.shared_state.ArticleQueue;
import com.wiki_dumps_word_counter.shared_state.FileNameQueue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ParserWorker implements Runnable {
    // TODO: Implement a method for stripping connection words / plural

    private static final String CHARSET = "אבגדהוזחטיכלמנסעפצקרשתםףץןך";
    private static final Pattern PATTERN = Pattern.compile("[^"+CHARSET+"]");
    private StringBuilder data;
    private Stream<String> lines;
    private ArticleQueue articleQueue = ArticleQueue.getInstance();
    private FileNameQueue fileNameQueue = FileNameQueue.getInstance();


    private String stripNonHebrewChars(String text) {
        // Strip all non hebrew characters
        Matcher matcher = PATTERN.matcher(text);
        text = matcher.replaceAll(" ");
        return text.trim();
    }

    private void enqueueParsedArticle() throws IOException {
        // Read file contents, remove unwanted chars and push to ArticleQueue
        while (!this.fileNameQueue.isEmpty()) {
            data = new StringBuilder();
            lines = Files.lines(this.fileNameQueue.pop());
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();
            this.articleQueue.insert(this.stripNonHebrewChars(data.toString()));
        }
    }

    @Override
    public void run() {
        try {
            this.enqueueParsedArticle();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
