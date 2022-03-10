package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    void createItemTest() {
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    private void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    void findItemByItemNameTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByItemName("테스트 상품1");
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getItemName()).isEqualTo("테스트 상품1");
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    void findItemByItemNameOrItemDetailTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByItemNameOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item : items) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    void findItemByPriceLessThanTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByPriceLessThan(10005);
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getPrice()).isLessThan(10005);
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    void findItemByPriceLessThanOrderByPriceDescTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getPrice()).isLessThan(10005);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    void findItemByItemDetailTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByItemDetail("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getItemDetail()).startsWith("테스트 상품 상세 설명");
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    void findItemByItemDetailByNativeTest() {
        this.createItemList();
        List<Item> items = itemRepository.findItemByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getItemDetail()).startsWith("테스트 상품 상세 설명");
        }
    }
}