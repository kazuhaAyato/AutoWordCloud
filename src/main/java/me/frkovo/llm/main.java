package me.frkovo.llm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws Exception{
        System.out.println("Hello World!");
        Scanner scanner = new Scanner(System.in);
        System.out.println("你想搜索什么");
        String ok = scanner.nextLine();
        System.out.println("你想搜索的是" + ok);
        long cur = System.currentTimeMillis();
        search(ok,0);
        Analyzer.Generate();
        System.out.println("OK! ("+(System.currentTimeMillis() - cur) + "ms)");
    }
    private static void search(String keyword,int page) throws Exception {
        if(page > 32)return;
        URL url = new URL("https://www.bing.com/search?first="+page+"&q=" + keyword);
        Document document = Jsoup.connect(url.toString()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0").get();
        Elements elements = document.select("ol#b_results").select("li.b_algo");
        for (Element element : elements) {
            try {
                Element element1 = element.select("h2").select("a").first();
                if(element1 == null)continue;
                Document docuemt= Jsoup.connect(element1.attr("href")).userAgent("ChatGPT/1.0 Edge/1.0").get();
                if(!docuemt.title().isEmpty()){
                     System.out.println("[#"+(page++)+"][READING] "+docuemt.title());
                }else{
                    System.out.println("[#"+(page++)+"][READING] "+element1.attr("href"));
                }
                String text = docuemt.body().text();
                Analyzer.extractKeywordDensity(text);
                }catch (Exception e){
                    System.out.println("Error, Safe to Ignore:" + e.getLocalizedMessage());
                }
        }
        if(elements.isEmpty()){
            System.out.println("Done");
            return;
        }
        search(keyword,page);
    }
}
