package com.imangazaliev.slugify;

import java.io.FileInputStream;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

public class Slugify {

    public enum Language {
        DEFAULT("default"),
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

        public String getLangFilePath() {
            return "rules/" + mLangFileName + ".properties";
        }
    }

    private final static Properties replacements = new Properties();

    private final static String EMPTY = "";
    private final static Pattern PATTERN_NORMALIZE_NON_ASCII = Pattern.compile("[^\\p{ASCII}]+");
    private final static Pattern PATTERN_NORMALIZE_SEPARATOR = Pattern.compile("[\\W\\s+]+");
    private final static Pattern PATTERN_NORMALIZE_TRIM_DASH = Pattern.compile("^-|-$");

    private final Map<String, String> customReplacements = new HashMap<>();

    private boolean underscoreSeparator = false;
    private boolean lowerCase = true;

    public Slugify() {
        if (replacements.isEmpty()) {
            replacements.putAll(loadLangReplacements(Language.DEFAULT));
        }
    }

    public Slugify addLanguageReplacements(Language language) {
        customReplacements.putAll((Map) loadLangReplacements(language));
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

    public void clearCustomReplacements() {
        customReplacements.clear();
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
        Map<String, String> customReplacements = getCustomReplacements();
        for (Entry<String, String> entry : customReplacements.entrySet()) {
            input = input.replace(entry.getKey(), entry.getValue());
        }

        return input;
    }

    private String builtInReplacements(String input) {
        for (Entry<Object, Object> e : replacements.entrySet()) {
            input = input.replace(e.getKey().toString(), e.getValue().toString());
        }

        return input;
    }

    private Properties loadLangReplacements(Language language) {
        String resourceFileName = language.getLangFilePath();
        try {
            Properties langReplacements = new Properties();
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            replacements.load(classloader.getResourceAsStream(resourceFileName));
            return langReplacements;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Resource '%s' not loaded!", resourceFileName), e);
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