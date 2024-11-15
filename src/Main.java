import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    // Classe per memorizzare il titolo della pagina e il suo URL
    public static class PageInfo {
        private final String title;
        private final String url;

        public PageInfo(String title, String url) {
            this.title = title;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Titolo: " + title + ", URL: " + url;
        }
    }

    // Metodo principale di crawl statico
    public static List<PageInfo> crawl(String url, int maxDepth) {
        Set<String> visitedUrls = new HashSet<>();
        List<PageInfo> pageInfos = new ArrayList<>();
        crawlRecursive(url, visitedUrls, pageInfos, 1, maxDepth);
        return pageInfos;
    }

    // Metodo per il crawling ricorsivo
    private static void crawlRecursive(String url, Set<String> visitedUrls, List<PageInfo> pageInfos, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth || visitedUrls.contains(url)) {
            return; // Interrompe se supera la profondità o l'URL è già stato visitato
        }

        visitedUrls.add(url); // Aggiunge l'URL al set dei visitati

        try {
            Document doc = Jsoup.connect(url).get(); // Scarica il contenuto della pagina

            // Aggiunge l'informazione della pagina alla lista e la stampa
            PageInfo pageInfo = new PageInfo(doc.title(), url);
            pageInfos.add(pageInfo);
            System.out.println("Profondità " + currentDepth + ": " + pageInfo);

            // Estrazione e ricorsione sui link
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkHref = link.absUrl("href");
                if (!visitedUrls.contains(linkHref) && isHttpOrHttps(linkHref)) {
                    crawlRecursive(linkHref, visitedUrls, pageInfos, currentDepth + 1, maxDepth);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'accesso all'URL: " + url);
        }
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

    // Metodo main per l'esecuzione del crawler
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Richiesta dell'URL seed all'utente
        System.out.print("Inserisci il seed URL: ");
        String seedUrl = scanner.nextLine().trim();
        seedUrl = formatUrl(seedUrl);

        System.out.print("Inserisci la profondità massima (es. 3): ");
        int maxDepth = scanner.nextInt();

        System.out.println("Inizio del crawling...\n");

        // Esegui il crawling e ottieni i risultati
        List<PageInfo> results = crawl(seedUrl, maxDepth);

        // Stampa riepilogativa
        System.out.println("\nRiepilogo dei link trovati:");
        for (PageInfo pageInfo : results) {
            System.out.println(pageInfo);
        }

        System.out.println("\nCrawling completato.");
    }
}
