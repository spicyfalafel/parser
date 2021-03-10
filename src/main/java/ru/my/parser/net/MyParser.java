package ru.my.parser.net;

import lombok.Setter;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.my.parser.model.OrderExtraInfo;

import java.util.Map;

public class MyParser {
    @Setter
    private Map<String, String> cookies;

    @SneakyThrows
    public OrderExtraInfo getInfoFromUrl(String url) {
        Document doc = Jsoup.connect(url).cookies(cookies).get();

        System.out.println(url);

        /*
        If some of the content is created dynamically once the page is loaded,
        then your best chance to parse the full content would be to use Selenium with JSoup

        Но это вроде запрещено и я буду парсить js в теге <script>
         */

        Element el = doc.select("script")
                .not("[href]")
                .not("[src]")
                .not("[crossorigin]").last();
        int left = el.toString().indexOf("data") + 6;
        int right = el.toString().indexOf("};") + 1;
        String data = el.toString().substring(left, right);
        JSONObject obj = new JSONObject(data);
        int wishes = obj.getJSONObject("actionModule").getInt("itemWishedCount");
        String storeName = obj.getJSONObject("storeModule").getString("storeName");
        int feedbacks = obj.getJSONObject("titleModule").getJSONObject("feedbackRating")
                .getInt("totalValidNum");
        String formattedPrice = obj.getJSONObject("priceModule")
                .getJSONObject("minAmount").getString("formatedAmount");
        String name = obj.getJSONObject("titleModule").getString("subject");
        return new OrderExtraInfo(name, storeName, wishes, formattedPrice, feedbacks);
    }
}
