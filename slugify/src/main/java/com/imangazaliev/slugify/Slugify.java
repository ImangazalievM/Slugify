package com.imangazaliev.slugify;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Slugify {

    public enum Language {
        ARABIC("arabic"),
        AUSTRIAN("austrian"),
        AZERBAIJANI("azerbaijani"),
        BULGARIAN("bulgarian"),
        CROATIAN("croatian"),
        CZECH("czech"),
        ESPERANTO("esperanto"),
        FINNISH("finnish"),
        GEORGIAN("georgian"),
        GERMAN("german"),
        GREEK("greek"),
        HINDI("hindi"),
        LATVIAN("latvian"),
        NORWEGIAN("norwegian"),
        POLISH("polish"),
        RUSSIAN("russian"),
        SWEDISH("swedish"),
        TURKISH("turkish"),
        UKRAINIAN("ukrainian"),
        VIETNAMESE("vietnamese");

        private String mLangFileName;

        Language(String langFileName) {
            this.mLangFileName = langFileName;
        }

        public String getLangFileName() {
            return mLangFileName;
        }
    }

    private final static String EMPTY = "";
    private final static Pattern PATTERN_NORMALIZE_NON_ASCII = Pattern.compile("[^\\p{ASCII}]+");
    private final static Pattern PATTERN_NORMALIZE_SEPARATOR = Pattern.compile("[\\W\\s+]+");
    private final static Pattern PATTERN_NORMALIZE_TRIM_DASH = Pattern.compile("^-|-$");

    private final static Map<String, String> replacements = new LinkedHashMap<>();
    private final Map<Language, Map<String, String>> languageRuleSets = new LinkedHashMap<>();
    private final Map<String, String> customReplacements = new HashMap<>();

    private boolean underscoreSeparator = false;
    private boolean lowerCase = true;

    public Slugify() {
        if (replacements.isEmpty()) {
            replacements.putAll(loadReplacementRuleSet("default"));
        }
    }

    public Slugify setLanguageRuleSet(Language language) {
        languageRuleSets.clear();
        if (!languageRuleSets.containsKey(language)) {
            Map languageRuleset = loadReplacementRuleSet(language.getLangFileName());
            languageRuleSets.put(language, languageRuleset);
        }
        return this;
    }

    public Slugify setLanguageRuleSets(Language... languages) {
        for (Language language : languages) {
            if (!languageRuleSets.containsKey(language)) {
                Map languageRuleset = loadReplacementRuleSet(language.getLangFileName());
                languageRuleSets.put(language, languageRuleset);
            }
        }
        return this;
    }

    public Slugify withCustomReplacement(final String from, final String to) {
        customReplacements.put(from, to);
        return this;
    }

    public Slugify withCustomReplacements(final Map<String, String> customReplacements) {
        this.customReplacements.putAll(customReplacements);
        return this;
    }

    public Slugify withUnderscoreSeparator(final boolean underscoreSeparator) {
        this.underscoreSeparator = underscoreSeparator;
        return this;
    }

    public Slugify withLowerCase(final boolean lowerCase) {
        this.lowerCase = lowerCase;
        return this;
    }

    public String slugify(final String text) {
        String input = text;
        if (isNullOrBlank(input)) {
            return EMPTY;
        }

        input = input.trim();
        input = customReplacements(input);
        input = languageReplacements(input);
        input = builtInReplacements(input);
        input = normalize(input);

        if (lowerCase) {
            input = input.toLowerCase();
        }

        return input;
    }

    public Map<String, String> getCustomReplacements() {
        return customReplacements;
    }

    private String customReplacements(String input) {
        for (Entry<String, String> replacementEntry : customReplacements.entrySet()) {
            input = input.replace(replacementEntry.getKey(), replacementEntry.getValue());
        }

        return input;
    }

    private String languageReplacements(String input) {
        for (Entry<Language, Map<String, String>> languageEntry : languageRuleSets.entrySet()) {
            for (Entry<String, String> replacementEntry : languageEntry.getValue().entrySet()) {
                input = input.replace(replacementEntry.getKey(), replacementEntry.getValue());
            }
        }
        return input;
    }

    private String builtInReplacements(String input) {
        for (Entry<String, String> e : replacements.entrySet()) {
            input = input.replace(e.getKey(), e.getValue());
        }
        return input;
    }

    private Map<String, String> loadReplacementRuleSet(String langFileName) {
        String resourceFilePath = "/rules/" + langFileName + ".json";
        try {
            Map<String, String> langReplacements = new LinkedHashMap<>();
            InputStream stream = Slugify.class.getResourceAsStream(resourceFilePath);
            String langRuleSetJson = new Scanner(stream, Charset.defaultCharset().name()).useDelimiter("\\A").next();

            JSONObject langRuleSet = new JSONObject(langRuleSetJson);
            Iterator<String> iter = langRuleSet.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = langRuleSet.getString(key);
                langReplacements.put(key, value);
            }

            return langReplacements;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Resource '%s' not loaded!", resourceFilePath), e);
        }
    }


    private static boolean isNullOrBlank(final String string) {
        return string == null || string.trim().isEmpty();
    }

    private String normalize(final String input) {
        String text = Normalizer.normalize(input, Normalizer.Form.NFKD);
        text = PATTERN_NORMALIZE_NON_ASCII.matcher(text).replaceAll(EMPTY);
        text = PATTERN_NORMALIZE_SEPARATOR.matcher(text).replaceAll(underscoreSeparator ? "_" : "-");
        text = PATTERN_NORMALIZE_TRIM_DASH.matcher(text).replaceAll(EMPTY);

        return text;
    }
}