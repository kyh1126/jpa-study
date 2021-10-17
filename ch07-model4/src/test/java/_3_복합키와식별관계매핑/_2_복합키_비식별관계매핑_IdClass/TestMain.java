package _3_복합키와식별관계매핑._2_복합키_비식별관계매핑_IdClass;

import jpabook.model.entity._3_복합키와식별관계매핑._2_복합키_비식별관계매핑_IdClass.Parent1;
import jpabook.model.entity._3_복합키와식별관계매핑._2_복합키_비식별관계매핑_IdClass.ParentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

@Transactional
class TestMain {
    EntityManagerFactory emf;
    EntityManager em;
    EntityTransaction tx;

    @BeforeEach
    public void init() {
        //엔티티 매니저 팩토리 생성
        emf = Persistence.createEntityManagerFactory("jpabook");
        //엔티티 매니저 생성
        em = emf.createEntityManager();
        //트랜잭션 기능 획득
        tx = em.getTransaction();
    }

    @Test
    @DisplayName("복합 키를 사용하는 엔티티 저장")
    void test1() {
        try {
            tx.begin(); //트랜잭션 시작

            Parent1 parent = new Parent1();
            parent.setId("myId1", "myId2");
            parent.setName("parentName");
            // 복합 키 사용 엔티티 저장
            em.persist(parent);

            tx.commit();//트랜잭션 커밋
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
//            em.close(); //엔티티 매니저 종료
        }
//        emf.close(); //엔티티 매니저 팩토리 종료
    }

    @Test
    @DisplayName("복합 키로 엔티티 조회")
    void extracted() {
        test1();
        ParentId parentId = new ParentId("myId1", "myId2");
        Parent1 findParent = em.find(Parent1.class, parentId);
        System.out.println("findParent: " + findParent);
    }
}
