package _1_상속관계매핑._1_조인전략;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class TestMain {
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
    public void test1() {
        try {
            tx.begin(); //트랜잭션 시작


            tx.commit();//트랜잭션 커밋
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback(); //트랜잭션 롤백
        } finally {
            em.close(); //엔티티 매니저 종료
        }

        emf.close(); //엔티티 매니저 팩토리 종료

    }
}
