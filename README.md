# Slugify
[ ![Download](https://api.bintray.com/packages/imangazaliev/maven/slugify/images/download.svg) ](https://bintray.com/imangazaliev/maven/slugify/_latestVersion)

Converts a string to a slug

# Setup

```gradle
compile 'com.github.imangazalievm:slugify:1.0.0'
```
# Usage

Generate a slug:

```gradle
Slugify slugify = new Slugify();
slugify.slugify("Android Development for Beginners"); //returns "android-development-for-beginners"
```

You can also change the separator to underscore separator:

```java
slugify.withUnderscoreSeparator(true);
```
# Custom replacements

