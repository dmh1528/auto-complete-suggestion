/*
package com.newshunt.content.processor.content.quality.utils;

import static com.newshunt.content.processor.commons.Constants.ContentPersister.DEFAULT_UUID_NAMESPACE;
import static com.newshunt.content.processor.commons.Constants.ContentPersister.NEWSHUNT_STORY_CONTENT_QUALITY_FACET_CONTENT_QUALITY_ATTR_NAME;
import static com.newshunt.content.processor.commons.Constants.ContentPersister.NEWSHUNT_STORY_CONTENT_QUALITY_FACET_ID_ATTR_NAME;
import static com.newshunt.content.processor.commons.Constants.ContentPersister.NEWSHUNT_STORY_CONTENT_QUALITY_FACET_NAME;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.AVG_INVERSE_QUALITY_INDEX;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.BAD_TEXT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.CATEGORY;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.CONTENT_QUALITY_INDEX;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.CT_PK;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.ID;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.INVERSE_QUALITY_INDEX;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_BAD_IMAGE_REFERENCE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_BAD_VIDEO_REFERENCE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_CONTENT_SHORT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_ICON_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_IMAGE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_INSTAGRAM_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_MULTI_IMAGE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_PARAGRAPH_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_PUBLISHER_DATE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_TITLE_LONG;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_TITLE_SHORT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_TWEET_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.IS_VIDEO_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.ITEM_PK;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.MAX_INVERSE_QUALITY_INDEX;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.MULTI_IMAGE_OVERFLOW;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.NEWSPAPER;
import static com.newshunt.content.processor.commons.Constants.ContentQuality.NP_PK;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.BAD_IMAGE_REF_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.BAD_VIDEO_REF_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.COMPUTED_QUALITY_SCORE;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.CONTENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.CONTENT_QUALITY_SCORE;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.COUNT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.ICON_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.IMAGE_OVERFLOW;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.IMAGE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.INSTAGRAM;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.INVERSE_QUALITY_SCORE;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.IS_LONG;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.IS_SHORT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.JUNK_TEXT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.LENGTH;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.MEDIA;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.MULTI_IMAGE;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.OVERALL;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.PARAGRAPH;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.PUBLISHER_DATE_PRESENT;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.TITLE;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.TWEETS;
import static com.newshunt.content.processor.commons.Constants.ContentQualityJson.VIDEO_PRESENT;
import static com.newshunt.content.processor.commons.Constants.HttpFeedFabricator.END_OF_STORY;
import static com.newshunt.content.processor.commons.Constants.NewsDataFields.ELEMENT_TEXT_20_CHUNK_1;
import static com.newshunt.content.processor.commons.Constants.NewsDataFields.ELEMENT_TEXT_20_CHUNK_2;
import static com.newshunt.content.processor.commons.Constants.NewsDataFields.ELEMENT_TITLE;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.newshunt.commons.CachedProperties;
import com.newshunt.content.commons.SpringApplicationContext;
import com.newshunt.content.model.ContentTypes;
import com.newshunt.content.model.NHModelFacet;
import com.newshunt.content.model.NewsData;
import com.newshunt.content.model.database.NewsItem;
import com.newshunt.content.model.util.NameBasedUUIDGenerator;
import com.newshunt.content.model.util.ObjectMapperPool;
import com.newshunt.content.processor.commons.FetchUtils;
import com.newshunt.content.processor.commons.FileUtils;
import com.newshunt.content.processor.content.quality.vo.CategoryQuality;
import com.newshunt.content.processor.content.quality.vo.ContentQltTypConfig;
import com.newshunt.content.processor.content.quality.vo.ContentQuality;
import com.newshunt.content.processor.content.quality.vo.ContentQualityConfig;
import com.newshunt.content.processor.content.quality.vo.ContentQualityJSONConfig;
import com.newshunt.content.processor.content.quality.vo.ContentTypeConfig;
import com.newshunt.content.processor.content.quality.vo.GridContentQualityConfig;
import com.newshunt.content.processor.content.quality.vo.ScoreGrid;
import com.newshunt.content.store.dao.cassandra.Content;

public class ContentQualityUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContentQualityUtils.class);
    private static CachedProperties properties = (CachedProperties) SpringApplicationContext.getBean(CachedProperties.class);
    private static ObjectMapperPool mapperPool = ObjectMapperPool.getInstance();

    private static String BAD_TEXT_STRING;
    private static String BAD_VIDEO_STRING;
    private static String BAD_IMAGE_STRING;

    private static final int SENTENCES_PER_PARAGRAPH = properties.getInt("content.quality.sentences.per.paragraph");
    private static final int MAX_MULTI_IMAGES = properties.getInt("content.quality.max.multi.images");
    private static final long MIN_CONTENT_LENGTH_VIDEO = properties.getLong("content.quality.min.content.length.video");
    private static final long MIN_CONTENT_LENGTH = properties.getLong("content.quality.min.content.length");

    private static final String QUALITY_CONFIG_FILE_PATH = "content.quality.config.file.path";
    private static final String QUALITY_GRID_CONFIG_FILE_PATH = "content.quality.grid.config.file.path";

    */
/*** Index for inverse quality index ****//*

    private static int titleQualityIndex = 20;
    private static int contentSizeIndex = 13;
    private static int newsIconIndex = 10;
    private static int primaryMediaIndex = 9;
    private static int publisherDateIndex = 8;
    private static int badContentIndex = 3;
    private static int badVideoReferenceIndex = 3;
    private static int badImageReferenceIndex = 3;
    private static int paragraphAbsentIndex = 15;
    private static int multiImageIndex = 6;
    private static int richMediaIndex = 4;

    private static Map<String , ContentQualityConfig> qualityConfig = new HashMap<>();

    private static Map<String , ContentTypeConfig> contentTypeConfig = new HashMap<>();

    private static Map<String , TreeMap<Integer, TreeMap<Integer, Integer>>> gridContentQualityConfig = new HashMap<>();

    static{
        try {
            getProperties(properties);
            readContentQualityConfigFromFile(properties.getString(QUALITY_CONFIG_FILE_PATH));
            readContentQualityGridConfigFromFile(properties.getString(QUALITY_GRID_CONFIG_FILE_PATH));
        } catch (Exception e) {
            LOG.error("Exception while reading content quality / content type config file.", e);
        }
    }
    */
/*******//*


    public static ContentQuality getContentQuality(NewsData newsData,
                                                   NewsItem newsItem,
                                                   int tweetCount,
                                                   int instamediaCount,
                                                   String npName,
                                                   String ctName,
                                                   ContentTypes contentType,
                                                   ContentQualityConfig qualityConfig,
                                                   int language,
                                                   String normalizedNewsText,
                                                   int wordCount) {

        ContentQuality contentQuality = new ContentQuality();

        String[] badText = BAD_TEXT_STRING.toLowerCase().split("##");
        String[] badVideoReference = BAD_VIDEO_STRING.toLowerCase().split("##");
        String[] badImageReference = BAD_IMAGE_STRING.toLowerCase().split("##");

        contentQuality.setItemPk(newsItem.getNewsItemPkey());
        contentQuality.setCtPk(newsItem.getCategoryPkey());
        contentQuality.setCtName(ctName);
        contentQuality.setNpPk(newsItem.getNewsPaperPkey());
        contentQuality.setNpName(npName);

        */
/*** Normalizing news text ***//*


        String text20 = newsData.get(ELEMENT_TEXT_20_CHUNK_1);
        if (!StringUtils.isEmpty(text20) && !StringUtils.isEmpty(newsData.get(ELEMENT_TEXT_20_CHUNK_2))) {
            text20 = text20 + " " + newsData.get(ELEMENT_TEXT_20_CHUNK_2);
        }
        String news_HtmlText;
        if (StringUtils.isEmpty(text20)) {
            news_HtmlText = "";
        } else {
            news_HtmlText = text20.replaceAll(END_OF_STORY, "");
        }
        String newsHtmlText = news_HtmlText.toLowerCase();

        contentQuality.setWordCount(wordCount);

        String titleAndText = newsData.get(ELEMENT_TITLE) + " " + normalizedNewsText;

        int totalQualityParameters = 0; // Add this while providing measurable
        // quality parameters
        int currentQualityParameters = 0;
        long inverseQualityIndex = 0;

        */
/* Test forMulti-Image and Image overflow quality score *//*

        int multiImagecount = StringUtils.countMatches(news_HtmlText, "##REPCDN-RICH-NEW##");
        boolean multiImagePresent = false;
        boolean multiImageOverflow = false;
        totalQualityParameters += multiImageIndex;
        if (multiImagecount > 0) {
            multiImagePresent = true;
            if (multiImagecount > MAX_MULTI_IMAGES) {
                inverseQualityIndex = inverseQualityIndex + multiImageIndex;
                multiImageOverflow = true;
            } else {
                multiImageOverflow = false;
                currentQualityParameters += multiImageIndex;
            }
        } else {
            multiImagePresent = false;
        }

        contentQuality.setMultiImagecount(multiImagecount);
        contentQuality.setMultiImagePresent(multiImagePresent);
        contentQuality.setMultiImageOverflow(multiImageOverflow);

        */
/* End of Multi-Image and Image overflow quality score *//*


        */
/* Test for News Icon *//*

        totalQualityParameters += newsIconIndex;
        boolean iconPresent = false;
        if (StringUtils.isEmpty(newsItem.getIconUrl())) {
            iconPresent = false;
            inverseQualityIndex = inverseQualityIndex + newsIconIndex;
        } else {
            iconPresent = true;
            currentQualityParameters += newsIconIndex;
        }
        contentQuality.setIconPresent(iconPresent);
        */
/* End of Test News Icon *//*


        */
/* Test for title length too large or too small *//*

        totalQualityParameters += titleQualityIndex;
        int titleLength = newsItem.getTitleFont().length();
        boolean titleShort = false;
        boolean titleLong = false;
        if (titleLength < qualityConfig.getTitleMinLength()) {
            titleShort = true;
            inverseQualityIndex = inverseQualityIndex + titleQualityIndex;
        } else {
            titleShort = false;
        }

        if (titleLength > qualityConfig.getTitleMaxLength()) {
            titleLong = true;
            inverseQualityIndex = inverseQualityIndex + titleQualityIndex;
        } else {
            titleLong = false;
        }
        if (!titleShort && !titleLong) {
            currentQualityParameters += titleQualityIndex;
        }
        contentQuality.setTitleLength(titleLength);
        contentQuality.setTitleLong(titleLong);
        contentQuality.setTitleShort(titleShort);
        */
/* End of Test for title length too short or too small *//*


        */
/* Check for embedded twitter and instagram media *//*

        totalQualityParameters += richMediaIndex;
        if (tweetCount > 0 || instamediaCount > 0) {
            currentQualityParameters += richMediaIndex;
        }
        contentQuality.setTweetCount(tweetCount);
        contentQuality.setTweetPresent(tweetCount <= 0 ? false : true);
        contentQuality.setInstagramPresent(instamediaCount <= 0 ? false : true);
        contentQuality.setInstamediaCount(instamediaCount);
        */
/* End of Check for embedded twitter and instagram media *//*


        */
/* Check for publisher date *//*

        totalQualityParameters += publisherDateIndex;
        boolean publisherDatePresent = false;
        if (!StringUtils.isEmpty(newsItem.getPubModifiedDate()) || !StringUtils.isEmpty(newsItem.getPubCreatedDate())) {
            publisherDatePresent = true;
            currentQualityParameters += publisherDateIndex;
        } else {
            publisherDatePresent = false;
            inverseQualityIndex = inverseQualityIndex + publisherDateIndex;
        }
        contentQuality.setPublisherDatePresent(publisherDatePresent);
        */
/* End of Check for publisher date *//*


        long minContentLength = 0;

        */
/* Check for video content *//*

        boolean imagePresent = false;
        boolean videoPresent = false;
        if (!StringUtils.isEmpty(newsItem.getVideoUrl())) {
            videoPresent = true;
            // minContentLength = MIN_CONTENT_LENGTH_VIDEO;
        } else {
            videoPresent = false;
            // minContentLength = MIN_CONTENT_LENGTH;
        }
        */
/* End of Check for video content *//*


        if (videoPresent || (contentQuality.isTweetPresent() && tweetCount >= 3) ||
                (contentQuality.isInstagramPresent() && instamediaCount >= 3) ||
                (multiImagePresent && multiImagecount >= 3)) {
            // Gallery type of content found
            minContentLength = MIN_CONTENT_LENGTH_VIDEO;
        } else {
            minContentLength = MIN_CONTENT_LENGTH;
        }

        */
/* Check if newsImage is present *//*

        if (StringUtils.isEmpty(newsItem.getImageUrl())) {
            imagePresent = false;
        } else {
            imagePresent = true;
        }
        contentQuality.setImagePresent(imagePresent);
        contentQuality.setVideoPresent(videoPresent);
        */
/* End of Check if newsImage is present *//*


        */
/*
         * Check for primary media presence (If both image and video are not
         * present the deduce score)
         *//*

        totalQualityParameters += primaryMediaIndex;
        if (!videoPresent && !imagePresent) {
            inverseQualityIndex = inverseQualityIndex + primaryMediaIndex;
        } else {
            currentQualityParameters += primaryMediaIndex;
        }
        */
/* End of check *//*


        */
/* Check for content length *//*

        totalQualityParameters += contentSizeIndex;
        boolean contentShort;
        long contentLength = normalizedNewsText.length();
        if (minContentLength > contentLength) {
            contentShort = true;
            inverseQualityIndex = inverseQualityIndex + contentSizeIndex;
        } else {
            currentQualityParameters += contentSizeIndex;
            contentShort = false;
        }
        contentQuality.setContentShort(contentShort);
        contentQuality.setContentLength(contentLength);
        */
/* End of check *//*


        */
/* Check for presence of bad text *//*

        totalQualityParameters += badContentIndex;
        List<String> badContent = new ArrayList<String>();
        for (int i = 0; i < badText.length; i++) {
            if (normalizedNewsText.contains(badText[i])) {
                badContent.add(badText[i]);
                inverseQualityIndex = inverseQualityIndex + badContentIndex;
            }

        }
        if (badContent.isEmpty()) {
            currentQualityParameters += badContentIndex;
        }
        contentQuality.setBadContent(badContent);
        */
/* End of Check *//*


        */
/* Check for presence of bad video reference *//*

        boolean badVideoReferencePresent = false;
        totalQualityParameters += badVideoReferenceIndex;
        for (int i = 0; i < badVideoReference.length; i++) {
            if (titleAndText.contains(badVideoReference[i]) && !videoPresent) {
                badVideoReferencePresent = true;
                inverseQualityIndex = inverseQualityIndex + (badVideoReferenceIndex * badVideoReferenceIndex);
                break;
            }
        }
        if (!badVideoReferencePresent) {
            currentQualityParameters += badVideoReferenceIndex;
        }
        contentQuality.setBadVideoReferencePresent(badVideoReferencePresent);
        */
/* End of check *//*


        */
/* Check for presence of bad image reference *//*

        boolean badImageReferencePresent = false;
        totalQualityParameters += badImageReferenceIndex;
        for (int i = 0; i < badImageReference.length; i++) {
            if (titleAndText.contains(badImageReference[i]) && (!imagePresent && !multiImagePresent && instamediaCount <= 0)) {
                badImageReferencePresent = true;
                inverseQualityIndex = inverseQualityIndex + (badImageReferenceIndex * badImageReferenceIndex);
                break;
            }
        }
        if (!badImageReferencePresent) {
            currentQualityParameters += badImageReferenceIndex;
        }
        contentQuality.setBadImageReferencePresent(badImageReferencePresent);
        */
/* End of check *//*


        */
/* Check for presence of paragraphs *//*

        totalQualityParameters += paragraphAbsentIndex;
        int totalSentences = countSentences(news_HtmlText);
        int totalExpectedParagraphs = totalSentences / SENTENCES_PER_PARAGRAPH;
        int paragraphCount =
                StringUtils.countMatches(newsHtmlText, "<p>") +
                        StringUtils.countMatches(newsHtmlText, "<br /><br />") +
                        StringUtils.countMatches(newsHtmlText, "<br><br>");
        boolean paragraphPresent;
        if (totalExpectedParagraphs > 2)// Removed one paragraph for balancing
            // purpose
            totalExpectedParagraphs--;
        if (totalExpectedParagraphs > paragraphCount) {
            paragraphPresent = false;
            inverseQualityIndex = inverseQualityIndex + paragraphAbsentIndex;
        } else {
            paragraphPresent = true;
            currentQualityParameters += paragraphAbsentIndex;
        }
        contentQuality.setParagraphCount(paragraphCount);
        contentQuality.setParagraphPresent(paragraphPresent);
        */
/* End of check *//*


        long contentQualityScore = (currentQualityParameters * 100) / totalQualityParameters;
        // double aggregateQualityScore;
        // if (publisherDatePresent) {
        // aggregateQualityScore = contentQualityScore / (Math.sqrt((1 +
        // inverseQualityIndex)));
        // } else {
        // aggregateQualityScore = contentQualityScore / (Math.sqrt((1 +
        // inverseQualityIndex - publisherDateIndex)));
        // }
        contentQuality.setContentQualityScore(contentQualityScore);
        contentQuality.setInverseQualityIndex(inverseQualityIndex);
        // contentQuality.setAggregateQualityScore(aggregateQualityScore);

        int imageCount =
                (contentQuality.isImagePresent() ? 1 : 0) + contentQuality.getMultiImagecount() +
                        contentQuality.getInstamediaCount() + contentQuality.getTweetCount();

        double aggregateQualityScore = getGridBasedQualityScore(contentQuality, FetchUtils.getLangEngForLangIndex(language).toUpperCase(), contentType.toString(), contentQuality.getWordCount() , imageCount);
        if(aggregateQualityScore == 0.0){
            aggregateQualityScore = getNewQualityScore(contentQuality, contentType, qualityConfig, language);
        }

        contentQuality.setAggregateQualityScore(aggregateQualityScore);

        return contentQuality;
    }

    private static int countSentences(String target) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(new Locale("en", "US"));
        iterator.setText(target.trim());
        int boundary = iterator.first();
        int initial = 0;
        int count = 0;
        boolean isUnderQuotes = false;
        int nextQuoteSubstrIndex = 0;
        while (boundary != BreakIterator.DONE) {
            String str = target.substring(initial, boundary).trim();
            Pattern p = Pattern.compile("(?i).*(?<=Mr|Dr|Miss|mrs|Ms)[\\.]\\s*");
            Pattern abv = Pattern.compile(".*(?<=[A-Z]{1}\\.)\\s+[A-Z]{1}\\S+\\.$");
            Pattern single = Pattern.compile(".*(?<=[A-Z]{1})\\.$");
            Pattern quotes = Pattern.compile(".*[\u201C\u201D\u05F4\u02BA\u030B\u030E\u2033\u3003].*");
            Matcher m = p.matcher(str);
            Matcher singleMatch = single.matcher(str);
            String quoteStr = target.substring(nextQuoteSubstrIndex, boundary).trim();
            Matcher quotesMatch = quotes.matcher(quoteStr);
            if (quotesMatch.matches()) {
                isUnderQuotes = !isUnderQuotes;
            }
            if (boundary > initial &&
                    (!singleMatch.matches() && (!m.matches() || abv.matcher(str).matches()) && !isUnderQuotes)) {
                initial = boundary;
                count++;
            }
            nextQuoteSubstrIndex = boundary;
            boundary = iterator.next();
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    public static ContentQuality getContentQuality(Document contentQualityDoc) {
        ContentQuality contentQuality = new ContentQuality();
        for (String key : contentQualityDoc.keySet()) {
            switch (key) {
                case ITEM_PK:
                    contentQuality.setItemPk(contentQualityDoc.getLong(ITEM_PK));
                case CONTENT_QUALITY_INDEX:
                    contentQuality.setContentQualityScore(contentQualityDoc.getLong(CONTENT_QUALITY_INDEX));
                case INVERSE_QUALITY_INDEX:
                    contentQuality.setInverseQualityIndex(contentQualityDoc.getLong(INVERSE_QUALITY_INDEX));
                case NP_PK:
                    contentQuality.setNpPk(contentQualityDoc.getLong(NP_PK));
                case CT_PK:
                    contentQuality.setCtPk(contentQualityDoc.getLong(CT_PK));
                case NEWSPAPER:
                    contentQuality.setNpName(contentQualityDoc.getString(NEWSPAPER));
                case CATEGORY:
                    contentQuality.setCtName(contentQualityDoc.getString(CATEGORY));
                case IS_TITLE_SHORT:
                    contentQuality.setTitleShort(contentQualityDoc.getBoolean(IS_TITLE_SHORT));
                case IS_TITLE_LONG:
                    contentQuality.setTitleLong(contentQualityDoc.getBoolean(IS_TITLE_LONG));
                case IS_CONTENT_SHORT:
                    contentQuality.setContentShort(contentQualityDoc.getBoolean(IS_CONTENT_SHORT));
                case IS_PUBLISHER_DATE_PRESENT:
                    contentQuality.setPublisherDatePresent(contentQualityDoc.getBoolean(IS_PUBLISHER_DATE_PRESENT));
                case IS_ICON_PRESENT:
                    contentQuality.setIconPresent(contentQualityDoc.getBoolean(IS_ICON_PRESENT));
                case IS_IMAGE_PRESENT:
                    contentQuality.setImagePresent(contentQualityDoc.getBoolean(IS_IMAGE_PRESENT));
                case IS_MULTI_IMAGE_PRESENT:
                    contentQuality.setMultiImagePresent(contentQualityDoc.getBoolean(IS_MULTI_IMAGE_PRESENT));
                case IS_TWEET_PRESENT:
                    contentQuality.setTweetPresent(contentQualityDoc.getBoolean(IS_TWEET_PRESENT));
                case IS_INSTAGRAM_PRESENT:
                    contentQuality.setInstagramPresent(contentQualityDoc.getBoolean(IS_INSTAGRAM_PRESENT));
                case IS_VIDEO_PRESENT:
                    contentQuality.setVideoPresent(contentQualityDoc.getBoolean(IS_VIDEO_PRESENT));
                case IS_PARAGRAPH_PRESENT:
                    contentQuality.setParagraphPresent(contentQualityDoc.getBoolean(IS_PARAGRAPH_PRESENT));
                case IS_BAD_IMAGE_REFERENCE_PRESENT:
                    contentQuality.setBadImageReferencePresent(contentQualityDoc.getBoolean(IS_BAD_IMAGE_REFERENCE_PRESENT));
                case IS_BAD_VIDEO_REFERENCE_PRESENT:
                    contentQuality.setBadVideoReferencePresent(contentQualityDoc.getBoolean(IS_BAD_VIDEO_REFERENCE_PRESENT));
                case MULTI_IMAGE_OVERFLOW:
                    contentQuality.setMultiImageOverflow(contentQualityDoc.getBoolean(MULTI_IMAGE_OVERFLOW));
                case BAD_TEXT:
                    contentQuality.setBadContent((ArrayList<String>) contentQualityDoc.get(BAD_TEXT));
            }
        }

        double aggregateQualityScore;
        if (contentQuality.isPublisherDatePresent()) {
            aggregateQualityScore =
                    contentQuality.getContentQualityScore() /
                            (Math.sqrt((1 + contentQuality.getInverseQualityIndex())));
        } else {
            aggregateQualityScore =
                    contentQuality.getContentQualityScore() /
                            (Math.sqrt((1 + contentQuality.getInverseQualityIndex() - publisherDateIndex)));
        }
        contentQuality.setAggregateQualityScore(aggregateQualityScore);
        return contentQuality;
    }

    public JsonObject getJson(ContentQuality contentQuality) {
        JsonObject jsonResponse = new JsonObject();
        JsonObject mediaResponse = new JsonObject();
        JsonObject contentResponse = new JsonObject();
        JsonObject tweetJSONResponse = new JsonObject();
        JsonObject instaMediaJSONResponse = new JsonObject();
        JsonObject multiImageResponse = new JsonObject();

        JsonObject contentQualityProp = new JsonObject();
        contentQualityProp.addProperty(IS_LONG, contentQuality.isTitleLong());
        contentQualityProp.addProperty(IS_SHORT, contentQuality.isTitleShort());
        contentQualityProp.addProperty(LENGTH, contentQuality.getTitleLength());
        jsonResponse.add(TITLE, contentQualityProp);

        contentQualityProp = new JsonObject();
        contentQualityProp.addProperty(PRESENT, contentQuality.isParagraphPresent());
        contentQualityProp.addProperty(COUNT, contentQuality.getParagraphCount());
        contentResponse.add(PARAGRAPH, contentQualityProp);
        contentResponse.addProperty(IS_SHORT, contentQuality.isContentShort());
        contentResponse.addProperty(LENGTH, contentQuality.getContentLength());
        contentResponse.add(JUNK_TEXT, getJsonArrayFromList(contentQuality.getBadContent()));
        jsonResponse.add(CONTENT, contentResponse);

        jsonResponse.addProperty(PUBLISHER_DATE_PRESENT, contentQuality.isPublisherDatePresent());

        mediaResponse.addProperty(IMAGE_PRESENT, contentQuality.isImagePresent());
        mediaResponse.addProperty(ICON_PRESENT, contentQuality.isIconPresent());
        mediaResponse.addProperty(BAD_IMAGE_REF_PRESENT, contentQuality.isBadImageReferencePresent());
        mediaResponse.addProperty(BAD_VIDEO_REF_PRESENT, contentQuality.isBadVideoReferencePresent());
        mediaResponse.addProperty(VIDEO_PRESENT, contentQuality.isVideoPresent());

        tweetJSONResponse.addProperty(PRESENT, contentQuality.isTweetPresent());
        tweetJSONResponse.addProperty(COUNT, contentQuality.getTweetCount());
        mediaResponse.add(TWEETS, tweetJSONResponse);

        instaMediaJSONResponse.addProperty(PRESENT, contentQuality.isInstagramPresent());
        instaMediaJSONResponse.addProperty(COUNT, contentQuality.getInstamediaCount());
        mediaResponse.add(INSTAGRAM, instaMediaJSONResponse);

        multiImageResponse.addProperty(PRESENT, contentQuality.isMultiImagePresent());
        multiImageResponse.addProperty(IMAGE_OVERFLOW, contentQuality.isMultiImageOverflow());
        multiImageResponse.addProperty(COUNT, contentQuality.getMultiImagecount());
        mediaResponse.add(MULTI_IMAGE, multiImageResponse);
        jsonResponse.add(MEDIA, mediaResponse);

        contentQualityProp = new JsonObject();
        contentQualityProp.addProperty(INVERSE_QUALITY_SCORE, contentQuality.getInverseQualityIndex());
        contentQualityProp.addProperty(CONTENT_QUALITY_SCORE, contentQuality.getContentQualityScore());
        contentQualityProp.addProperty(COMPUTED_QUALITY_SCORE, contentQuality.getAggregateQualityScore());
        jsonResponse.add(OVERALL, contentQualityProp);

        return jsonResponse;
    }

    private static JsonArray getJsonArrayFromList(List<String> items) {
        JsonArray array = new JsonArray();
        for (String str : items) {
            array.add(new JsonPrimitive(str));
        }
        return array;
    }

    public static CategoryQuality getCategoryQuality(Document categoryQualityDoc) {
        CategoryQuality categoryQuality = new CategoryQuality();
        Document group = (Document) categoryQualityDoc.get(ID);
        categoryQuality.setNpPk(group.getLong(NP_PK).intValue());
        categoryQuality.setCtPk(group.getLong(CT_PK).intValue());
        categoryQuality.setMaxInverseQualityIndex(categoryQualityDoc.getLong(MAX_INVERSE_QUALITY_INDEX));
        Double averageInverseQualityIndex = categoryQualityDoc.getDouble(AVG_INVERSE_QUALITY_INDEX);
        categoryQuality.setAverageInverseQualityIndex((Math.round(averageInverseQualityIndex * 100.0)) / 100.0);
        categoryQuality.setNewsPaper(group.getString(NEWSPAPER));
        categoryQuality.setCategory(group.getString(CATEGORY));

        return categoryQuality;
    }

    public static Content createContentForContentQuality(String contentStoreId, ContentQuality contentQuality) {

        Content content = null;
        ObjectMapper mapper = mapperPool.get();
        UUID writer = NameBasedUUIDGenerator.generateNameBasedUUID(UUID.fromString(DEFAULT_UUID_NAMESPACE), "nh-ingestion");
        NHModelFacet nhModelFacet = new NHModelFacet(UUID.fromString(contentStoreId), writer);
        try {
            nhModelFacet.put(NEWSHUNT_STORY_CONTENT_QUALITY_FACET_ID_ATTR_NAME, UUID.fromString(contentStoreId));
            nhModelFacet.put(NEWSHUNT_STORY_CONTENT_QUALITY_FACET_CONTENT_QUALITY_ATTR_NAME, contentQuality);
            String facetAsString = mapper.writeValueAsString(nhModelFacet);
            Map<UUID, String> facetRevisionMap = new LinkedHashMap<UUID, String>();
            facetRevisionMap.put(nhModelFacet.getRev(), facetAsString);
            content =
                    new Content(nhModelFacet.getId().toString(), NEWSHUNT_STORY_CONTENT_QUALITY_FACET_NAME,
                            facetRevisionMap);
        } catch (Exception ex) {
            LOG.error("Error while persisting facet {} for object {}", nhModelFacet.getName(), nhModelFacet.getId()
                    .toString(), ex);
        } finally {
            mapperPool.release(mapper);
        }
        return content;
    }

    private static double getNewQualityScore(ContentQuality contentQuality,
                                             ContentTypes contentType,
                                             ContentQualityConfig qualityConfig,
                                             int language) {
        double score = 0.0;

        if (contentQuality.getTitleLength() < qualityConfig.getTitleMinLength() ||
                contentQuality.getTitleLength() > qualityConfig.getTitleMaxLength()) {
            LOG.info("CONTENT_QUALITY_SCORE_ZERO TITLE_LENGTH_FAILED for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
            return 0;
        }

        if (contentQuality.isBadImageReferencePresent()) {
            LOG.info("CONTENT_QUALITY_SCORE_ZERO BAD_IMAGE_REFERENCE for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
            return 0;
        }

        if (contentQuality.isBadVideoReferencePresent()) {
            LOG.info("CONTENT_QUALITY_SCORE_ZERO BAD_VIDEO_REFERENCEfor item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
            return 0;
        }

        int wordCount = contentQuality.getWordCount();

        double wordCountScore = 0;
        if (wordCount != 0) {
            wordCountScore = getWordCountVsContentTypeScore(contentType, wordCount, qualityConfig);
        }

        if (wordCountScore <= 0) {
            LOG.info("CONTENT_QUALITY_SCORE_ZERO WORD_COUNT_FAILED for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
            return 0;
        }

        int iconScore = 0;

        if (contentQuality.isIconPresent()) {
            iconScore = 80;
        } else if (language == 2 || language == 7 || language == 8) {
            iconScore = 30;
        }

        double imageCountScore = 0.0;
        int imageCount =
                (contentQuality.isImagePresent() ? 1 : 0) + contentQuality.getMultiImagecount() +
                        contentQuality.getInstamediaCount() + contentQuality.getTweetCount();
        if (imageCount != 0) {
            imageCountScore = getImageVsContentTypeScore(contentType, imageCount);
        }

        score = iconScore + wordCountScore + imageCountScore;

        return score;
    }

    private static double getWordCountVsContentTypeScore(ContentTypes contentType,
                                                         int wordCount,
                                                         ContentQualityConfig qualityConfig) {
        double score = 0;
        if (ContentTypes.STORY == contentType && wordCount > qualityConfig.getStoryMinLength()) {
            score = 3 * Math.log10(wordCount);
        } else if (ContentTypes.S_W_IMAGES == contentType && wordCount > qualityConfig.getS_w_imagesMinLength()) {
            score = 3 * Math.log10(wordCount);
        } else if (ContentTypes.S_W_PHOTOGALLERY == contentType && wordCount > qualityConfig.getGalleryMinLength()) {
            score = 3 * Math.log10(wordCount);
        } else if (ContentTypes.RICH_PHOTOGALLERY == contentType && wordCount > qualityConfig.getRichGalleryMinLength()) {
            score = 3 * Math.log10(wordCount);
        } else if (ContentTypes.S_W_VIDEO == contentType && wordCount > qualityConfig.getVideoMinLength()) {
            score = 5 * Math.log10(wordCount);
        }else if (ContentTypes.RICH_VIDEO == contentType) {
            score = 5 * Math.log10(wordCount);
        }
        return score > 10 ? 10 : score;
    }

    private static double getImageVsContentTypeScore(ContentTypes contentType, int imageCount) {
        double score = 0;
        if (ContentTypes.STORY == contentType) {
            if (imageCount <= 4) {
                score = 5 * (1 + Math.log10(imageCount));
            } else {
                score = 5 * (2 - (1 + Math.log10(imageCount - 4)));
            }
        } else if (ContentTypes.S_W_IMAGES == contentType) {
            if (imageCount <= 12) {
                score = 5 * (1 + Math.log10(imageCount));
            } else {
                score = 5 * (2 - (1 + Math.log10(imageCount - 12)));
            }
        } else if (ContentTypes.S_W_PHOTOGALLERY == contentType) {
            if (imageCount <= 14) {
                score = 5 * (1 + Math.log10(imageCount));
            } else {
                score = 5 * (2 - (1 + Math.log10(imageCount - 14)));
            }
        } else if (ContentTypes.RICH_PHOTOGALLERY == contentType) {
            if (imageCount <= 16) {
                score = 5 * (1 + Math.log10(imageCount));
            } else {
                score = 5 * (2 - (1 + Math.log10(imageCount - 16)));
            }
        }
        return score > 10 ? 10 : (score < 0) ? 0 : score;
    }


    public static String getNormalizedNewsText(NewsData newsData){
        String newsText = "";
        String text20 = newsData.get(ELEMENT_TEXT_20_CHUNK_1);
        if (!StringUtils.isEmpty(text20) && !StringUtils.isEmpty(newsData.get(ELEMENT_TEXT_20_CHUNK_2))) {
            text20 = text20 + " " + newsData.get(ELEMENT_TEXT_20_CHUNK_2);
        }
        String news_HtmlText;
        if (StringUtils.isEmpty(text20)) {
            news_HtmlText = "";
        } else {
            news_HtmlText = text20.replaceAll(END_OF_STORY, "");
        }
        String newsHtmlText = news_HtmlText.toLowerCase();


        if (StringUtils.isEmpty(newsHtmlText)) {
            newsText = "";
        } else {
            newsText = Jsoup.parse(newsHtmlText).text();
        }

        return newsText;
    }

    public static int getWordCount(String newsText){
        int wordCount = 0;
        String[] content = newsText.split("\\s+");
        if (content != null && content.length > 0) {
            wordCount = content.length;
        }
        return wordCount;
    }

    public static Map<String , ContentQualityConfig> getContentQualityConfig() {
        return qualityConfig;
    }

    public static Map<String , ContentTypeConfig> getContentTypeConfig() {
        return contentTypeConfig;
    }

    public static void readContentQualityConfigFromFile(String fileName)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ContentQltTypConfig qltTypConfig = mapper.readValue(new File(fileName), ContentQltTypConfig.class);

        if(qltTypConfig != null){

            ContentQualityConfig qualityConfigAll = qltTypConfig.getContentQuality();
            for(ContentQualityConfig tmp:qualityConfigAll.getQualityParamsByLang()){
                qualityConfig.put(tmp.getLang(), tmp);
            }
            qualityConfig.put("ALL", qualityConfigAll);

            //Content Type config
            ContentTypeConfig contentTypeConfigAll = qltTypConfig.getContentType();
            for(ContentTypeConfig tmp:contentTypeConfigAll.getContentTypeParamsByLang()){
                contentTypeConfig.put(tmp.getLang(), tmp);
            }
            contentTypeConfig.put("ALL", contentTypeConfigAll);
        }

    }

    public static ContentQualityConfig getContentQualityInfo(String language) {
        Map<String , ContentQualityConfig> qualityConfig = getContentQualityConfig();
        ContentQualityConfig result = qualityConfig.get(language);
        if(result == null){
            result = qualityConfig.get("ALL");
            if(result == null){
                result = getDefaultContentQualityConfig();
            }
        }
        return result;
    }

    public static ContentTypeConfig getContentTypeInfo(String language) {
        Map<String , ContentTypeConfig> contentTypeConfig = getContentTypeConfig();
        ContentTypeConfig result = contentTypeConfig.get(language);
        if(result == null){
            result = contentTypeConfig.get("ALL");
            if(result == null){
                result = getDefaultContentTypeConfig();
            }
        }
        return result;
    }

    private static ContentQualityConfig getDefaultContentQualityConfig(){
        ContentQualityConfig config = new ContentQualityConfig();
        config.setTitleMinLength(properties.getInt("content.quality.title.min.length"));
        config.setTitleMaxLength(properties.getInt("content.quality.title.max.length"));
        config.setStoryMinLength(properties.getInt("content.quality.story.min.length"));
        config.setS_w_imagesMinLength(properties.getInt("content.quality.s_w_images.min.length"));
        config.setGalleryMinLength(properties.getInt("content.quality.gallery.min.length"));
        config.setRichGalleryMinLength(properties.getInt("content.quality.rich_gallery.min.length"));
        config.setVideoMinLength(properties.getInt("content.quality.video.min.length"));
        return config;
    }

    private static ContentTypeConfig getDefaultContentTypeConfig(){
        ContentTypeConfig config = new ContentTypeConfig();
        config.setRichGalleryMinLength(properties.getInt("content.type.rich_gallery.min.length"));
        config.setRichVideoMinLength(properties.getInt("content.type.video.min.length"));
        return config;
    }

    public static double getGridBasedQualityScore(ContentQuality contentQuality, String language, String contentType, int wordCount, int imageCount){
        if (contentQuality != null && contentQuality.isBadImageReferencePresent()) {
            LOG.info("CONTENT_QUALITY_SCORE_ONE BAD_IMAGE_REFERENCE for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
//            return 1;
        }

        if (contentQuality != null && contentQuality.isBadVideoReferencePresent()) {
            LOG.info("CONTENT_QUALITY_SCORE_ONE BAD_VIDEO_REFERENCE for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk());
//            return 1;
        }

        if (contentQuality != null && contentQuality.getBadContent() != null && !contentQuality.getBadContent().isEmpty()) {
            LOG.info("CONTENT_QUALITY_SCORE_ONE BAD_CONTENT_REFERENCE for item_id : " + contentQuality.getItemPk() +
                    " , newspaper : " + contentQuality.getNpPk() + " , category : " + contentQuality.getCtPk() + " , Bad Content : "+contentQuality.getBadContent().toString());
//            return 1;
        }

        String key = language+"_"+contentType;
        String key1 = language+"_ALL";

        TreeMap<Integer, TreeMap<Integer, Integer>> scoresMap = gridContentQualityConfig.get(key) == null ? gridContentQualityConfig.get(key1) : gridContentQualityConfig.get(key);

        //Incase of other languages we will be looking into default mapping
        if(scoresMap == null){
            key = "ANY_"+contentType;
            key1 = "ANY_ALL";
        }

        scoresMap = gridContentQualityConfig.get(key) == null ? gridContentQualityConfig.get(key1) : gridContentQualityConfig.get(key);

        if(scoresMap != null){
            Entry<Integer, TreeMap<Integer, Integer>>  entry = scoresMap.floorEntry(wordCount);
            Entry<Integer, Integer> resultEntry = entry.getValue().floorEntry(imageCount);
            return resultEntry.getValue();
        }
        return 0;
    }

    private static void readContentQualityGridConfigFromFile(String fileName)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        ContentQualityJSONConfig qltTypConfig = mapper.readValue(new File(fileName), ContentQualityJSONConfig.class);

        if(qltTypConfig != null){
            List<GridContentQualityConfig> qualityConfigAll = qltTypConfig.getContentQuality();
            for(GridContentQualityConfig tmp: qualityConfigAll){
                List<String> langArr = tmp.getLang();
                List<ScoreGrid> scoreGridArr= tmp.getScoreGrid();
                for(String lang:langArr){
                    for(ScoreGrid score:scoreGridArr){
                        String key = lang+"_"+score.getContentType();
                        TreeMap<Integer, TreeMap<Integer, Integer>> scoresMap = score.getQualityScore();
                        gridContentQualityConfig.put(key, scoresMap);
                    }
                }
            }
        }
    }


    public static void getProperties(CachedProperties properties){
        try {
            BAD_TEXT_STRING = FileUtils.readFromFile(properties.getString("content.quality.bad.text"));
            BAD_VIDEO_STRING = FileUtils.readFromFile(properties.getString("content.quality.bad.video.reference"));
            BAD_IMAGE_STRING = FileUtils.readFromFile(properties.getString("content.quality.bad.image.reference"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
*/
