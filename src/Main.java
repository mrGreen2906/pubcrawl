import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    // Classe per memorizzare il titolo della pagina e il suo URL
    public static class PageInfo {
        private String title;
        private String url;

        public PageInfo(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "Titolo: " + title + ", URL: " + url;
        }
    }

    // Metodo principale di crawl statico
    public static List<PageInfo> crawl(String url, int level) {
        Set<String> visitedUrls = new HashSet<>();
        List<PageInfo> pageInfos = new ArrayList<>();
        crawlLevelOne(url, visitedUrls, pageInfos, level);
        return pageInfos;
    }

    // Metodo per il crawling di livello 1
    private static void crawlLevelOne(String url, Set<String> visitedUrls, List<PageInfo> pageInfos, int level) {
        if (level != 1 || visitedUrls.contains(url)) {
            return;
        }
        visitedUrls.add(url);

        try {
            Document doc = getDocument(url);
            if (doc != null) {
                // Aggiunge l'informazione della pagina alla lista e la stampa
                PageInfo pageInfo = new PageInfo(doc.title(), url);
                pageInfos.add(pageInfo);
                System.out.println(pageInfo);

                // Estrazione e stampa dei link di livello 1
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String linkHref = link.absUrl("href");
                    if (!visitedUrls.contains(linkHref) && isHttpOrHttps(linkHref)) {
                        visitedUrls.add(linkHref);
                        PageInfo linkedPageInfo = new PageInfo(getPageTitle(linkHref), linkHref);
                        pageInfos.add(linkedPageInfo);
                        System.out.println(linkedPageInfo);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'accesso all'URL: " + url);
        }
    }

    // Metodo per ottenere un documento JSoup dato un URL
    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    // Metodo per controllare se l'URL inizia con "http" o "https"
    private static boolean isHttpOrHttps(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    // Metodo per aggiungere "http://" se manca
    private static String formatUrl(String url) {
        if (!isHttpOrHttps(url)) {
            url = "http://" + url;
        }
        return url;
    }

    // Metodo per ottenere il titolo della pagina data la sua URL
    private static String getPageTitle(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.title();
        } catch (IOException e) {
            return "Titolo non disponibile";
        }
    }

    // Metodo main per l'esecuzione del crawler
    public static void main(String[] args) {
        // Richiesta dell'URL seed all'utente
        String seedUrl = "wikipedia.org"; // Modifica con un URL valido o richiedi all'utente
        seedUrl = formatUrl(seedUrl);

        // Esegui il crawling e ottieni i risultati
        List<PageInfo> results = crawl(seedUrl, 1);

        // Stampa riepilogativa
        System.out.println("\nRiepilogo dei link trovati:");
        for (PageInfo pageInfo : results) {
            System.out.println(pageInfo);
        }
    }
}
