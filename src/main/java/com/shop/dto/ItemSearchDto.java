package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType; // all, 1d, 1w, 1m, 6m

    private ItemSellStatus searchSellStatus; // SELL, SOLD_OUT

    private String searchBy; // itemName, createdBy

    private String searchQuery = "";
}
