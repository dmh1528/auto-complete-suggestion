package com.example.location_data.locNormV3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author shivam.kumar
 */

public class TextUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TextUtil.class);
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeSpecialFloatingPointValues().create();
    // private static StopList stopList = new StopList(true);

    private static Pattern p = Pattern.compile("(?i).*(?<=Mr|Dr|Miss|mrs|Ms)[\\.]\\s*");
    private static Pattern abv = Pattern.compile(".*(?<=[A-Z]{1}\\.)\\s+[A-Z]{1}\\S+\\.$");
    private static Pattern single = Pattern.compile(".*(?<=[A-Z]{1})\\.$");
    private static Pattern quotes = Pattern.compile(".*[\u201C\u201D\u05F4\u02BA\u030B\u030E\u2033\u3003].*");
    private static Pattern parentheses = Pattern.compile("\\s+\\((.*?)\\)");
    private static Pattern digits = Pattern.compile("\\.*\\(\\d*\\)\\.*");
    private static String zeroWidthNonJoiner;

    static {
        char zeroWidthNonJoinerChar = 8204;
        zeroWidthNonJoiner = String.valueOf(zeroWidthNonJoinerChar);
    }

    public static String formatText(String text) {
        return text.replaceAll(zeroWidthNonJoiner, "").replaceAll("(<U\\+[\\w\\d]*>)", "").toLowerCase()
                .replaceAll("[-]+", "-").replaceAll("- ", " ").replaceAll("-", " ").replaceAll("'s|’s", "")
                .replaceAll("[”“‘’()]", "").replaceAll("[;:,+?/\\[\\]\\}\\{\\(\\)\"\'\\\\/\\>\\<]", " ")
                .replaceAll("[\'\"\u201C\u200B\u200C\u200D\uFEFF\u201D\u05F4\u02BA\u030B\u030E\u2033\u3003।]", "")
                .replaceAll("[;:,'\"?!.]", "").replaceAll("\\s+", " ").trim();

    }

    /*
     * public static void main(String[] st){ String str="तहरीक-ए-इंसाफ के प्रम";
     * System.out.println(formatText(str)); }
     */
    public static String removePunctuations(String text) {

        return text.trim().toLowerCase().replaceAll("[\\.।]", " ");
    }

    public static String removeDateAndDigits(String text) {

        return text.trim().toLowerCase().replaceAll("([\\d]+\\s+[a-z]{3} )", " ");
    }

    /*
     * public static void main(String[] args) { String s =
     * " 12 aug नई दिल्ली. कभी आपने प्रकृति "; System.out.println(formatText(s));
     * System.out.println(removePunctuations(s));
     * System.out.println(removeDateAndDigits(removePunctuations(formatText(s))));
     * System.out.println(s); }
     */

    public static String getFirstParagraph(String text) {
        if (StringUtils.isNotBlank(text)) {
            return text.split("\n")[0];
        }
        return text;
    }

    public static String getFirstWord(String text) {
        if (StringUtils.isNotBlank(text)) {
            text = formatText(text);
            text = removeDateAndDigits(removePunctuations(text));
            String[] textArray = text.split(" ");
            StringBuilder str = new StringBuilder();
            int count = 0;
            for (int i = 0; i < textArray.length; ++i) {
                str.append(textArray[i]).append(" ");
                if (++count >= 5) {
                    break;
                }
            }
            return str.toString().trim();
        }
        return "";
    }

    public static String firstSentence(String target) {

        BreakIterator iterator = BreakIterator.getSentenceInstance(new Locale("en", "US"));
        iterator.setText(target.trim());
        StringBuilder first = new StringBuilder();
        int boundary = iterator.first();
        int initial = 0;
        boolean isUnderQuotes = false;
        int nextQuoteSubstrIndex = 0;
        while (boundary != BreakIterator.DONE) {
            String str = target.substring(initial, boundary).trim();
            first.append(str);

            Matcher m = p.matcher(str);
            Matcher singleMatch = single.matcher(str);
            String quoteStr = target.substring(nextQuoteSubstrIndex, boundary).trim();
            Matcher quotesMatch = quotes.matcher(quoteStr);
            if (quotesMatch.matches()) {
                isUnderQuotes = !isUnderQuotes;
            }
            if (boundary > initial
                    && (!singleMatch.matches() && (!m.matches() || abv.matcher(str).matches()) && !isUnderQuotes)) {
                initial = boundary;
                break;
            }
            nextQuoteSubstrIndex = boundary;
            boundary = iterator.next();
        }

        return first.toString();
    }

    public static List<String> getAllSentences(String target) {

        BreakIterator iterator = BreakIterator.getSentenceInstance(new Locale("en", "US"));
        iterator.setText(target.trim());
        int boundary = iterator.first();
        int initial = 0;
        List<String> sentences = new ArrayList<>();
        boolean isUnderQuotes = false;
        int nextQuoteSubstrIndex = 0;
        while (boundary != BreakIterator.DONE) {
            String str = target.substring(initial, boundary).trim();

            Matcher m = p.matcher(str);
            Matcher singleMatch = single.matcher(str);
            String quoteStr = target.substring(nextQuoteSubstrIndex, boundary).trim();
            Matcher quotesMatch = quotes.matcher(quoteStr);
            if (quotesMatch.matches()) {
                isUnderQuotes = !isUnderQuotes;
            }
            if (boundary > initial
                    && (!singleMatch.matches() && (!m.matches() || abv.matcher(str).matches()) && !isUnderQuotes)) {
                initial = boundary;
                sentences.add(str);
            }
            nextQuoteSubstrIndex = boundary;
            boundary = iterator.next();
        }

        return sentences;
    }


}
