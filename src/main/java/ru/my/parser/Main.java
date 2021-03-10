package ru.my.parser;


import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import ru.my.parser.model.Order;
import ru.my.parser.model.OrderExtraInfo;
import ru.my.parser.net.MyParser;
import ru.my.parser.net.OrdersGetter;
import ru.my.parser.net.OrdersGetterImpl;
import ru.my.parser.storage.CSVOrdersFile;
import ru.my.parser.storage.OrdersFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/*
Спарсить 100 товаров по этой ссылке https://flashdeals.aliexpress.com/en.htm? за 1 минуту.
Писать парсер на Java version : 8 с применением ООП
Фреймворки запрещены (в том числе и Selenium)
Вспомогательные библиотеки разрешены. Формат csv, полей чем больше, тем лучше)
Проект должен запускаться без каких либо дополнительных действий (скачал, запустил, получил результат).
 */
public class Main {
    /*

    запишу ход своих мыслей)
    парсить статические сайты представляю как
    на алиэкспрессе ajax, показывает по 12 товаров

    вижу, что начальный запрос такой

    https://gpsfront.aliexpress.com/getRecommendingResults.do
    ?callback=jQuery18306415091887480753_1615295949782
    &widget_id=5547572
    &platform=pc
    &limit=12
    &offset=0
    &phase=1
    &productIds2Top=
    &postback=
    &_=1615295950054
    */
    /*

    кручу вниз, еще 12 товаров показывается, запрос был

    https://gpsfront.aliexpress.com/getRecommendingResults.do
    ?callback=jQuery18306415091887480753_1615295949782
    &widget_id=5547572
    &platform=pc
    &limit=12
    &offset=12
    &phase=1
    &productIds2Top=
    &postback=ef0a3e15-2508-47da-873a-28ef4a6be3dc
    &_=1615296104201


    вижу, что callback одинаковые, offset становится 12
    проверил при limit=3, что offset - это количество товаров, которые уже прогрузились
    при этом offset=0 offset=1 offset=2 дают одинаковый результат
    limit максимум 50, значит, надо будет сделать 2 запроса


    то есть, такой алгоритм:
    1) делаю два запроса с этими же параметрами, только limit=50, во втором offset=50
    2) для каждого запроса сохраняю товары
    3) сохраняю в csv
    (UPD при limit=50 дает 40 товаров, 3 запроса)
    https://gpsfront.aliexpress.com/getRecommendingResults.do?widget_id=5547572&platform=pc&limit=50&offset=0
     */

    public static void main(String[] args) throws IOException {
        long before = System.currentTimeMillis();
        OrdersGetter ordersGetter = new OrdersGetterImpl();

        System.out.println("Downloading orders from API");
        String first40 = "https://gpsfront.aliexpress.com/" +
                "getRecommendingResults.do?widget_id=5547572&platform=pc&limit=40&offset=0";
        System.out.println(first40);
        List<Order> orders1 = ordersGetter.getFromUrl(first40);
        String postback = OrdersGetterImpl.getPostback();
        String second40 = "https://gpsfront.aliexpress.com/" +
                "getRecommendingResults.do?widget_id=5547572&platform=pc&limit=50&offset=40&postback=" + postback;
        System.out.println(second40);
        List<Order> orders2 = ordersGetter.getFromUrl(second40);
        String third20 = "https://gpsfront.aliexpress.com/" +
                "getRecommendingResults.do?widget_id=5547572&platform=pc&limit=20&offset=80&postback=" + postback;
        System.out.println(third20);
        List<Order> orders3 = ordersGetter.getFromUrl(third20);

        orders1.addAll(orders2);
        orders1.addAll(orders3); // это выглядит не очень, но это быстрее, чем 3 раза открывать файл


        /*
        после этого я понял, что можно зайти на товар и найти еще информацию
        например, взять название на русском, название магазина, количество отзывов, количество людей, добавивших в избранное
        и цену в рублях
         */

        MyParser parser = new MyParser();


        System.out.println("Getting info from details pages");
        // с куками быстрее в 7 раз
        Connection.Response res = Jsoup
                .connect("https://" + orders1.get(0).getProductDetailUrl().substring(2))
                .method(Connection.Method.GET)
                .execute();
        Map<String, String> cookies = res.cookies();
        parser.setCookies(cookies);


        orders1.forEach(order -> {
            OrderExtraInfo info = parser.getInfoFromUrl("https://" + (order.getProductDetailUrl()
                    .substring(2)));
            order.setInfo(info);
        });
        System.out.println("Done scrapping!");
        OrdersFile of = new CSVOrdersFile();
        of.toFile(orders1, "orders.csv");
        System.out.println("written to file next to jar");
        System.out.println("Done in " + (System.currentTimeMillis() - before) / 1000 + " sec");
    }
}
