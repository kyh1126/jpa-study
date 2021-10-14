# Chapter 6. 다양한 연관관계 매핑

- 엔티티의 연관관계를 매핑할 때 고려해야 할 3가지
    - 다중성
        - 다대일(`@ManyToOne`)
        - 일대다(`@OneToMany`)
        - 일대일(`@OneToOne`)
        - 다대다(`@ManyToMany`): 실무에서 거의 사용하지 않는다.
    - 단방향, 양방향
        - 단방향 관계: 객체 관계에서 한 쪽만 참조하는 것
        - 양방향 관계: 양쪽이 서로 참조하는 것
    - 연관관계의 주인
        - JPA 는 두 객체 연관관계 중 하나를 정해서 DB 외래 키를 관리한다.


## 1. 다대일

### 1-1. 다대일 단방향 [N:1]

---

- 회원 엔티티
    
    ```java
    @Entity
    public class Member {
        @Id
        @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;
    
        private String username;
    
        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;
        ....
    }
    ```
    
- 팀 엔티티
    
    ```java
    @Entity
    public class Team {
    
        @Id
        @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;
    
        private String name;
        ...
    }
    ```
    
- `@JoinColumn`을 사용해서 Member.team 필드를 TEAM_ID 외래키와 매핑
    
    → Member.team 필드로 회원 테이블의 TEAM_ID 외래키를 관리한다.
    

### 1-2. 다대일 양방향 [N:1, 1:N]

---

- 회원 엔티티
    
    ```java
    @Entity
    public class Member {
        @Id
        @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;
    
        private String username;
    
        @ManyToOne
        @JoinColumn(name = "TEAM_ID")
        private Team team;
    
        public void setTeam(Team team) {
            this.team = team;
    
            //무한루프에 빠지지 않도록 체크
            if(!team.getMembers().contains(this)) {
                team.getMembers().add(this);
            }
        }
    }
    ```
    
- 팀 엔티티
    
    ```java
    @Entity
    public class Team {
    
        @Id
        @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;
    
        private String name;
    
        @OneToMany(mappedBy = "team")
        private List<Member> members = new ArrayList<Member>();
    
        public void addMember(Member member) {
            this.members.add(member);
            if(member.getTeam() != this) { //무한루프에 빠지지 않도록 체크
                member.setTeam(this);
            }
        }
    }
    ```
    
- 다대일 관계의 반대방향은 항상 일대다, 일대다 관계의 반대방향은 항상 다대일 이다.
    - 양방향은 외래키가 있는 쪽이 연관관계의 주인이다.
    - 양방향 연관관계에서 주인은 항상 다(N)쪽이다.
- 양방향 연관관계는 항상 서로를 참조해야 한다.
    - 연관관계 편의 메소드: 한 곳에만 or 양쪽 다 작성할 수 있는데, 양쪽에 다 작성하면 무한루프에 빠지므로 주의해야 한다.
    - 위는 양쪽 다 작성해서 둘 중 하나만 호출하면 되는데, 무한루프에 빠지지 않도록 검사하는 로직도 있다.


## 2. 일대다

- 일대다 관계는 엔티티를 하나 이상 참조할 수 있으므로 자바 컬렉션 `Collection`, `List`, `Set`, `Map` 중에 하나를 사용해야 한다.

### 2-1. 일대다 단방향 [1:N]

---

- 일대다 단방향 팀 엔티티
    
    ```java
    @Entity
    public class Team {
    
        @Id @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;
    
        private String name;
    
        @OneToMany
        **@JoinColumn(name = "TEAM_ID") //MEMBER 테이블의 TEAM_ID (FK)**
        private List<Member> members = new ArrayList<Member>();
        ...
    }
    ```
    
- 일대다 단방향 회원 엔티티
    
    ```java
    @Entity
    public class Member {
    
        @Id @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;
    
        private String username;
        ...
    }
    ```
    
- 자신이 매핑한 테이블의 외래키가 아닌, 반대쪽 테이블에 있는 외래키를 관리한다.
- 일대다 단방향 관계를 매핑할 때는 `@JoinColumn`을 명시해야 한다. 그렇지 않으면 연결 테이블을 중간에 두고 조인 테이블 전략을 기본으로 사용해서 매핑한다.

- 일대다 단방향 매핑의 단점
    - 매핑한 객체가 관리하는 외래 키가 다른 테이블에 있다.(성능➕ 관리의 부담)
    - 본인 테이블에 외래 키가 있으면 엔티티의 저장과 연관관계 처리를 INSERT SQL 한 번으로 끝낼 수 있지만, 다른 테이블에 외래 키가 있으면 연관관계 처리를 위한 UPDATE SQL 을 추가로 실행해야 한다.
        
        ```java
        public void testSave() {
            Member member1 = new Member("member1");
            Member member2 = new Member("member2");
        
            Team team1 = new Team("team1");
            team1.getMembers().add(member1);
            team1.getMembers().add(member2);
        
            em.persist(member1); //INSERT-member1
            em.persist(member2); //INSERT-member2
            em.persist(team1);   //INSERT-team1, UPDATE-member1,2.fk
        
            transaction.commit();
        }
        ```
        

👉 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하자.

### 2-2. 일대다 양방향 [1:N, N:1]

---

- 일대대 양방향 매핑은 존재하지 않는다 → 다대일 양방향 매핑을 사용해야 한다.
    - 양방향 매핑에서 `@OneToMany`는 연관관계의 주인이 될 수 없다.
- 그래도, 일대다 양방향 = 일대다 단방향 + 다대일 단방향 을 구현해보자!
    - 일대다 단방향 매핑이 가지는 단점을 그대로 가진다.

- 일대다 양방향 팀 엔티티
    
    ```java
    @Entity
    public class Team {
        @Id @GeneratedValue
        @Column(name = "TEAM_ID")
        private Long id;
    
        private String name;
    
        @OneToMany
        **@JoinColumn(name = "TEAM_ID")**
        private List<Member> members = new ArrayList<Member>();
        ...
    }
    ```
    
- 일대다 양방향 회원 엔티티
    
    ```java
    @Entity
    public class Member {
        @Id @GeneratedValue
        @Column(name = "MEMBER_ID")
        private Long id;
    
        private String username;
    
        @ManyToOne
        **@JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)**
        private Team team;
        ...
    }
    ```
    
- 둘 다 같은 키를 관리하므로 문제가 발생할 수 있다.
- `insetable=false, updatable=false` 설정해서 읽기만 가능하게 해야한다.


## 3. 일대일 [1:1]

- 일대일 관계는 그 반대도 일대일 관계다.
- 주 테이블, 대상 테이블 둘 중 어느 곳이나 외래 키를 가질 수 있다 → 누가 외래 키를 가질지 선택해야 한다.
    - 주 테이블에 외래키: 주 객체가 대상 객체를 참조하는 것처럼 주 테이블에 외래 키를 두고 대상 테이블을 참조한다. 외래 키를 객체 참조와 비슷하게 사용할 수 있어서 객체지향 개발자들이 선호
        - 장점: 주 테이블이 외래 키를 가지고 있어서 주 테이블만 확인해도 대상 테이블과 연관관계가 있는지 알 수 있다.
    - 대상 테이블에 외래키: 전통적인 DB 개발자들은 보통 대상 테이블에 외래 키를 두는 것을 선호
        - 장점: 테이블 관계에서 일대일에서 일대다로 변경할 때 테이블 구조를 그대로 유지할 수 있다.

### 3-1. 주 테이블에 외래 키

---

- JPA 도 주 테이블에 외래 키가 있으면 좀 더 편리하게 매핑 가능
- 단방향: Member(주 테이블) → Locker(대상 테이블)
    - 일대일 주 테이블에 외래 키, 단방향
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled.png)
        
    - 일대일 주 테이블에 외래 키, 단방향 예제 코드
        
        ```java
        @Entity
        public class Member {
            @Id @GeneratedValue
            @Column(name = "MEMBER_ID")
            private Long id;
            private String username;
            @OneToOne
            @JoinColumn(name = "LOCKED_ID")
            private Locker locker;
            ...
        }
        
        @Entity
        public class Locker {
            @Id @GeneratedValue
            @Column(name = "LOCKED_ID")
            private Long id;
            private String name;
            ...
        }
        ```
        
- 양방향
    - 일대일 주 테이블에 외래 키, 양방향
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%201.png)
        
    - 일대일 주 테이블에 외래 키, 양방향 예제 코드
        
        ```java
        @Entity
        public class Member {
            @Id @GeneratedValue
            @Column(name = "MEMBER_ID")
            private Long id;
            private String username;
            @OneToOne
            @JoinColumn(name = "LOCKED_ID")
            private Locker locker;
            ...
        }
        
        @Entity
        public class Locker {
            @Id @GeneratedValue
            @Column(name = "LOCKED_ID")
            private Long id;
            private String name;
            @OneToOne(mappedBy = "locker")
            private Member member;
            ...
        }
        ```
        
    - 양방향이므로 연관관계의 주인을 정해야 한다.
    - MEMBER 테이블이 외래 키를 가지고 있으므로, Member.locker 가 연관관계의 주인이다.

### 3-2. 대상 테이블에 외래 키

---

- 단방향
    - 일대일 관계 중 대상 테이블에 외래 키가 있는 단방향 관계는 JPA 에서 지원하지 않는다.
        - 참고> JPA 2.0 부터 일대다 단방향 관계에서 대상 테이블에 외래 키가 있는 매핑을 허용함. 하지만 일대일 단방향은 이런 매핑을 허용하지 않는다.
        - 이 때는
            1. 단방향 관계를 Locker 에서 Member 방향으로 수정하거나
            2. 양방향 관계로 만들고 Locker 를 연관관계의 주인으로 설정해야 한다.
    - 일대일 대상 테이블에 외래 키, 단방향
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%202.png)
        
- 양방향
    - 일대일 대상 테이블에 외래 키, 양방향
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%203.png)
        
    - 일대일 대상 테이블에 외래 키, 양방향 예제 코드
        
        ```java
        @Entity
        public class Member {
            @Id @GeneratedValue
            @Column(name = "MEMBER_ID")
            private Long id;
            private String username;
            @OneToOne(mappedBy = "member")
            private Locker locker;
            ...
        }
        
        @Entity
        public class Locker {
            @Id @GeneratedValue
            @Column(name = "LOCKED_ID")
            private Long id;
            private String name;
            @OneToOne
            @JoinColumn(name = "MEMBER_ID")
            private Member member;
            ...
        }
        ```
        
    - 주 엔티티(Member) 대신, 대상 엔티티(Locker)를 연관관계의 주인으로 만들어서 LOCKER 테이블의 외래 키를 관리하도록 했다.

프록시 사용 시 외래 키를 직접 관리하지 않는 일대일 관계는 지연 로딩으로 설정해도 즉시 로딩된다. → Member.locker 는 지연 로딩할 수 있지만, Locker.member 는 지연 로딩으로 설정해도 즉시 로딩된다. 이것은 프록시의 한계 때문에 발생하는 문제, 프록시 대신 `bytecode instrumentation`을 사용하면 해결할 수 있다.


## 4. 다대다 [N:N]

- RDB 는 정규화된 테이블 2개로 다대다 관계를 표현할 수 없다.
    
    ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%204.png)
    
    → 보통 다대다 관계를 일대다, 다대일 관계로 풀어내는 연결 테이블을 사용한다.
    
    ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%205.png)
    
- 그런데 객체는 테이블과 다르게 객체 2개로 다대다 관계를 만들 수 있다.
    - ex> 회원 객체는 컬렉션을 사용해 상품들 참조, 반대로 상품들도 컬렉션을 사용해 회원들 참조
    - `@ManyToMany`를 사용하여 다대다 관계를 편리하게 매핑할 수 있다.

### 4-1. 다대다: 단방향

---

- 다대다 단방향 회원
    
    ```java
    @Entity
    public class Member {
        @Id @Column(name = "MEMBER_ID")
        private String id;
        private String username;
        @ManyToMany
        @JoinTable(name = "MEMBER_PRODUCT", // 연결 테이블 지정
            joinColumns = @JoinColumn(name = "MEMBER_ID"), // joinColumns: 현재 방향인 회원과 매핑할 조인 컬럼 정보를 지정, MEMBER_ID로 지정함.
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID")) // 반대 방향인 상품과 매핑할 조인 컬럼 정보를 지정. PRODUCT_ID로 지정함.
        private List<Product> products = new ArrayList<Product>();
        ...
    }
    ```
    
- 다대다 단방향 상품
    
    ```java
    @Entity
    public class Product {
        @Id @Column(name = "PRODUCT_ID")
        private String id;
        private String name;
        ...
    }
    ```
    
- 회원과 상품 엔티티를 `@ManyToMany`로 매핑
- `@ManyToMany`와 `@JoinTable`을 사용해서 연결 테이블을 바로 매핑
- 따라서 회원과 상품을 연결하는 회원_상품(Member_Product) 엔티티 없이 매핑을 완료할 수 있다.

- `@JoinTable`의 속성
    - `name`: 연결테이블을 지정
    - `joinColumns`: 현재 방향인 회원과 매핑할 조인 컬럼 정보를 지정
    - `inverseJoinColumns:` 반대 방향인 상품과 매핑할 조인 컬럼 정보를 지정

- 저장
    
    ```java
    public void save(){
        Product productA = new Product();
        productA.setId("productA");
        productA.setName("상품A");
        em.persist(productA);
    
        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        member1.getProducts().add(productA); // 연관관계 설정
        em.persist(member1);
    }
    ```
    
    - 회원1 과 상품A 의 연관관계를 설정했으므로 회원1 을 저장할 때 연결 테이블에도 값이 저장된다.
- `@ManyToMany`로 매핑을 했지만, DB 에서는 다대다 매핑이 안되기 때문에 연결 테이블이 자동으로 생성되고 거기에도 데이터가 쌓인다!
    
    ```sql
    INSERT INTO PRODUCT ...
    INSERT INTO MEMBER ...
    INSERT INTO MEMBER_PRODUCT ...
    ```
    

- 탐색
    
    ```java
    public void find() {
        Member member = em.find(Member.class, "member1");
        List<Product> products = member.getProducts(); // 객체 그래프 탐색
        for(Product product : products){
            System.out.println("product.name = " + product.getName());
        }
    }
    ```
    
    - member.getProducts() 호출하여 상품 이름을 출력하면 다음 SQL 이 실행된다.
        
        ```sql
        SELECT * FROM MEMBER_PRODUCT MP
        INNER JOINPRODUCT P ON MP.PRODUCT_ID = P.PRODUCT_ID
        WHERE MP.MEMBER_ID = ?
        ```
        
    - 탐색 시 연결 테이블인 MEMBER_PRODUCT 와 상품 테이블을 조인해서 연관된 상품을 조회한다.

### 4-2. 다대다: 양방향

---

- 역방향 추가
    
    ```java
    @Entity
    public class Product {
        @Id
        private String id;
        @ManyToMany(mappedBy = "products") // 역방향 추가
        private List<Member> members;
        ...
    }
    ```
    
- 양방향 연관관계는 연관관계 편의 메소드를 추가해서 관리하는 것이 편리하다.
    
    ```java
    public void addProduct(Product product){
        ...
        products.add(product);
        product.getMembers().add(this);
      }
    ```
    
- 이후 member.addProduct(product) 처럼 간단히 양방향 연관관계 설정

- 역방향 탐색
    
    ```java
    public voidfindInverse(){
        Product product = em.find(Product.class, "productA");
        List<Member> members = product.getMembers();
        for(Member member : members){
            System.out.println("member = " + member.getUsername());
        }
    }
    ```
    

### 4-3. 다대다: 매핑의 한계와 극복, 연결 엔티티 사용

---

- `@ManyToMany`를 사용하면 연결 테이블을 자동으로 처리해주므로 도메인 모델이 단순해지고 여러 가지로 편리하다.
- 하지만 실무에서 사용하기에는 한계가 있다.
    - ex> 회원이 상품을 주문하면연결 테이블에 단순히 주문한 회원 아이디와 상품 아이디만 담고 끝나지 않는다. 예를 들면 보통은 연결 테이블에 주문 수량 컬럼이나 주문한 날짜 같은 컬럼이 더 필요하다.
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%206.png)
        
    - 따라서 위와 같은 구조에서 연결 테이블에 주문 수량(ORDERAMOUNT)과 주문 날짜(ORDERDATE) 컬럼을 추가한다.
    - 이렇게 컬럼을 추가하면 더 이상 `@ManyToMany`를 사용할 수 없다. → 왜냐하면 주문 엔티티나 상품 엔티티에는 추가한 컬럼들을 매핑할 수 없기 때문이다.
    - 결국 연결 테이블을 매핑하는 연결 엔티티를 만들고 이곳에 추가한 컬럼들을 매핑해야 한다.
    - 그리고 엔티티 간의 관계도 테이블 관계처럼 다대다에서 일대다, 다대일 관계로 풀어야 한다.
        
        ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%207.png)
        
- 연결 테이블에 주문 수량(ORDERAMOUNT)과 주문 날짜(ORDERDATE) 컬럼을 추가했다.
- 회원 코드
    
    ```java
    @Entity
    public class Member {
        @Id @Column(name = "MEMBER_ID")
        private String id;
        // 역방향
        @OneToMany(mappedBy = "member")
        private List<MemberProduct> memberProducts;
        ...
    }
    ```
    
    - 회원과 회원상품은 양방향 관계
    - 회원상품 엔티티 쪽이 외래 키를 갖고 있으므로 연관관계의 주인이다.
- 상품 코드
    
    ```java
    @Entity
    public class Product {
        @Id @Column(name = "PRODUCT_ID")
        private String id;
        private String name;
        ...
    }
    ```
    
    - 상품 엔티티에서 회원상품 엔티티로 객체 그래프 탐색 기능이 필요하지 않다고 판단해서 연관관계 X
- 회원상품 엔티티 코드
    - `@id` 와 외래 키를 매핑하는 `@JoinColumn`을 동시에 사용해서 기본 키 + 외래 키를 한 번에 매핑
    - 그리고 `@IdClass` 를 사용해서 복합 기본 키를 매핑
    
    ```java
    @Entity
    @IdClass(MemberProductId.class) // 복합 키를 사용하기 위한 식별자 클래스
    public class MemberProduct {
        @Id
        @ManyToOne
        @JoinColumn(name = "MEMBER_ID")
        private Member member; // MemberProductId.member와 연결
        @Id
        @ManyToOne
        @JoinColumn(name = "PRODUCT_ID")
        private Product product; // MemberProductId.product 연결
        private int orderAmount;
        ...
    }
    ```
    
- 회원상품 식별자 클래스
    
    ```java
    public class MemberProductId implements Serializable {
        private String member; // MemberProduct.member와 연결
        private String product; // MemberProduct.product와 연결
        // hashCode and equals
        @Override
        public boolean equals(Object o) {...}
        @Override
        public int hashCode() {...}
    }
    ```
    

- 복합 기본 키
    - 회원상품 엔티티는 기본 키가 `MEMBER_ID`와 `PRODUCT_ID`로 이루어진 복합 기본 키다.
    - JPA 에서 복합 키를 사용하려면 별도의 식별자 클래스를 만들어야 한다.
    - 그리고 엔티티에 `@IdClass`를 사용해서 식별자 클래스를 지정해준다.

- 복합 키를 위한 식별자 클래스 특징
    - 복합 키는 별도의 식별자 클래스로 만들어야 한다.
    - `Serializable`을 구현해야 한다.
    - `equals`와 `hashCode` 메소드를 구현해야 한다.
    - 기본 생성자가 있어야 한다.
    - 식별자 클래스는 `public`이어야 한다.
    - `@IdClass`를 사용하는 방법 외에 `@EmbeddedId`를 사용하는 방법도 있다.

- 식별 관계
    - 부모 테이블의 기본 키를 받아서 자신의 기본 키 + 외래 키로 사용하는 것
    - 회원상품은
        1. 회원의 기본 키를 받아서 자신의 기본 키로 사용함과 동시에 회원과의 관계를 위한 외래 키로 사용
        2. 상품의 기본 키를 받아서 자신의 기본 키로 사용함과 동시에 상품과의 관계를 위한 외래 키로 사용
    - `MemberProductId` 식별자 클래스로 두 기본 키를 묶어서 복합 기본 키로 사용

- 저장
    
    ```java
    public void save() {
        // 회원 저장
        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        em.persist(member1);
    
        // 상품 저장
        Product productA = new Product();
        productA.setId("productA");
        productA.setName("상품1");
        em.persist(productA);
    
        // 회원상품 저장
        MemberProduct memberProduct = new MemberProduct();
        memberProduct.setMember(member1); // 주문 회원 - 연관관계 설정
        memberProduct.setProduct(productA); // 주문 상품 - 연관관계 설정
        memberProduct.setOrderAmount(2); // 주문 수량
    
        em.persist(memberProduct);
    }
    ```
    
    - 데이터베이스 저장 시 연관된 회원의 식별자와 상품의 식별자를 가져와서 자신의 기본 키 값으로 사용

- 조회
    - 복합 키는 항상 식별자 클래스를 만들어야 한다.
    - `em.find()`를 통해 생성한 식별자 클래스로 엔티티를 조회한다.
    
    ```java
    public void find() {
        // 기본 키 값 생성
        MemberProductId memberProductId = new MemberProductId();
        memberProductId.setMember("member1");
        memberProductId.setProduct("productA");
    
        MemberProduct memberProduct = em.find(MemberProduct.class, memberProductId);
    
        Member member = memberProduct.getMember();
        Product product = memberProduct.getProduct();
    
        System.out.println("member = " + member.getUsername());
        System.out.println("product = " + product.getName());
        System.out.println("orderAmount = " + memberProduct.getOrderAmount());
    }
    ```
    

복합 키를 사용하면 ORM 매핑에서 처리할 일이 상당히 많아진다 - 복합 키를 위한 식별자 클래스, `@IdClass` 또는 `@EmbeddedId` 사용, `equals`와 `hashCode` 구현 필요

### 4-4. 다대다: 새로운 기본 키 사용

---

- 추천 기본 키 생성 전략: 데이터베이스에서 자동으로 생성해주는 대리 키를 `Long` 값으로 사용하는 것
- 장점
    - 간편하고 거의 영구적으로 쓸 수 있으며 비즈니스에 의존하지 않는다.
    - ORM 매핑 시 복합 키를 만들지 않아도 되므로 간단히 매핑을 완성할 수 있다.
- 회원상품 대신 주문(Order) 이라는 이름을 사용하는 것이 더 어울린다. → ORDERS 사용
    
    ![Untitled](Chapter%206%20%E1%84%83%E1%85%A1%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%92%E1%85%A1%E1%86%AB%20%E1%84%8B%E1%85%A7%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%AA%E1%86%AB%E1%84%80%E1%85%A8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20db2e31651bec4c8ca5f8ae089aefd61c/Untitled%208.png)
    

- 주문 코드
    - 대리 키를 사용함으로써 이전에 식별 관계에서의 복합 키 사용 보다 매핑이 단순하고 이해가 쉬워졌다.
    
    ```java
    @Entity
    public class Order {
        @Id @GeneratedValue
        @Column(name = "ORDER_ID")
        private Long id;
    
        @ManyToOne
        @JoinColumn(name = "MEMBER_ID")
        private Member member;
    
        @ManyToOne
        @JoinColumn(name = "PRODUCT_ID")
        private Product product;
    
        private int orderAmount;
        ...
    }
    ```
    
- 회원 엔티티와 상품 엔티티
    
    ```java
    import java.util.ArrayList;
    
    @Entity
    public class Member {
        @Id
        @Column(name = "MEMBER_ID")
        private String id;
        private String username;
    
        @OneToMany(mappedt = "member")
        private List<Order> orders = new ArrayList<Order>();
        ...
    }
    
    @Entity
    public class Product {
        @Id
        @Column(name = "PRODUCT_ID")
        private String id;
        private String name;
    }
    ```
    
- 저장
    
    ```java
    public void save() {
        // 회원 저장
        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        em.persist(member1);
    
        // 상품 저장
        Product productA = new Product();
        productA.setId("productA");
        productA.setName("상품1");
        em.persist(productA);
    
        // 주문 저장
        Order order = new Order();
        order.setMember(member1); // 주문 회원 - 연관관계 설정
        order.setProduct(productA); // 주문 상품 - 연관관계 설정
        order.setOrderAmount(2); // 주문 수량량
        m.persist(order);
    }
    ```
    
- 조회
    - 식별자 클래스를 사용하지 않아 코드가 단순해짐
    
    ```java
    public void find() {
        Long orderId = 1L;
        Order order = em.find(Order.class, orderId);
    
        Member member= order.getMember();
        Product product =order.getProduct();
    
        System.out.println("member = " + member.getUsername());
        System.out.println("product = " + product.getName());
        System.out.println("orderAmount = " + order.getOrderAmount());
    }
    ```
    

### 4-5. 다대다 연관관계 정리

---

- 식별 관계 : 받아온 식별자를 기본 키 + 외래 키로 사용한다.
    - 부모 테이블의 기본 키를 받아서 자식 테이블의 기본 키 + 외래 키로 사용하는 것
- 비식별 관계 : 받아온 식별자는 외래 키로만 사용하고 새로운 식별자를 추가한다.
    - 단순히 외래 키로만 사용, 기본 키는 자동 생성 전략으로 대리 키 생성
    - 단순하고 편리하게 ORM 매핑 가능 → 사용 추천

- 참고
    - [https://www.nowwatersblog.com/jpa/ch6/6-3](https://www.nowwatersblog.com/jpa/ch6/6-3)
    - [https://backend.gitbooks.io/jpa/content/chapter6.html](https://backend.gitbooks.io/jpa/content/chapter6.html)
