# Chapter 5. 연관관계 매핑 기초

## 1. 단방향 연관관계

- 객체의 참조와 테이블의 외래 키 매핑 핵심 키워드
    - 방향
        - 단방향
            - 회원 → 팀
            - 팀 → 회원
        - 양방향
            - 회원 → 팀, 팀 → 회원
    - 다중성
        - 다대일 (N:1), 일대다 (1:N), 일대일 (1:1), 다대다 (N:N)
    - 연관관계의 주인
        - 객체를 양방향 연관관계로 만들면 연관관계의 주인을 정해야 한다.

### 1-1. 순수한 객체 연관관계

---

- 단방향, 다대일(N:1)
    
    ![Untitled](Chapter%205%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%203a9bd1519d6849039651186fc6dee4bc/Untitled.png)
    
- 회원과 팀 클래스
    
    ```java
    public class Member {
        private String id;
        private String username;
    
        @Getter @Setter
        private Team team; //팀의 참조를 보관
    }
    
    @Getter @Setter
    public class Team {
        private String id;
        private String name;
    }
    ```
    
- 동작 코드
    
    ```java
    Team team1 = new Team("team1", "팀1");
    
    Member member1 = new Member("member1", "회원1");
    Member member2 = new Member("member2", "회원2");
    
    member1.setTeam(team);
    member2.setTeam(team);
    
    Team findTeam = member1.getTeam(); // 그래프 탐색
    ```
    

### 1-2. 테이블 연관관계

---

- 테이블 DDL
    
    ```sql
    CREATE TABLE MEMBER (
        MEMBER_ID VARCHAR(255) NOT NULL,
        TEAM_ID VARCHAR(255),
        USERNAME VARCHAR(255),
        PRIMARY KEY (MEMBER_ID)
    )
    CREATE TABLE TEAM (
        TEAM_ID VARCHAR(255) NOT NULL,
        NAME VARCHAR(255),
        PRIMARY KEY (TEAM_ID)
    )
    ALTER TABLE MEMBER ADD CONSTRAINT FK_MEMBER_TEAM
        FOREIGN KEY (TEAM_ID) REFERENCES TEAM;
    ```
    

### 1-3. 객체 관계 매핑

---

- 다대일 연관관계1 | 다대일(N:1), 단방향
    
    ![Untitled](Chapter%205%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%203a9bd1519d6849039651186fc6dee4bc/Untitled%201.png)
    
- 매핑한 회원, 팀 엔티티
    
    ```java
    @Entity
    @Getter @Setter
    public class Member {
        @Id
        @Column(name = "MEMBER_ID")
        private String id;
    
        private String username;
    
        //연관관계 매핑
        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;
    }
    
    @Entity
    @Getter @Setter
    public class Team {
        @Id
        @Column(name = "TEAM_ID")
        private String id;
    
        private String name;
    }
    ```
    
- `@ManyToOne`
    - 다대일(N:1) 관계라는 매핑 정보
    - 연관관계를 매핑할 때: 다중성을 나타내는 어노테이션 필수
- `@JoinColumn(name="TEAM_ID")`
    - 외래 키를 매핑할 때 사용. 생략 가능
    - `name` 속성에 매핑할 외래 키 이름 지정

### 1-4. `@JoinColumn`

---

- `name`: 매핑할 외래 키 이름
    - default: 필드명 + `_` + 참조하는 테이블의 기본 키 컬럼명
- `referencedColumnName`: 외래 키가 참조하는 대상 테이블의 컬럼명
    - default: 참조하는 테이블의 기본 키 컬럼명
- `foreignKey`(DDL): 외래 키 제약조건을 직접 지정할 수 있다. 테이블을 생성할 때만 사용한다.
- `unique`, `nullable`, `insertable`, `updatable`, `columnDefinition`, `table`: `@Column`의 속성과 같다.

### 1-5. `@ManyToOne`

---

- `optional`: `false`로 설정하면 연관된 엔티티가 항상 있어야함
    - default: `true`
- `fetch`: 글로벌 페치 전략을 설정
    - default: `@ManyToOne=EAGER`, `@OneToMany=LAZY`, `@OneToOne=EAGER`
- `cascade`: 영속성 전이 기능을 사용한다.
- `targetEntity`: 연관된 엔티티의 타입 정보를 설정한다. (거의 사용 x → 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있다)


## 2. 연관관계 사용

### 2-1. 저장

---

- 회원과 팀을 저장하는 코드
    
    ```java
    public void testSave() {
        //팀1 저장
        Team team1 = new Team("team1", "팀1");
        em.persist(team1);
    
        //회원1 저장
        Member member1 = new Member(100L, "회원1");
        member1.setTeam(team1);     //연관관계 설정 member1 -> team1
        em.persist(member1);
    
        //회원2 저장
        Member member2 = new Member(101L, "회원2");
        member2.setTeam(team1);     //연관관계 설정 member2 -> team1
        em.persist(member2);
    }
    ```
    

[결과](https://www.notion.so/b8ed2597f6d147daa682e67345f2495e)

JPA 에서 엔티티를 저장할 때 연관된 모든 엔티티는 영속 상태여야 한다.

### 2-2. 조회

---

- 연관관계가 있는 엔티티를 조회하는 방법 2가지
    - 객체 그래프 탐색(객체 연관관계를 사용한 조회)
        
        ```java
        Member member = em.find(Member.class, 100L);
        Team team = member.getTeam();   //객체 그래프 탐색
        System.out.println("팀 이름 = " + team.getName());
        ```
        
    - 객체지향 쿼리 사용(JPQL)
        
        ```java
        public static void testJPQL(EntityManager em) {
            String jpql1 = "select m from Member m join m.team t where " +
                    "t.name = :teamName";
        
            List<Member> resultList = em.createQuery(jpql1, Member.class)
                    .setParameter("teamName", "팀1")
                    .getResultList();
        
            for (Member member : resultList) {
                System.out.println("[query] member.username = " +
                        member.getUsername());
            }
        }
        ```
        

### 2-3. 수정

---

- 연관관계를 수정하는 코드
    
    ```java
    private static void updateRelation(EntityManager em) {
        // 새로운 팀2
        Team team2 = new Team("team2", "팀2");
        em.persist(team2);
    
        //회원1에 새로운 팀2 설정
        Member member = em.find(Member.class, 100L);
        member.setTeam(team2);
    }
    ```
    
- 수정은 `em.update()` 같은 메소드가 없다.
- 불러온 엔티티의 값만 변경해두면 트랜잭션을 커밋할 때 플러시가 일어나면서 변경 감지 기능이 작동한다.
    - 변경 사항을 DB에 자동으로 반영한다.

### 2-4. 연관관계 제거

---

- 연관관계를 삭제하는 코드
    
    ```java
    private static void deleteRelation(EntityManager em) {
        Member member1 = em.find(Member.class, "member1");
        member1.setTeam(null);      //연관관계 제거
    }
    ```
    
- 실제 SQL
    
    ```sql
    UPDATE MEMBER
    SET 
        TEAM_ID = null, ...
    WHERE 
        ID = 'member1'
    ```
    

### 2-5. 연관된 엔티티 삭제

---

- 연관된 엔티티를 삭제하려면 먼저 연관관계를 제거하고 삭제해야 한다.
    
    ```java
    member1.setTeam(null);  // 회원1 연관관계 제거
    member2.setTeam(null);  // 회원2 연관관계 제거
    em.remove(team);        // 팀 삭제
    ```
    
- 그렇지 않으면 외래 키 제약조건으로 데이터베이스 오류가 발생한다.


## 3. 양방향 연관관계

- 양방향 매핑
    - 단방향 매핑으로 이미 연관관계 매핑은 완료
    - 양방향은 반대 방향으로 객체 그래프 탐색 기능이 추가된 것
    - 단방향 매핑을 잘하고, 양방향 매핑은 필요할 때 추가해도 됨

![Untitled](Chapter%205%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%203a9bd1519d6849039651186fc6dee4bc/Untitled%202.png)

- 위(다대일 단방향 매핑)➕ 팀에서 회원으로 접근하는 관계를 추가하자.
    
    → 양방향 연관관계로 매핑: 회원에서 팀으로, 팀에서 회원으로 접근 가능
    
    - 데이터베이스 테이블은 외래 키 하나로 양방향 조회 가능 → DB 에 추가할 내용은 전혀 없다.

### 3-1. 양방향 연관관계 매핑

---

- 매핑한 회원 엔티티: 변경되는 부분 없음
    
    ```java
    @Entity
    public class Member {
        @Id
        @Column(name = "MEMBER_ID")
        private String id;
    
        private String username;
    
        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;
    
        //연관관계 설정
        public void setTeam(Team team) {
            this.team = team;
        }
    
        //Getter, Setter
    }
    ```
    
- 매핑한 팀 엔티티
    
    ```java
    @Entity
    public class Team {
        @Id
        @Column(name = "TEAM_ID")
        private String id;
    
        private String name;
    
        //추가
        **@OneToMany(mappedBy = "team")
        private List<Member> members = new ArrayList<Member>();**
    
        // Getter, Setter ...
    }
    ```
    
    - 일대다 관계 매핑: `List<Member> members` 추가
    - `mappedBy` 속성: 양방향 매핑일 때 사용한다. 반대쪽 매핑의 필드 이름을 값으로 주면 된다.

### 3-2. 일대다 컬렉션 조회

---

- 일대다 방향으로 객체 그래프 탐색
    
    ```java
    public void biDirection() {
        Team team = em.find(Team.class, "team1");
        List<Member> members = team.getMembers();   // 팀 -> 회원, 객체그래프 탐색
    
        for (Member member : members) {
            System.out.println("member.username = " + 
                    member.getUsername());
        }
    }
    ```
    

## 4. 연관관계의 주인

- 객체에는 양방향 연관관계는 없음.
    - 서로 다른 단방향 연관관계 2개를 어플리케이션 로직으로 양방향인 것처럼 보이게 할 뿐
- 데이터베이스 테이블은 외래 키 하나로 양쪽이 서로 조인 가능
    - 테이블은 외래 키 하나만으로 양방향 연관관계
- 엔티티를 양방향 연관관계로 설정하면 객체의 참조는 둘인데 외래키는 하나
    
    → 둘 사이에 차이가 발생
    
    👉 두 객체 연관관계 중 하나를 정해서 테이블의 외래 키를 관리하는데 이것이 연관관계의 주인
    

### 4-1. 양방향 매핑의 규칙: 연관관계의 주인

---

- 연관관계의 주인만이 데이타베이스 연관관계와 매핑되고 외래 키를 관리(등록, 수정, 삭제)할 수 있다.
- 주인이 아닌 쪽은 읽기만 할 수 있다.
- `mappedBy` 속성
    - 주인은 `mappedBy` 속성을 사용하지 않는다.
    - 주인이 아니면 `mappedBy` 속성을 사용해서 속성의 값으로 연관관계의 주인을 지정해야 한다.

- 둘 중 하나를 연관관계의 주인으로 선택해야 한다.
    
    ![Untitled](Chapter%205%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%203a9bd1519d6849039651186fc6dee4bc/Untitled%203.png)
    

### 4-2. 연관관계의 주인은 외래 키가 있는 곳

---

- 연관관계의 주인과 반대편
    
    ![Untitled](Chapter%205%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20%E1%84%80%E1%85%B5%E1%84%8E%E1%85%A9%203a9bd1519d6849039651186fc6dee4bc/Untitled%204.png)
    
- 연관관계의 주인은 테이블에 외래 키가 있는 곳으로 정해야 한다.
    - 회원 테이블이 외래 키를 가지고 있다.
- 주인이 아닌 Team.members 는 `mappedBy`를 통해 주인이 아님을 설정한다.
    
    ```java
    class Team {
        @OneToMany(mappedBy = "team")  // 연관관계 주인인 Member.team
        private List<Member> members = new ArrayList<Member>();
    }
    ```
    

DB 테이블의 다대일, 일대다 관계에서는 항상 다 쪽이 외래키를 가진다. 다 쪽인 `@ManyToOne`은 항상 연관관계의 주인이 되므로 `mappedBy`를 설정할 수 없다.(`mappedBy` 속성도 없다)


## 5. 양방향 연관관계 저장

- 양방향 연관관계 저장: '1. 단방향 연관관계' 에서 살펴본 코드와 같다.
    
    ```java
    public void testSave() {
        //팀1 저장
        Team team1 = new Team("team1", "팀1");
        em.persist(team1);
    
        //회원1 저장
        Member member1 = new Member("member1", "회원1");
        member1.setTeam(team1);     //연관관계 설정 member1 -> team1
        em.persist(member1);
    
        //회원2 저장
        Member member2 = new Member("member2", "회원2");
        member2.setTeam(team1);     //연관관계 설정 member2 -> team1
        em.persist(member2);
    }
    ```
    

- 주인이 아닌 곳에 입력된 값은 외래 키에 영향을 주지 않는다.
- Member.team 은 연관관계의 주인, 엔티티 매니저는 이곳에 입력된 값으로 외래 키를 관리한다.
    
    ```java
    team1.getMembers().add(member1);        //무시
    team1.getMembers().add(member2);        //무시
    
    member1.setTeam(team1);                 //연관관계 설정(연관관계의 주인)
    member2.setTeam(team1);                 //연관관계 설정(연관관계의 주인)
    ```
    

## 6. 양방향 연관관계의 주의점

- 양방향 연관관계 주의점: 주인이 아닌 곳에만 값을 입력하는 실수
    
    ```java
    public void testSaveNonOwner() {
        //회원1 저장
        Member member1 = new Member("member1", "회원1");
        em.persist(member1);
    
        //회원2 저장
        Member member2 = new Member("member2", "회원2");
        em.persist(member2);
    
        Team team1 = new Team("team1", "팀1");
    
        //주인이 아닌 곳에 연관관계 설정
        team1.getMembers().add(member1);
        team2.getMembers().add(member2);
    
        em.persist(team1);
    }
    ```
    

[조회한 결과](https://www.notion.so/923e52c320cd40f586a55b2c9a6a8da1)

👉 연관관계의 주인만이 외래 키의 값을 변경할 수 있다.

### 6-1. 순수한 객체까지 고려한 양방향 연관관계

---

- 객체 관점에서 양쪽 방향에 모두 값을 입력해주는 것이 가장 안전
    - 양쪽 방향 모두 값을 입력하지 않으면 JPA 를 사용하지 않는 순수한 객체 상태에서 심각한 문제가 발생할 수 있다.
- 순수한 객체 연관관계
    
    ```java
    //팀1
    Team team1 = new Team("team1", "팀1");
    Member member1 = new Member("member1", "회원1");
    member1.setTeam(team); //연관관계 설정 member1 -> team1
    
    List<Member> members = team1.getMembers(); //members.size()가 다르다.
    ```
    
- 양방향 모두 관계를 설정
    
    ```java
    //팀1
    Team team1 = new Team("team1", "팀1");
    em.persist(team1);
    
    Member member1 = new Member("member1", "회원1");
    
    //양방향 연관관계 설정
    member1.setTeam(team);           //연관관계 설정 member1 -> team1
    team1.getMembers().add(member1); //연관관계 설정 team1 -> member1, 저장시 사용하지는 않음
    em.persist(member1);
    ```
    

### 6-2. 연관관계 편의 메소드

---

- 양방향 연관관계는 결국 양쪽 다 신경써야 한다.
    - 이렇게 각각 호출하다 보면 실수로 둘 중 하나만 호출해서 양방향이 깨질 수 있다.
        
        ```java
        public class Member {
            private Team team;
        
            public void setTeam(Team team) {
                this.team = team;
                team.getMembers().add(this); //연관관계 설정
            }
        }
        
        //연관관계 설정
        member1.setTeam(team1);
        ```
        
- 연관관계 편의 메소드(helper method): 한 번에 양방향 관계를 설정하는 메소드

### 6-3. 연관관계 편의 메소드 작성 시 주의사항

---

- 연관관계를 변경할 때는 기존 연관관계를 삭제하는 코드를 추가해야 한다.
    
    ```java
    member1.setTeam(teamA);
    member1.setTeam(teamB);
    Member findMember = teamA.getMember(); //member1이 여전히 조회
    ```
    
- 기존 관계 제거
    
    ```java
    public class Member {
        public void setTeam(Team team) {
            //기존 팀과 관계를 제거
            if (this.team != null) {
                this.team.getMembers().remove(this);
            }
            this.team = team;
            team.getMembers().add(this);
        }
    }
    ```
    

## 7. 정리

- 양방향의 장점: 반대방향으로 객체 그래프 탐색 기능이 추가된 것 뿐
- 정리
    - 단방향 매핑만으로 테이블과 객체의 연관관계 매핑은 이미 완료되었다.
    - 단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색 기능이 추가된다.
    - 양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야 한다.

- 연관관계의 주인을 정하는 기준
    - 외래 키의 위치와 관련해서 정해야지, 비즈니스 중요도로 접근하면 안된다.
- 양방향 매핑 시 무한 루프 주의
    - 엔티티를 JSON 으로 변환할 때 자주 발생한다.(`toString()` 무한루프)

- 참고
    - [https://backend.gitbooks.io/jpa/content/chapter5.html](https://backend.gitbooks.io/jpa/content/chapter5.html)
    - [https://ultrakain.gitbooks.io/jpa/content/chapter5/chapter5.2.html](https://ultrakain.gitbooks.io/jpa/content/chapter5/chapter5.2.html)
