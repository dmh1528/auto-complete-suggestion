package com.example.location_data.locNormV3;


public enum Language {
    ENGLISH("en"), HINDI("hi"), MARATHI("mr"), GUJARATI("gu"), BANGLA("bn"), PUNJABI("pa"), ORIYA("or"), TELUGU("te"), TAMIL("ta"), KANNADA("kn"), MALAYALAM("ml"), URDU("ur"), NEPALI("ne"), BHOJPURI("bh"), ALL("all");

    private String language;
    private int id;
    private Language(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return this.language;
    }

    public static Language getLanguage(String lang) {
        for (Language language: Language.values()) {
            if (language.getLanguage().equalsIgnoreCase(lang)) {
                return language;
            }
        }
        return null;
    }


    public static Language getLanguage(int lang) {
        for (Language language : Language.values()) {
            if (language.ordinal() == lang) {
                return language;
            }
        }
        return null;
    }

    public static Language getLanguageById(int id) {
        for (Language language : Language.values()) {
            if (language.getId() == id) {
                return language;
            }
        }
        return null;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

}

