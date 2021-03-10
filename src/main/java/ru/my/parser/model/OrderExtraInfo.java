package ru.my.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderExtraInfo {
    private String localeTitleText;
    private String shopName;

    private int numberOfWishlist;


    private String price;

    private int reviews;


}
