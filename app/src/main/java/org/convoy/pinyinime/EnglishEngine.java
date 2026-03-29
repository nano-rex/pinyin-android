package org.convoy.pinyinime;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class EnglishEngine {
    private static final String WORDS_ASSET = "english_words.txt";
    private static final int MAX_CANDIDATES = 32;

    private static final List<String> WORDS = new ArrayList<>();
    private static volatile boolean loaded;

    public EnglishEngine(Context context) {
        ensureLoaded(context.getApplicationContext().getAssets());
    }

    public List<String> getCandidates(String rawInput) {
        String input = normalize(rawInput);
        if (input.isEmpty()) {
            return Collections.emptyList();
        }

        LinkedHashSet<String> results = new LinkedHashSet<>();
        for (String word : WORDS) {
            if (!word.startsWith(input)) {
                continue;
            }
            results.add(word);
            if (results.size() >= MAX_CANDIDATES) {
                break;
            }
        }
        return new ArrayList<>(results);
    }

    private static void ensureLoaded(AssetManager assets) {
        if (loaded) {
            return;
        }
        synchronized (EnglishEngine.class) {
            if (loaded) {
                return;
            }
            WORDS.clear();
            try (InputStream stream = assets.open(WORDS_ASSET);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = normalize(line);
                    if (!word.isEmpty()) {
                        WORDS.add(word);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load english word asset", e);
            }
            loaded = true;
        }
    }

    private static String normalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = Character.toLowerCase(text.charAt(i));
            if ((ch >= 'a' && ch <= 'z') || ch == '\'') {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
