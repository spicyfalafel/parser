package ru.my.parser.storage;

import ru.my.parser.model.Order;

import java.util.List;

public interface OrdersFile {

    void toFile(List<Order> orders, String filename);
}
