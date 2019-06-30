/*
package com.example.location_data.locNormV3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.example.location_data.Language;
import com.example.location_data.LocationNorm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;

public class LocationNormV3Tagger {

	private static final Logger LOG = LoggerFactory.getLogger(LocationNormV3Tagger.class);

	private static final int case_not_valid = 0;
	private static final int case_valid_not_success = 1;
	private static final int case_valid_and_success = 2;

	private static Gson gson = new Gson();

	@Value("${locationid.map.file.path}")
	private String locationIdMapfilePath;

	@Value("${excluded.catPk.file.path}")
	private String excludedCatPkFilePath;

	@Value("${excluded.topicId.file.path}")
	private String excludedTopicIdFilePath;

	@Value("${location.category.pk.file.path}")
	private String locationCategoryPkFilePath;

	private Map<Language, Map<String, String>> languageMap = new HashMap<>();
	private Map<String, String> englishCityMap = new HashMap<>();
	private Map<String, String> englishStateMap = new HashMap<>();

	private Map<String, String> englishCityStateMap = new HashMap<>();
	private Set<String> cityStateCapitalSet = new HashSet<>();
	private Set<String> cityStateMustSet = new HashSet<>();
	private Map<String, Language> cityLanguageMustMap = new HashMap<>();
	private Set<String> cityMustNpCat = new HashSet<>();
	private Map<Long, String> npCatToCityMap = new HashMap<>();

	private List<String> excludedTopicIds;
	private List<Long> excludedCatPKs;


	public void init() throws Exception {

		excludedCatPKs = Files.readAllLines(Paths.get(excludedCatPkFilePath)).stream()
				.map(line -> Long.parseLong(line.trim())).collect(Collectors.toList());

		excludedTopicIds = Files.readAllLines(Paths.get(excludedTopicIdFilePath)).stream().map(line -> line.trim())
				.collect(Collectors.toList());

		// set specific cat to city map
		readNpCatToCityMappingFromFile();

		Scanner scanToMapLocation = new Scanner(new File(locationIdMapfilePath), "utf-8");

		*/
/*
		 * ignore the headers
		 *//*

		scanToMapLocation.nextLine();

		while (scanToMapLocation.hasNext()) {
			String line = scanToMapLocation.nextLine();
			saveValues(line);
		}

		LOG.info(" languageMap ::: " + gson.toJson(languageMap));
		LOG.info(" englishCityMap ::: " + gson.toJson(englishCityMap));
		LOG.info(" englishStateMap ::: " + gson.toJson(englishStateMap));
		LOG.info(" englishCityStateMap ::: " + gson.toJson(englishCityStateMap));
		LOG.info(" cityStateCapitalList ::: " + gson.toJson(cityStateCapitalSet));
		LOG.info(" excludedCatPKs ::: " + excludedCatPKs);
		LOG.info(" excludedTopicIds ::: " + excludedTopicIds);

		scanToMapLocation.close();
	}

	public boolean isEligibleForTagging(Language language) {
		return languageMap.containsKey(language);
	}

	public List<LocationNorm> tagLocation(NewsItem newsItem) {

		try {
			Map<String, String> locationMap = languageMap.get(newsItem.getLanguage());

			Set<Long> catIds = new HashSet<>();
			newsItem.getDuplicateCategories().forEach(obj -> catIds.add(new Long(obj.getCategoryId())));
			catIds.add(newsItem.getCategoryId());

			for (Long catId : catIds) {
				if (excludedCatPKs.contains(catId)) {
					LOG.error(newsItem.getId() + " - " + catId + " - Category Excluded !!!");
					return null;
				}
			}

			Set<String> newTopics = new HashSet<>(newsItem.getTopics());

			if (!newTopics.isEmpty()) {
				for (String topicId : newTopics) {
					if (excludedTopicIds.contains(topicId)) {
						LOG.error(topicId + " - TopicId Excluded !!!");
						return null;
					}
				}
			}

			if (locationMap == null) {
				LOG.error(newsItem.getLanguage().name() + " - Language not supported !!!");
				return null;
			}

			List<String> firstParaList = getLocationListInEng(newsItem.getFirstParagraph(), locationMap);
			List<String> firstWordList = getLocationListInEng(newsItem.getFirstWord(), locationMap);
			List<String> textList = getLocationListInEng(newsItem.getText(), locationMap);
			List<String> firstParaNotfirstWordList = getLocationListInEng(
					newsItem.getFirstParagraph().substring(newsItem.getFirstWord().length()), locationMap);

			Set<String> locationsInArticle = new HashSet<>(textList);

			LOG.info(" locationsInArticle ::: " + gson.toJson(locationsInArticle));

			Set<String> taggedLocations = isTaggedFromLocation(locationsInArticle, firstWordList,
					firstParaNotfirstWordList, newsItem.getCategory(), newsItem.getLanguage(),
					newsItem.getCategoryId());

			LOG.info(" taggedLocations ::: " + gson.toJson(taggedLocations));

			if (taggedLocations.isEmpty()) {
				LOG.info("No location tagging available");
				return null;
			}

			*/
/*
			 * Find out list of states from title and if its different from tagged location
			 * then ignore the tags.
			 * 
			 *//*


			List<String> titleList = getLocationListInEng(newsItem.getTitle(), locationMap);

			if (!titleList.isEmpty()) {
				Set<String> states = new HashSet<>();
				for (String loc : titleList) {
					for (Entry<String, String> entry : englishCityStateMap.entrySet()) {
						if (loc.equalsIgnoreCase(entry.getKey())) {
							states.add(entry.getValue());
							break;
						}
					}
				}

				*/
/* check tagged location contains a different state if so then ignore tags. *//*


				if (!states.isEmpty()) {
					for (String loc : taggedLocations) {
						for (Entry<String, String> entry : englishCityStateMap.entrySet()) {
							if (loc.equalsIgnoreCase(entry.getKey()) && !states.contains(entry.getValue())) {
								LOG.info(newsItem.getId()
										+ " - Ignoring location tagging because title contains different state. Title States :: "
										+ states + ", Wrong state :: " + entry.getKey() + " - " + entry.getValue());
								return null;
							}
						}
					}
				}
			}

			Set<String> firstParaSet = firstParaList.stream().filter(loc -> taggedLocations.contains(loc))
					.collect(Collectors.toSet());
			Set<String> firstWordSet = firstWordList.stream().filter(loc -> taggedLocations.contains(loc))
					.collect(Collectors.toSet());
			Set<String> textSet = textList.stream().filter(loc -> taggedLocations.contains(loc))
					.collect(Collectors.toSet());

			LOG.info(" NewsItem ::: " + newsItem.getId() + " Final firstParaWords ::: " + gson.toJson(firstParaSet)
					+ " Final firstWordWords ::: " + gson.toJson(firstWordSet) + " Final textSet ::: "
					+ gson.toJson(textSet) + " firstParaNotfirstWordList:: " + gson.toJson(firstParaNotfirstWordList));

			List<LocationNorm> locNormV3 = new ArrayList<>();

			// check if location is np-cat, tag it as np-cat
			LOG.info("Checking if np cat tagging for item:{}", newsItem);
			if (!CollectionUtils.isEmpty(npCatToCityMap)) {
				Set<String> locations = new HashSet<>();
				for (Long catId : catIds) {
					String location = npCatToCityMap.get(catId);
					if (location != null) {
						locations.add(location);
					}
					LOG.info("Location is np cat tagging for item:{}, categoryID:{}, locations:{}", newsItem.getId(),
							catId, location);
				}

				locNormV3.addAll(getLocationNormList(locations, LocationTaggedContext.NP_CAT.name()));
				LOG.info(" NewsItem ::: " + newsItem.getId() + " Language ::: " + newsItem.getLanguage()
						+ " Final locNormV3 ::: " + gson.toJson(locNormV3));
			}

			locNormV3.addAll(getLocationNormList(firstWordSet, LocationTaggedContext.FIRST_WORD.toString() + ","
					+ LocationTaggedContext.FIRST_PARAGRAPH.toString()));

			Set<String> filteredfirstParaSet = firstParaSet.stream().filter(word -> !firstWordSet.contains(word))
					.collect(Collectors.toSet());

			locNormV3.addAll(
					getLocationNormList(filteredfirstParaSet, LocationTaggedContext.FIRST_PARAGRAPH.toString()));

			Set<String> filteredTextSet = textSet.stream().filter(word -> !firstParaSet.contains(word))
					.collect(Collectors.toSet());

			locNormV3.addAll(getLocationNormList(filteredTextSet, LocationTaggedContext.TEXT.toString()));

			LOG.info(" NewsItem ::: " + newsItem.getId() + " Language ::: " + newsItem.getLanguage()
					+ " Final locNormV3 ::: " + gson.toJson(locNormV3));
			return locNormV3;

		} catch (Exception e) {
			LOG.error("Error while tagLocation :" + newsItem.getId(), e);
		}
		return null;
	}

	private enum ColumnCommonIndex {

		ID(1, 3), CAPITAL(22, -1), LANG(23, -1), STATE(24, -1), NPCAT(25, -1);

		int cityIndex;
		int stateIndex;

		private ColumnCommonIndex(int cityIndex, int stateIndex) {
			this.cityIndex = cityIndex;
			this.stateIndex = stateIndex;
		}
	}

	private enum ColumnLanguageIndex {

		ENGLISH(4, 5), HINDI(6, 7), TAMIL(8, 9), TELUGU(10, 11), KANNADA(12, 13), MALAYALAM(14, 15), GUJARATI(16,
				17), BANGLA(18, 19), MARATHI(20, 21);

		int cityIndex;
		int stateIndex;

		private ColumnLanguageIndex(int cityIndex, int stateIndex) {
			this.cityIndex = cityIndex;
			this.stateIndex = stateIndex;
		}
	}

	private List<LocationNorm> getLocationNormList(Set<String> location, String taggedFrom) {
		List<LocationNorm> locationIds = location.stream().map(locStr -> {
			LocationNorm locationNorm = new LocationNorm();
			String locationCityId = englishCityMap.get(locStr);
			if (locationCityId == null) {
				locationNorm.setState(englishStateMap.get(locStr));
			} else {
				locationNorm.setCity(locationCityId);
				String stateLocation = englishCityStateMap.get(locStr);
				locationNorm.setState(englishStateMap.get(stateLocation));
			}

			locationNorm.setCountry("India");
			locationNorm.setTaggedFrom(taggedFrom);
			return locationNorm;
		}).collect(Collectors.toList());

		return locationIds;
	}

	private List<String> getLocationListInEng(String text, Map<String, String> locationMap) {
		List<String> locationsList = new ArrayList<>();
		for (Map.Entry<String, String> locEntry : locationMap.entrySet()) {
			if (text.contains(locEntry.getKey())) {
				if (Pattern.compile("\\b" + locEntry.getKey() + "\\b", Pattern.UNICODE_CHARACTER_CLASS).matcher(text)
						.find()) {
					locationsList.add(locEntry.getValue());
				}
			}
		}
		return locationsList;
	}

	private String validate(String input) {
		if (StringUtils.isBlank(input)) {
			throw new RuntimeException("Invalid String");
		}
		return input.trim();
	}

	private void saveValues(String line) {
		String[] columns = line.split("\t");

		String engState = validate(columns[ColumnLanguageIndex.ENGLISH.stateIndex]);
		String engCity = validate(columns[ColumnLanguageIndex.ENGLISH.cityIndex]);

		englishCityMap.put(engCity, validate(columns[ColumnCommonIndex.ID.cityIndex]));
		englishStateMap.put(engState, validate(columns[ColumnCommonIndex.ID.stateIndex]));

		if (columns.length > ColumnCommonIndex.CAPITAL.cityIndex
				&& columns[ColumnCommonIndex.CAPITAL.cityIndex].trim().equalsIgnoreCase("YES")) {
			cityStateCapitalSet.add(engCity);
		}
		if (columns.length > ColumnCommonIndex.STATE.cityIndex
				&& columns[ColumnCommonIndex.STATE.cityIndex].trim().equalsIgnoreCase("TRUE")) {
			cityStateMustSet.add(engCity);
		}
		if (columns.length > ColumnCommonIndex.LANG.cityIndex
				&& !StringUtils.isBlank(columns[ColumnCommonIndex.LANG.cityIndex])) {
			cityLanguageMustMap.put(engCity,
					Language.valueOf(columns[ColumnCommonIndex.LANG.cityIndex].trim().toUpperCase()));
		}

		// set is np-cat tagged
		boolean isNpCat = setIsCityNpCat(columns, engCity);
		
		*/
/*
		 * adding state for both city and state
		 *//*

		englishCityStateMap.put(engCity, engState);
		englishCityStateMap.put(engState, engState);

		//if NP CAT true then cannot use text comparison.
		if(isNpCat) {
			return;
		}

		for (ColumnLanguageIndex index : ColumnLanguageIndex.values()) {

			Language language = Language.valueOf(index.name());
			Map<String, String> locationMap = languageMap.get(language);
			if (locationMap == null) {
				locationMap = new HashMap<>();
				languageMap.put(language, locationMap);
			}

			if (index.cityIndex >= columns.length || index.stateIndex >= columns.length) {
				LOG.info("Invalid Index");
				throw new RuntimeException("Invalid Index");
			}

			for (String value : validate(columns[index.cityIndex]).split(",")) {
				locationMap.put(validate(value), engCity);
			}

			for (String value : validate(columns[index.stateIndex]).split(",")) {
				locationMap.put(validate(value), engState);
			}

		}

	}

	private boolean setIsCityNpCat(String[] columns, String engCity) {
		if (columns.length > ColumnCommonIndex.NPCAT.cityIndex) {
			String value = columns[ColumnCommonIndex.NPCAT.cityIndex];
			if (StringUtils.isNotBlank(value) && value.trim().equalsIgnoreCase(Boolean.TRUE.toString())) {
				cityMustNpCat.add(engCity);
				return true;
			}
		}
		return false;
	}

	public Set<String> isTaggedFromLocation(Set<String> locationsInArticle, List<String> firstWordList,
			List<String> firstParaNotfirstWordList, String categoryName, Language itemLanguage, long categoryId) {

		Set<String> taggedLoc = new HashSet<>();

		locationsInArticle.forEach(loc -> {
			boolean isNpCatName = loc.equalsIgnoreCase(categoryName);
			boolean isStateCapital = cityStateCapitalSet.contains(loc);
			boolean isFirstWord = firstWordList.contains(loc);
			boolean isOnlyFirstPara = firstParaNotfirstWordList.contains(loc);
			boolean notFirstParaOrWord = !(firstParaNotfirstWordList.contains(loc) || firstWordList.contains(loc))
					? Boolean.TRUE
					: Boolean.FALSE;

			boolean isTagged = false;
			LOG.info(" firstWordList ::: " + gson.toJson(firstWordList));

			LOG.info(" firstParaList ::: " + gson.toJson(firstParaNotfirstWordList));

			LOG.info(loc + " => isNpCatName, isStateCapital, isFirstWord, isOnlyFirstPara, notFirstParaOrWord :: "
					+ isNpCatName + " :: " + isStateCapital + " :: " + isFirstWord + " :: " + isOnlyFirstPara + " :: "
					+ notFirstParaOrWord);

			if (isOnlyFirstPara == Boolean.TRUE) {
				isTagged = true;
			} else {
				if (notFirstParaOrWord == Boolean.TRUE) {
					isTagged = true;
				} else if (isStateCapital == Boolean.TRUE && isNpCatName == Boolean.TRUE) {
					isTagged = true;
				} else if (isFirstWord == Boolean.TRUE && isStateCapital == Boolean.FALSE
						&& isNpCatName == Boolean.FALSE) {
					isTagged = true;
				}
			}

			if (isTagged) {

				int languageCondition = case_not_valid;
				Language language = cityLanguageMustMap.get(loc);
				if (language != null) {
					languageCondition = case_valid_not_success;
					if (language == itemLanguage) {
						languageCondition = case_valid_and_success;
					}
				}

				int stateCondition = case_not_valid;
				if (cityStateMustSet.contains(loc)) {
					stateCondition = case_valid_not_success;
					if (locationsInArticle.contains(englishCityStateMap.get(loc))) {
						stateCondition = case_valid_and_success;
					}
				}

				if (languageCondition != case_valid_not_success && stateCondition != case_valid_not_success) {
					taggedLoc.add(loc);
				} else {
					LOG.info("languageCondition: " + languageCondition + ", stateCondition: " + stateCondition);
				}
			}
		});

		return taggedLoc;
	}

	private enum LocationTaggedContext {
		TEXT, FIRST_PARAGRAPH, FIRST_WORD, NP_CAT
	}

	private void readNpCatToCityMappingFromFile() throws Exception {
		try {
			Files.readAllLines(Paths.get(locationCategoryPkFilePath)).stream().skip(1).forEach(line -> {
				String[] columns = line.split("\t");
				if (columns.length == 2) {
					npCatToCityMap.put(Long.parseLong(columns[0].trim()), columns[1].trim());
				}
			});
		} catch (Exception e) {
			LOG.error("Error reading file:{}", locationCategoryPkFilePath, e);
			throw e;
		}
	}
}*/
