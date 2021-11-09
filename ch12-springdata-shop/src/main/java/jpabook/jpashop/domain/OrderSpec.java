package jpabook.jpashop.domain;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class OrderSpec {

    public static Specification<Order> memberNameLike(final String memberName) {
        return new Specification<Order>() {
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {

                if (StringUtils.isEmpty(memberName)) {
                    return null;
                }

                Join<Order, Member> m = root.join("member", JoinType.INNER); //회원과 조인
                return builder.like(m.<String>get("name"), "%" + memberName + "%");
            }
        };
    }

    public static Specification<Order> orderStatusEq(final OrderStatus orderStatus) {
        return new Specification<Order>() {
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query,
                CriteriaBuilder builder) {

                if (orderStatus == null) {
                    return null;
                }

                return builder.equal(root.get("status"), orderStatus);
            }
        };
    }
}
