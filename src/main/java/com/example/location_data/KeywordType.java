package com.example.location_data;

public enum KeywordType {

    TAGS(2.0), TITLE(1.5), TEXT(1.0), FIRST_WORD(0.0);

    private KeywordType(double weight) {
        this.weight = weight;
    };
    public double getWeight() {
        return weight;
    }
    private double weight;
}
