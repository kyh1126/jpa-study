package jpabook.model;

import static jpabook.model.entity.QMember.member;

import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import jpabook.model.entity.Member;
import jpabook.model.entity.QMember;
import jpabook.model.entity.item.Item;
import jpabook.model.entity.item.QItem;

public class Main {

    public static void main(String[] args) {

        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성

        EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득

        try {

            tx.begin(); //트랜잭션 시작
            //TODO 비즈니스 로직
            queryDSL94(em);
            tx.commit();//트랜잭션 커밋

        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
            em.close(); //엔티티 매니저 종료
        }

        emf.close(); //엔티티 매니저 팩토리 종료
    }

    /**
     * select m from jpabook.model.entity.Member m where m.name = ?1 order by m.name desc
     */
    public static void queryDSL(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QMember qMember = new QMember("m");     // 생성되는 JPQL의 별칭이 m
        JPAQuery qb = (JPAQuery) query
            .from(qMember)
            .where(qMember.name.eq("제니"))
            .orderBy(qMember.name.desc());
        List<Member> members = qb.fetch();
        for (Member member : members) {
            System.out.println("Member : " + member.getId() + ", " + member.getName());
        }
    }

    public static void basic(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        JPAQuery qb = (JPAQuery) query.from(member)
            .where(member.name.eq("제니"))
            .orderBy(member.name.desc());
        List<Member> members = qb.fetch();
    }

    /**
     * select item from jpabook.model.entity.item.Item item where item.name = ?1 and item.price >
     * ?2
     */
    public static void queryDSL83(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QItem item = QItem.item;
        JPAQuery qb = (JPAQuery) query.from(item)
            .where(item.name.eq("좋은상품").and(item.price.gt(20000)));
        List<Item> items = qb.fetch();
        for (Item findItem : items) {
            System.out.println("Member : " + findItem.getId() + ", " + findItem.getName());
        }
    }

    /**
     * select item from jpabook.model.entity.item.Item item where item.price > ?1 order by
     * item.price desc, item.stockQuantity asc
     */
    public static void queryDSL85(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QItem item = QItem.item;

//        JPAQuery qb = (JPAQuery) query.from(item)
//            .where(item.price.gt(20000))
//            .orderBy(item.price.desc(), item.stockQuantity.asc())
//            .offset(10).limit(20);

        QueryModifiers queryModifiers = new QueryModifiers(20L, 10L); // limit, offset

        JPAQuery qb = (JPAQuery) query.from(item)
            .where(item.price.gt(20000))
            .orderBy(item.price.desc(), item.stockQuantity.asc())
            .restrict(queryModifiers);
        List<Item> items = qb.fetch();

        for (Item findItem : items) {
            System.out.println("Member : " + findItem.getId() + ", " + findItem.getName());
        }
    }

    public static void queryDSL87(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QItem item = QItem.item;

        JPAQuery qb = (JPAQuery) query.from(item)
            .where(item.price.gt(10000))
            .orderBy(item.price.desc(), item.stockQuantity.asc()) // 가격 내림차순, 재고수량 오름차순
            .offset(10).limit(20);

//        SearchResult<Item> result = qb.listResults(item);
        QueryResults<Item> items = qb.fetchResults();
    }

    /**
     * select item from jpabook.model.entity.item.Item item where item.price = (select
     * max(itemSub.price) from jpabook.model.entity.item.Item itemSub)
     */
    public static void queryDSL93(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QItem item = QItem.item;
        QItem itemSub = new QItem("itemSub");

        JPAQuery qb = (JPAQuery) query.from(item)
            .where(item.price.eq(
//                new JPASubQuery().from(itemSub).unique(itemSub.price.max())
                JPAExpressions
                    .select(itemSub.price.max())
                    .from(itemSub)
            ));
        List<Item> items = qb.fetch();
    }

    /**
     * select item from jpabook.model.entity.item.Item item where item in (select itemSub from
     * jpabook.model.entity.item.Item itemSub where item.name = itemSub.name)
     */
    public static void queryDSL94(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QItem item = QItem.item;
        QItem itemSub = new QItem("itemSub");

        JPAQuery qb = (JPAQuery) query.from(item)
            .where(item.in(
//                new JPASubQuery().from(itemSub).where(item.name.eq(itemSub.name))
                JPAExpressions
                    .select(itemSub)
                    .from(itemSub)
                    .where(item.name.eq(itemSub.name))
            ));
        List<Item> items = qb.fetch();
    }

    public static void queryDSL101(EntityManager entityManager) {
        QItem item = QItem.item;

        JPAUpdateClause updateClause = new JPAUpdateClause(entityManager, item);
        long count1 = updateClause.where(item.name.eq("jenny 의 JPA 책"))
            .set(item.price, item.price.add(100)) // 상품의 가격을 100원 증가
            .execute();

        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, item);
        long count2 = deleteClause.where(item.name.eq("jenny 의 JPA 책"))
            .execute();
    }
}
