package ru.my.parser.net;

import ru.my.parser.model.Order;

import java.util.List;

public interface OrdersGetter {
    List<Order> getFromUrl(String url);
    String getJsonFromUrl(String url);
}
