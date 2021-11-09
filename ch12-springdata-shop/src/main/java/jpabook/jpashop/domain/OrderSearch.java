package jpabook.jpashop.domain;

import static jpabook.jpashop.domain.OrderSpec.memberNameLike;
import static jpabook.jpashop.domain.OrderSpec.orderStatusEq;
import static org.springframework.data.jpa.domain.Specification.where;

import org.springframework.data.jpa.domain.Specification;

public class OrderSearch {

    private String memberName;      //회원 이름
    private OrderStatus orderStatus;//주문 상태

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Specification<Order> toSpecification() {
        return where(memberNameLike(memberName))
                .and(orderStatusEq(orderStatus));
    }

}
