package jpabook.jpashop.repository.custom;

import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

public class OrderRepositoryImpl extends QuerydslRepositorySupport implements
    CustomOrderRepository {

    public OrderRepositoryImpl() {
        super(Order.class);
    }

    @Override
    public List<Order> search(OrderSearch orderSearch) {

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        JPQLQuery<Order> query = from(order);

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query.leftJoin(order.member, member)
                .where(member.name.contains(orderSearch.getMemberName()));
        }

        if (orderSearch.getOrderStatus() != null) {
            query.where(order.status.eq(orderSearch.getOrderStatus()));
        }

        return query.fetch();
    }
}
