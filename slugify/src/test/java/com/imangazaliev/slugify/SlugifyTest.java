package com.imangazaliev.slugify;

import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

import static org.junit.Assert.*;

public class SlugifyTest {

    private class Slug {

        private String original;
        private String slug;

        public Slug(String original, String slug) {
            this.original = original;
            this.slug = slug;
        }

        public String getOriginal() {
            return original;
        }

        public String getSlug() {
            return slug;
        }
    }

    private static final HashMap<Slugify.Language, Slug> slugs = new HashMap<>();

    @Before
    public void setup() throws UnsupportedEncodingException {
        slugs.put(Slugify.Language.DEFAULT, new Slug("Android Development for Beginners", "android-development-for-beginners"));
        slugs.put(Slugify.Language.ARABIC, new Slug("العَرَبِيَّة", "alaarabiya"));
        slugs.put(Slugify.Language.AUSTRIAN, new Slug("", ""));
        slugs.put(Slugify.Language.AZERBAIJANI, new Slug("", ""));
        slugs.put(Slugify.Language.BULGARIAN, new Slug("", ""));
        slugs.put(Slugify.Language.CROATIAN, new Slug("", ""));
        slugs.put(Slugify.Language.CZECH, new Slug("", ""));
        slugs.put(Slugify.Language.ESPERANTO, new Slug("", ""));
        slugs.put(Slugify.Language.FINNISH, new Slug("", ""));
        slugs.put(Slugify.Language.GEORGIAN, new Slug("", ""));
        slugs.put(Slugify.Language.GERMAN, new Slug("", ""));
        slugs.put(Slugify.Language.GREEK, new Slug("", ""));
        slugs.put(Slugify.Language.HINDI, new Slug("", ""));
        slugs.put(Slugify.Language.LATVIAN, new Slug("", ""));
        slugs.put(Slugify.Language.NORWEGIAN, new Slug("", ""));
        slugs.put(Slugify.Language.POLISH, new Slug("", ""));
        slugs.put(Slugify.Language.RUSSIAN, new Slug("Разработка мобильных приложений под Android", "razrabotka-mobilnyh-prilozheniy-pod-android"));
        slugs.put(Slugify.Language.SWEDISH, new Slug("", ""));
        slugs.put(Slugify.Language.TURKISH, new Slug("Android Uygulama Geliştirme", "android-uygulama-gelistirme"));
        slugs.put(Slugify.Language.UKRAINIAN, new Slug("Розробка мобільних додатків під Android", "rozrobka-mobilnih-dodatkiv-pid-android"));
        slugs.put(Slugify.Language.VIETNAMESE, new Slug("", ""));
    }

    @Test
    public void testSlugify() throws Exception {
        Slugify slugify = new Slugify();
        Slugify.Language[] languages = Slugify.Language.values();
        for (Slugify.Language language:languages) {
            slugify.clearCustomReplacements();
            slugify.addLanguageReplacements(language);
            Slug slug = slugs.get(language);
            assertEquals(slug.getSlug(), slugify.slugify(slug.getOriginal()));
        }

    }

    @Test
    public void testCustomReplacements() throws Exception {
        Slugify slugify = new Slugify();
        slugify.withCustomReplacement("&", "and");
        assertEquals("dogs-and-cats", slugify.slugify("Dogs & Cats"));
    }

    @Test
    public void testSlugCase() throws Exception {
        Slugify slugify = new Slugify();
        slugify.withLowerCase(false);
        assertEquals("Dogs-and-Cats", slugify.slugify("Dogs and Cats"));
        slugify.withLowerCase(true);
        assertEquals("dogs-and-cats", slugify.slugify("Dogs and Cats"));
    }

    @Test
    public void testUnderscoreSeparator() throws Exception {
        Slugify slugify = new Slugify();
        slugify.withUnderscoreSeparator(true);
        assertEquals("dogs_and_cats", slugify.slugify("Dogs and Cats"));
    }

    @Test
    public void testMultipleSpaces() throws Exception {
        Slugify slugify = new Slugify();
        assertEquals("dogs-and-cats", slugify.slugify("Dogs  and   Cats"));
    }

}