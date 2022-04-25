package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistoryDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImageRepository itemImageRepository;
    private final OrderItemQueryRepository orderItemQueryRepository;

    public Long order(OrderDto orderDto, String email) {

        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    public Long orders(List<OrderDto> orderDtoList, String email) {

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistoryDto> orderHistoryDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistoryDto orderHistoryDto = new OrderHistoryDto(order);
            List<OrderItem> orderItems = order.getOrderItems(); // Order <-> OrderItem 지연로딩시 N + 1 문제 발생
            for (OrderItem orderItem : orderItems) {
                ItemImage itemImage = itemImageRepository.findByItemIdAndRepresentativeImage(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImage.getImageUrl());
                orderHistoryDto.addOrderItemDto(orderItemDto);
            }
            orderHistoryDtos.add(orderHistoryDto);
        }

        return new PageImpl<>(orderHistoryDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryDto> getOrderListOptimized(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        // Order와 OrderItem은 1:N 관계이다. 컬렉션 Fetch Join을 하면 페이징처리를 할 수 없다. 따라서 Order 먼저 페이징 처리로 읽어온다.
        // OrderItem은 default_batch_fetch_size 옵션을 통해 in 쿼리로 여러 개의 orderId에 해당하는 OrderItem을 한 번에 받아온다.
        // OrderItem을 받아올 때 연관된 Item과 ItemImage를 한번에 받아오자.

        //Map<Long, List<OrderItem>> map = orderItemRepository.findOrderItems(orderIds).stream().collect(Collectors.groupingBy(o -> o.getOrder().getId()));
        Map<Long, List<OrderItem>> map = orderItemQueryRepository.findOrderItemsWithItemAndItemImage(orderIds).stream().collect(Collectors.groupingBy(o -> o.getOrder().getId()));

        List<OrderHistoryDto> orderHistoryDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistoryDto orderHistoryDto = new OrderHistoryDto(order);
            List<OrderItem> orderItems = map.get(order.getId());

            for (OrderItem orderItem : orderItems) {
                ItemImage itemImage = orderItem.getItem().getItemImages().get(0);
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImage.getImageUrl());
                orderHistoryDto.addOrderItemDto(orderItemDto);
            }
            orderHistoryDtos.add(orderHistoryDto);
        }

        return new PageImpl<>(orderHistoryDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        Member currentMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if (!StringUtils.equals(currentMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }
}
