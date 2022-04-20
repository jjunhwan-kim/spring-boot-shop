package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MainItemDto {

    private Long id;

    private String itemName;

    private String itemDetail;

    private String imageUrl;

    private Integer price;
}
