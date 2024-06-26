package me.frkovo.llm;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.awt.*;
import java.io.File;
import java.io.StringReader;
import java.util.*;
import java.util.List;


public class Analyzer {
    static HashMap<String, Integer> wordCountMap = new HashMap<>();

    public static void extractKeywordDensity(String text) {
        try (SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer()) {
            TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
            CharTermAttribute charTermAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String token = charTermAttr.toString();
                wordCountMap.put(token, wordCountMap.getOrDefault(token, 0) + 1);
            }
            tokenStream.end();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void Generate(){
    List<WordFrequency> wordFrequencyList = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
        if(entry.getKey().matches("([0-9]+)"))continue;
        wordFrequencyList.add(new WordFrequency(entry.getKey(), entry.getValue()));
    }
    Collections.sort(wordFrequencyList, (o1, o2) -> o2.getFrequency() - o1.getFrequency());
    final Dimension dimension = new Dimension(2160,2160); // Increase the size of the word cloud
    final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
    wordCloud.setPadding(2);
    wordCloud.setKumoFont(new KumoFont(main.class.getResourceAsStream("SourceHanSerif-Regular.ttf")));
    wordCloud.setBackground(new CircleBackground(2160/2)); // Increase the radius of the circle background
    wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
    wordCloud.setFontScalar(new SqrtFontScalar(20, 256)); // Decrease the maximum size of the words
    wordCloud.build(wordFrequencyList.subList(0, 300)); // Only use the top 300 words
        wordCloud.writeToFile("wordcloud.png");
    }
}
