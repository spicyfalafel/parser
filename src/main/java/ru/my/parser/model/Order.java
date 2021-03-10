package ru.my.parser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Order {
    private long productId;
    private long sellerId;
    private String minPrice;
    private String maxPrice;
    private String discount;
    private boolean soldout;

    private String productPositiveRate;
    private String productAverageStar;

    private String productTitle;
    private String productDetailUrl;
    private String productImage;
    private String icon;
    private String orders;

    @JsonUnwrapped
    private OrderExtraInfo info;

}
