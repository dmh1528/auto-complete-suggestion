/*
package ContentQualityCheck;

import org.apache.commons.lang3.StringUtils;

public class ContentQualityUtils {
    content.quality.sentences.per.paragraph=4
    content.quality.max.multi.images=20
    ontent.quality.min.content.length.video=10
    content.quality.min.content.length=200
    content.quality.title.min.length=9
    content.quality.title.max.length=12



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
            iconPresent = true;*/
/*
content.quality.sentences.per.paragraph=4
        content.quality.max.multi.images=20
        ontent.quality.min.content.length.video=10
        content.quality.min.content.length=200
        content.quality.title.min.length=9
        content.quality.title.max.length=12
*//*


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
}
*/
