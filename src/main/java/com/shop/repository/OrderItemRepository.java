package com.shop.repository;

import com.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi " +
            "join fetch oi.item i " +
            "join fetch i.itemImages g " +
            "where g.representativeImage = 'Y' " +
            "and oi.order.id in :orderIds")
    List<OrderItem> findOrderItems(@Param("orderIds") List<Long> orderIds);
}
