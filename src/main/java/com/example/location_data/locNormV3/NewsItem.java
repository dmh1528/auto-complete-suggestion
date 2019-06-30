package com.example.location_data.locNormV3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import javax.persistence.*;
import java.util.*;

public class NewsItem {

	@Transient
	@JsonIgnore
	private Gson gson = new Gson();

	@Transient
	private String id;

	@Transient
	private List<String> topics = new ArrayList<>();

	@Transient
	private long categoryId;

	@Transient
	private List<String> curatedDigestTopic = new ArrayList<>();

	@Transient
	private String title;

	@Transient
	private String firstParagraph;

	@Transient
	private String firstWord;

	@Transient
	private String text;

	@Transient
	private Language language;

	@Transient
	private List<LocationNorm> locNormV3;

	@Transient
	private List<DuplicateCategory> duplicateCategories=new ArrayList<>();

	@Transient
	private String category;

	@Transient
	private List<String> rawTopics=new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public String getFirstParagraph() {
		return firstParagraph;
	}

	public void setFirstParagraph(String firstParagraph) {
		this.firstParagraph = firstParagraph;
	}

	public String getFirstWord() {
		return firstWord;
	}

	public List<String> getTopics() {

		return topics;
	}

	public void setTopics(List<String> topics) {

		this.topics = topics;
	}

	public void setFirstWord(String firstWord) {
		this.firstWord = firstWord;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Language getLanguage() {
		return language;
	}

	public String getCategory() {

		return category;
	}

	public void setCategory(String category) {

		this.category = category;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}


	public List<LocationNorm> getLocNormV3() {
		return locNormV3;
	}

	public void setLocNormV3(List<LocationNorm> locNormV3) {
		this.locNormV3 = locNormV3;
	}

	public List<DuplicateCategory> getDuplicateCategories() {
		return duplicateCategories;
	}

	public void setDuplicateCategories(List<DuplicateCategory> duplicateCategories) {
		this.duplicateCategories = duplicateCategories;
	}


	public List<String> getRawTopics() {
		return rawTopics;
	}

	public void setRawTopics(List<String> rawTopics) {
		this.rawTopics = rawTopics;
	}

	public List<String> getCuratedDigestTopic() {

		return curatedDigestTopic;
	}

	public void setCuratedDigestTopic(List<String> curatedDigestTopic) {

		this.curatedDigestTopic = curatedDigestTopic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
}


