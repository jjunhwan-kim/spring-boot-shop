package com.shop.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.OrderItem;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.shop.entity.QItem.item;
import static com.shop.entity.QItemImage.itemImage;
import static com.shop.entity.QOrderItem.orderItem;

@Repository
public class OrderItemQueryRepository {

    private final JPAQueryFactory queryFactory;

    public OrderItemQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<OrderItem> findOrderItemsWithItemAndItemImage(List<Long> orderIds) {
        return queryFactory.selectFrom(orderItem)
                .join(orderItem.item, item).fetchJoin()
                .join(item.itemImages, itemImage).fetchJoin()
                .where(itemImage.representativeImage.eq("Y"))
                .where(orderItem.order.id.in(orderIds))
                .fetch();
    }
}
