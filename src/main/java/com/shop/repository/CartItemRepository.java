package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

/*
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemName, i.price, ci.count, ii.imageUrl) " +
            "from CartItem ci, ItemImage ii " + // cross join?
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and ii.item.id = i.id " +
            "and ii.representativeImage = 'Y' " +
            "order by ci.createdTime desc")
*/
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemName, i.price, ci.count, ii.imageUrl) " +
            "from CartItem ci " +
            "join ci.item i " +
            "join i.itemImages ii " +
            "where ci.cart.id = :cartId " +
            "and ii.item.id = i.id " +
            "and ii.representativeImage = 'Y' " +
            "order by ci.createdTime desc")
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);
}
