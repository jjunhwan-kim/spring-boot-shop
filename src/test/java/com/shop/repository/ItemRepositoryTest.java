package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

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

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    void querydslTest() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> items = query.fetch();
        for (Item item : items) {
            System.out.println(item.toString());
            assertThat(item.getItemSellStatus()).isEqualTo(ItemSellStatus.SELL);
            assertThat(item.getItemDetail()).startsWith("테스트 상품 상세 설명");
        }
    }

    private void createItemList2() {
        for (int i = 1; i <= 5; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for (int i = 6; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    void querydslTest2() {
        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem qItem = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStatus = "SELL";

        booleanBuilder.and(qItem.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(qItem.price.gt(price));

        if (StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)) {
            booleanBuilder.and(qItem.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        PageRequest pageable = PageRequest.of(0, 5);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> items = itemPagingResult.getContent();
        for (Item item : items) {
            System.out.println(item);
            assertThat(item.getItemDetail().startsWith("테스트 상품 상세 설명"));
            assertThat(item.getPrice()).isGreaterThan(price);
            assertThat(item.getItemSellStatus()).isEqualTo(ItemSellStatus.SELL);
        }
    }
}