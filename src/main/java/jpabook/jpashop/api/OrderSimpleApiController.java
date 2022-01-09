package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ManyToOne, OneToOne 관계(Lazy 로딩) 성능 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch("기연", OrderStatus.ORDER)); //모든 order를 가져옴 -> order가 member를 가져옴, member가 order를 가져옴(양방향) -> 무한루프, 해결방안은 양방향에 @JsonIgnore
        for (Order order : all) {
            order.getMember().getName(); //getMember()까지는 프록시 객체, getName()은 멤버의 이름을 가져오는 것이라 DB에 접근해야함 -> Lazy 강제 초기화
            order.getDelivery().getAddress(); //delivery에 있는 아무 필드 가져오면 delivery Lazy 강제 초기화
        }
        return all; //엔티티 그대로 노출하지 않기 -> 바뀌면 유지보수 힘듦
    }

    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() { //바로 List로 반환하지 말고 Result로 감싸기
        List<Order> orders = orderRepository.findAll(new OrderSearch("기연", OrderStatus.ORDER));

        //N+1 문제
        //주문 2개 -> 루프 2번 -> Dto를 2번씩 new 함 -> Lazy 초기화 총 4번 => 총 쿼리 개수 5번 (Order(1) + Member(2) + Delivery(2))
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //v1, v2 함수 -> lazy 로딩 때문에 쿼리가 많이 호출됨 (order, member, delivery 테이블을 다 조회)

    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T simpleOrders;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //Lazy 초기화 (값이 없으니까 DB에 쿼리날림)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //Lazy 초기화
        }
    }

    @Data
    static class orderSearch {

    }

}
