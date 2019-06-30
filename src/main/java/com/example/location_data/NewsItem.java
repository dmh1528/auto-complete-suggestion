package com.example.location_data;

import com.google.gson.Gson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

@Entity
@Table(name = "e_news_items")
public class NewsItem {

    @Transient
    @JsonIgnore
    private Gson gson = new Gson();

    @Id
    @Column(name = "pk")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Transient
    private String text;

    @Transient
    private String lang;

    @Transient
    private String lda17;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLda17() {
        return lda17;
    }

    public void setLda17(String lda17) {
        this.lda17 = lda17;
    }
}
