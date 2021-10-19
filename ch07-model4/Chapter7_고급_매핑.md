# Chapter 7. 고급 매핑

## 1. 상속 관계 매핑

- ORM 상속 관계 매핑: 객체의 상속 구조와 DB 의 슈퍼타입 서브타입 관계를 매핑하는 것
    - ORM 에는 객체지향 언어에서 다루는 상속이라는 개념이 없다.
- 슈퍼타입 서브타입 논리 모델 → 실제 물리 모델인 테이블로 구현 3가지 방법
    - 각각의 테이블로 변환: 각각을 모두 테이블로 만들고 조회할 때 조인을 사용한다. JPA 에서는 `조인 전략`이라 한다.
    - 통합 테이블로 변환: 테이블을 하나만 사용해서 통합한다. JPA 에서는 `단일 테이블 전략`이라 한다.
    - 서브타입 테이블로 변환: 서브 타입마다 하나의 테이블을 만든다. JPA 에서는 `구현 클래스마다 테이블 전략`이라 한다.

### 1-1. 조인 전략

---

- 엔티티 각각을 모두 테이블로 만들고, 자식 테이블이 부모 테이블의 기본 키를 받아서 기본 키 + 외래 키로 사용하는 전략
- 조회할 때 조인을 자주 사용
- 타입을 구분하는 컬럼을 추가해야 한다.
    - 객체는 타입으로 구분할 수 있지만, 테이블은 타입의 개념이 없다.

- JOINED TABLE
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled.png)
    
- 조인 전략 매핑
    
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.JOINED) // 상속 매핑은 부모 클래스에 @Inheritance 를 사용해야 한다. 그리고 매핑 전략을 지정해준다 => 조인 전략 사용
    @DiscriminatorColumn(name = "DTYPE") // 부모 클래스에 구분 컬럼을 지정. 이 컬럼으로 저장된 자식 테이블을 구분할 수 있다. 기본값이 DTYPE 이므로 @DiscriminatorColumn 으로 줄여쓸 수 있다.
    public abstract class Item {
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
        private String name;
        private int price;
        ...
    }
    @Entity
    @DiscriminatorValue("A")
    public class Album extends Item {
        private String artist;
        ...
    }
    @Entity
    @DiscriminatorValue("M") // 엔티티 저장 시 구분 컬럼에 입력할 값을 지정. 엔티티를 저장하면 구분 컬럼 DTYPE에 값 M이 저장된다.
    public class Movie extends Item {
        private String director;
        private String actor;
        ...
    }
    ```
    
    - `@DiscriminatorColumn(name = "..")`: 구분 컬럼 지정
    - `@DiscriminatorValue`: 구분 컬럼 데이터 지정
- ID 재정의
    
    ```java
    @Entity
    @DiscriminatorValue("B")
    @PrimaryKeyJoinColumn(name="BOOK_ID") // 기본 키 컬럼명 변경
    public class Book extends Item {
        private String author;
        private String isbn;
        ...
    }
    ```
    
    - `@PrimaryKeyJoinColumn`: 자식 테이블의 기본 키 이름 변경
- 장점
    - 테이블이 정규화된다.
    - 외래키 참조 무결성 제약조건을 활용할 수 있다.
    - 저장 공간을 효율적으로 사용한다.
- 단점
    - 조회할 때 조인이 많이 사용되므로 성능이 저하될 수 있다.
    - 조회 쿼리가 복잡하다.
    - 데이터를 등록할 INSERT SQL 을 두 번 실행한다.
- 특징
    - JPA 표준 명세는 구분 컬럼을 사용하도록 하지만 하이버네이트를 포함한 몇몇 구현체는 구분 컬럼(`@DiscriminatorColumn`) 없이도 동작한다.

### 1-2. 단일 테이블 전략

---

- 테이블을 하나만 사용한다.
- 구분 컬럼(DTYPE)으로 어떤 자식 데이터가 저장되었는지 구분
- 자식 엔티티가 매핑한 컬럼은 모두 `null` 허용해야 한다.

- SINGLE TABLE
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%201.png)
    
- 단일 테이블 전략 매핑
    
    ```java
    @Entity
    **@Inheritance(strategy = InheritanceType.SINGLE_TABLE)** // 테이블 하나에 모든 것을 통합, 구분 컬럼 필수 => 단일 테이블 전략
    @DiscriminatorColumn(name = "DTYPE") // 부모 클래스에 구분 컬럼을 지정. 이 컬럼으로 저장된 자식 테이블을 구분할 수 있다.
    public abstract class Item {
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
        private String name;
        private int price;
        public void change(Long id, String name, int price){
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
    
    @Entity
    @DiscriminatorValue("A")
    public class Album extends Item {
        private String author;
        private String isbn;
        public Album(Long id, String name, int price, String author, String isbn){
            super.change(id, name, price); // 부모 클래스의 정보를 넣어줌.
            this.author = author;
            this.isbn = isbn;
        }
    }
    
    @Entity
    @DiscriminatorValue("M") // 엔티티 저장 시 구분 컬럼에 입력할 값을 지정.
    public class Movie extends Item {
        ...
    }
    
    @Entity
    @DiscriminatorValue("B")
    public class Book extends Item {
        ...
    }
    ```
    
- 장점
    - 조인이 필요 없으므로 일반적으로 조회 성능이 빠르다.
    - 조회 쿼리가 단순하다.
- 단점
    - 자식 엔티티가 매핑한 컬럼은 모두 `null`을 허용해야 한다.
    - 단일 테이블에 모든 것을 저장하므로 테이블이 커질 수 있다. 상황에 따라 조회 성능이 오히려 느려질 수 있다.
- 특징
    - 구분 컬럼(`@DiscriminatorColumn`)을 꼭 사용해야 한다.
    - `@DiscriminatorValue`를 지정하지 않으면 기본으로 엔티티 이름을 사용한다.

### 1-3. 구현 클래스마다 테이블 전략

---

- 자식 엔티티마다 하나의 테이블을 만든다. 각각에 필요한 컬럼이 모두 있다.
- 일반적으로 추천하지 않는 전략이다.

- CONCRETE TABLE
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%202.png)
    
- 구현 클래스마다 테이블 전략 매핑
    
    ```java
    @Entity
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 자식 테이블마다 테이블 생성
    public abstract class Item {
        @Id @GeneratedValue
        @Column(name = "ITEM_ID")
        private Long id;
        private String name;
        private int price;
        ...
    }
    @Entity
    @DiscriminatorValue("A")
    public class Album extends Item {
        ...
    }
    
    @Entity
    @DiscriminatorValue("M") // 엔티티 저장 시 구분 컬럼에 입력할 값을 지정.
    public class Movie extends Item {
        ...
    }
    
    @Entity
    @DiscriminatorValue("B")
    public class Book extends Item {
        ...
    }
    ```
    
- 장점
    - 서브 타입을 구분해서 처리할 때 효과적이다.
    - `not null` 제약 조건을 사용할 수 있다.
- 단점
    - 여러 자식 테이블을 함께 조회할 때 성능이 느리다.(SQL 에 `UNION`을 사용해야 한다)
    - 자식 테이블을 통합해서 쿼리하기 어렵다.
- 특징
    - 구분 컬럼을 사용하지 않는다.


## 2. `@MappedSuperclass`

- `@MappedSuperclass`: 부모 클래스는 테이블과 매핑하지 않고 부모 클래스를 상속받는 자식 클래스에게 매핑 정보만 제공하고 싶을 때 사용. 실제 테이블과는 매핑되지 않는다.

- `@MappedSuperclass` 설명
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%203.png)
    
- `@MappedSuperclass` 매핑
    
    ```java
    **@MappedSuperclass** //테이블과 매핑할 필요가 없고 자식 엔티티에게 공통으로 사용되는 매핑 정보만 제공
    ****public abstract class BaseEntity {
        @Id @GeneratedValue
        private Long id;
        private String name;
        ...
    }
    
    @Entity
    public class Member extends BaseEntity {
        // ID 상속
        // NAME 상속
        private String email;
        ...
    }
    
    @Entity
    public class Seller extends BaseEntity {
        // ID 상속
        // NAME 상속
        private String shopName;
        ...
    }
    ```
    

- `@AttributeOverride`/`@AssociationOverride`
    - `@AttributeOverride`: 부모로부터 물려받은 매핑 정보를 재정의
        
        ```java
        @Entity
        @AttributeOverride(name="id", column= @Column(name="MEMBER_ID"))
        public class Member extends BaseEntity {...}
        ```
        
    - 둘 이상을 재정의: `@AttributeOverrides`
        
        ```java
        @Entity
        @AttributeOverrides({
          @AttributeOverride(name="id", column= @Column(name="MEMBER_ID")),
          @AttributeOverride(name="name", column= @Column(name="MEMBER_NAME"))
        })
        public class Member extends BaseEntity {...}
        ```
        
        - JPA 2.2(JSR 338), [Hibernate 5.3](https://hibernate.org/orm/releases/) 이상부터 `@AttributeOverride` 중복 정의로 사용 가능
    - `@AssociationOverride`: 부모로부터 물려받은 연관관계를 재정의
- `@MappedSuperclass`의 특징
    - 테이블과 매핑되지 않고 자식 클래스에 엔티티의 매핑 정보를 상속하기 위해 사용
    - `@MappedSuperclass`로 지정한 클래스는 엔티티가 아니므로 `em.find()`나 JPQL 에서 사용할 수 없다.
    - 이 클래스를 직접 생성해서 사용할 일은 거의 없으므로 추상 클래스로 만드는 것을 권장한다.
- `@MappedSuperclass`를 사용하면 등록일자, 수정일자, 등록자, 수정자 같은 여러 엔티티에서 공통으로 사용하는 속성을 효과적으로 관리할 수 있다.

참고로, 엔티티는 엔티티이거나 `@MappedSuperclass`로 지정한 클래스만 상속받을 수 있다.


## 3. 복합 키와 식별 관계 매핑

### 3-1. 식별 관계 vs 비식별 관계

---

- DB 테이블 사이의 관계는 외래키가 기본키에 포함되는지 여부에 따라 식별 관계와 비식별 관계로 구분한다.
    - 식별관계(Identitying Relationship)
        - 부모 테이블의 기본 키를 내려받아서 자식 테이블의 기본 키 + 외래 키로 사용하는 관계
            
            ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%204.png)
            
    - 비식별관계(Non-Identifying Relationship)
        - 부모 테이블의 기본 키를 받아서 자식 테이블의 외래 키로만 사용하는 관계
            
            ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%205.png)
            
        - 필수적 비식별 관계(Mandatory): 외래키에 `NULL`을 허용하지 않는다.
        - 선택적 비식별 관계(Optional): 외래키에 `NULL`을 허용한다.

### 3-2. 복합 키: 비식별 관계 매핑

---

- JPA 에서 식별자를 둘 이상 사용하려면 별도의 식별자 클래스를 만들어야 한다.
- 식별자 필드가 2개 이상이면 `equals` 와 `hashCode`를 구현해야 한다.
    - JPA 는 영속성 컨텍스트에 엔티티를 보관할 때 엔티티의 식별자를 키로 사용한다.
    - 그리고 식별자 구분을 위해 `equals` 와 `hashCode`를 사용해서 동등성 비교를 한다.
- JPA 는 복합키를 지원하기 위해 `@IdClass`와 `@EnbeddedId`2가지 방법을 제공한다.
    - `@IdClass`: 관계형 데이터베이스에 가까운 방법
    - `@EmbeddedId`: 객체지향에 가까운 방법

- `@IdClass`
    - 예시
        - 부모 클래스
            
            ```java
            // 부모 클래스
            @Entity
            @IdClass(ParentId.class)
            public class Parent{
                @Id
                @Column(name = "PARENT_ID1")
                private String id1; // ParentId.id1과 연결
                @Id
                @Column(name = "PARENT_ID2")
                private String id2; // ParentId.id2와 연결
                private String name;
                ...
            }
            ```
            
        - 식별자 클래스
            
            ```java
            import java.io.Serializable;
            
            // 식별자 클래스
            public class ParentId implements Serializable {
                private String id1; // Parent.id1 매핑
                private String id2; // Parent.id2 매핑
            
                public ParentId(){
                }
                public ParentId(String id1, String id2){
                    this.id1 = id1;
                    this.id2 = id2;
                }
            
                @Override
                public boolean equals(Object o) {...}
                @Override
                public int hashCode() {...}
            }
            ```
            
        - 자식 클래스
            
            ```java
            @Entity
            public class Child {
                @Id
                private String id;
                @ManyToOne
                @JoinColumn(name = "PARENT_ID1", referencedColumnName = "PARENT_ID1")
                @JoinColumn(name = "PARENT_ID2", referencedColumnName = "PARENT_ID2")
                // name 과 referencedColumnName 이 같으면 referencedColumnName 은 생략 가능하다.
                private Parent parent;
            }
            ```
            
            - 부모 테이블 기본 키 컬럼이 복합 키 → 자식 테이블 외래 키도 복합 키
            - 외래 키 매핑 시 여러 컬럼을 매핑해야 함: `@JoinColumns`
                - JPA 2.2(JSR 338), [Hibernate 5.3](https://hibernate.org/orm/releases/) 이상부터 `@JoinColumn` 중복 정의로 사용 가능
        - 복합 키를 사용하는 엔티티 저장
            
            ```java
            Parent parent = new Parent();
            parent.setId("myid1");
            parent.setId("myid2");
            em.persist(parent);
            ```
            
            ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%206.png)
            
        - 복합 키로 조회
            
            ```java
            ParentId parentId = new ParentId("myId1", "myId2");
            Parent findParent = em.find(Parent.class, parentId);
            ```
            
    - `@IdClass`를 사용할 때 식별자 클래스 조건
        - 식별자 클래스의 속성명과 엔티티에서 사용하는 식별자의 속성명이 같아야 한다.
            - ex> Parent.id1 과 ParentId.id1 가 같아야 한다.
        - `Serializable` 인터페이스를 구현해야 한다.
        - `equals`, `hashCode`를 구현해야 한다.
        - 기본 생성자가 있어야 한다.
        - 식별자 클래스는 `public`이어야 한다.
- `@EmbeddedId`
    - 예시
        - 부모 클래스에 식별자 클래스를 직접 사용하고 `@EmbeddedId` 어노테이션을 적어주면 된다.
            
            ```java
            @Entity
            public class Parent {
                @EmbeddedId
                private ParentId id;
                private String name;
                ...
            }
            ```
            
        - 식별자 클래스
            - `@IdClass` 와 달리, 식별자 클래스에 기본키를 직접 매핑한다 → `@Column(name = "PARENT_ID1")`를 안적어주면 안된다.
            
            ```java
            import java.io.Serializable;
            
            @Embeddable
            public class ParentId implements Serializable {
                @Column(name = "PARENT_ID1")
                private String id1;
                @Column(name = "PARENT_ID2")
                private String id2;
                // equals and hashCode 구현
                ...
            }
            ```
            
        - 복합 키를 사용하는 엔티티 저장
            
            ```java
            Parent parent = new Parent();
            ParentId parentId = new ParentId("myId1", "myId2");
            parent.setId(parentId);
            em.persist(parent);
            ```
            
            ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%206.png)
            
    - `@EmbeddedId`를 사용할 때 식별자 클래스 조건
        - `@Embeddable` 어노테이션을 붙여주어야 한다.
        - `Serializable` 인터페이스를 구현해야 한다.
        - `equals`, `hashCode`를 구현해야 한다.
        - 기본 생성자가 있어야 한다.
        - 식별자 클래스는 `public`이어야 한다.

- 복합 키와 `equals()`, `hashCode()`
    - 복합 키는 `equals()` 와 `hashCode()`를 필수로 구현해야 한다.
        - 자바의 모든 클래스는 기본으로 `Object` 클래스를 상속받는데, 이 클래스가 제공하는 기본 `equals()`는 인스턴스 참조 값 비교인 `==` 비교(동일성 비교)를 한다.
        - 따라서 `id1.equals(id2) -> ?` 는 `equals()`를 적절히 오버라이딩 했다면 참이고, 그렇지 않다면 거짓이다.
- `@IdClass` vs `@EmbeddedId`
    - `@EmbeddedId`가 `@IdClass`와 비교해서 더 객체지향적이고 중복도 없어서 좋아보이긴 하지만 특정 상황에 JPQL 이 좀 더 길어질 수 있다.
        
        ```java
        em.createQuery("select p.id.id1, p.id.id2 from Parent p"); // @EmbeddedId
        em.createQuery("select p.id1, p.id2 from Parent p"); // @IdClass
        ```
        

복합 키에는 `@GeneratedValue`를 사용할 수 없다. 복합 키를 구성하는 여러 컬럼 중 하나에도 사용할 수 없다.

### 3-3. 복합 키: 식별 관계 매핑

---

- 식별 관계 구현
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%207.png)
    
- 식별 관계에서는 자식은 부모의 기본 키를 포함해서 복합 키를 구성해야 한다.

- `@IdClass`와 식별 관계
    - `@IdClass`로 식별 관계 매핑하기
        
        ```java
        import java.io.Serializable;
        
        // 부모
        @Entity
        public class Parent {
          @Id
          @Column(name = "PARENT_ID")
          private String id;
          private String name;
            ...
        }
        // 자식
        @Entity
        @IdClass(ChildId.class)
        public class Child {
          @Id
          @ManyToOne
          @JoinColumn(name = "PARENT_ID")
          public Parent parent;
          @Id
          @Column(name = "CHILD_ID")
          private String childId;
          private String name;
            ...
        }
        // 자식 ID
        public class ChildId implements Serializable{
            private String parent; // Child.parent 매핑
            private String childId; // Child.childId 매핑
            // equals, hashCode
            ...
        }
        // 손자
        @Entity
        @IdClass(GrandChildId.class)
        public class GrandChild {
            @Id
            @ManyToOne
            @JoinColumns({
                    @JoinColumn(name = "PARENT_ID"),
                    @JoinColumn(name = "CHILD_ID")
            })
            public Child child;
            @Id @Column(name = "GRANDCHILD_ID")
            private String id;
            private String name;
        }
        // 손자 ID
        public class GrandChildId implements Serializable{
            private ChildId child; // GrandChild.child 매핑
            private String id; // GrandChild.id 매핑
            // equals, hashCode
            ...
        }
        ```
        
    - 식별 관계는 기본 키와 외래 키를 같이 매핑해야 한다.
        
        → 식별자 매핑인 `@Id`와 연관관계 매핑인 `@ManyToOne`을 같이 사용하면 된다.
        
- `@EmbeddedId`와 식별 관계
    - `@EmbeddedId` 식별 관계 매핑하기
        
        ```java
        import java.io.Serializable;
        
        // 부모
        @Entity
        public class Parent {
          @Id
          @Column(name = "PARENT_ID")
          private String id;
          private String name;
            ...
        }
        // 자식
        @Entity
        public class Child {
          @EmbeddedId
          private ChildId childId;
          @MapsId("parentId") // ChildId.parentId 매핑
          @ManyToOne
          @JoinColumn(name = "PARENT_ID")
          public Parent parent;
          private String name;
            ...
        }
        // 자식 ID
        @Embeddable
        public class ChildId implements Serializable {
            private String parentId; // @MapsId("parentId")로 매핑
            @Column(name = "CHILD_ID")
            private String id;
            //equals, hashCode
            ...
        }
        // 손자
        @Entity
        public class GrandChild {
            @EmbeddedId
            private GrandChildId id;
            @MapsId("childId") // GrandChildId.childId 매핑
            @ManyToOne
            @JoinColumns({
                @JoinColumn(name = "PARENT_ID"),
                @JoinColumn(name = "CHILD_ID")
            })
            public Child child;
            private String name;
            ...
        }
        // 손자 ID
        @Embeddable
        public class GrandChildId implements Serializable{
            private ChildId childId; // @MapsId("childId")로 매핑
            @Column(name = "GRANDCHILD_ID")
            private String id; //
            // equals, hashCode
            ...
        }
        ```
        
    - `@EmbeddedId`는 식별 관계로 사용할 연관관계의 속성에 `@MapsId`를 사용하면 된다.
    - `@IdClass`와 다른 점은 `@Id` 대신에 `@MapsId` 를 사용한 점이다.
    - `@MapsId`는 외래 키와 매핑한 연관관계를 기본 키에도 매핑하겠다는 뜻이다.
    - `@MapsId` 속성 값은 `@EmbeddedId` 를 사용한 식별자 클래스의 기본 키 필드를 지정하면 된다.

### 3-4. 비식별 관계로 구현

---

- 비식별 관계로 변경
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%208.png)
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%209.png)
    
- 비식별 관계 매핑하기
    
    ```java
    // 부모
    @Entity
    public class Parent {
        @Id @GeneratedValue
        @Column(name = "PARENT_ID")
        private Long id;
        private String name;
        ...
    }
    
    // 자식
    @Entity
    public class Child {
        @Id @GeneratedValue
        @Column(name = "CHILD_ID")
        private Long id;
        private String name;
        @ManyToOne
        @JoinColumn(name = "PARENT_ID")
        private Parent parent;
        ...
    }
    
    // 손자
    @Entity
    public class GrandChild {
        @Id @GeneratedValue
        @Column(name = "GRANDCHILD_ID")
        private Long id;
        private String name;
        @ManyToOne
        @JoinColumn(name = "CHILD_ID")
        private Child child;
        ...
    }
    ```
    
- 식별 관계의 복합 키를 사용한 코드에 비해 매핑도 쉽고 코드도 단순하다.
- 그리고 복합 키가 없으므로 복합 키 클래스를 만들지 않아도 된다.

### 3-5. 일대일 식별 관계

---

- 식별 관계 일대일
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2010.png)
    
- 일대일 식별 관계는 자식의 기본 키 값으로 부모의 기본 키 값만 사용한다.
- 그래서 부모의 기본 키가 복합 키가 아니면 자식의 기본 키는 복합 키로 구성하지 않아도 된다.
- 일대일 식별 관계 매핑하기
    
    ```java
    // 부모
    @Entity
    public class Board {
        @Id @GeneratedValue
        @Column(name = "BOARD_ID")
        private Long id;
        private String title;
        @OneToOne(mappedBy = "board")
        private BoardDetail boardDetail;
        ...
    }
    
    // 자식
    @Entity
    public class BoardDetail {
        @Id
        private Long boardId;
        @MapsId // BoardDetail.boardId 매핑
        @OneToOne
        @JoinColumn(name="BOARD_ID")
        private Board board;
        private String content;
        ...
    }
    ```
    
- BoardDetail 처럼 식별자가 단순히 컬럼 하나면 `@MapsId`를 사용하고 속성 값은 비워두면 된다.

### 3-6. 식별, 비식별 관계의 장단점

---

- 식별 관계가 가지는 장점
    - 기본 키 인덱스를 활용하기 좋다.
    - 상위 테이블들의 기본 키 컬럼을 자식, 손자 테이블들이 가지고 있으므로 특정 상황에 조인 없이 하위 테이블만으로 검색을 완료할 수 있다.
        - 두 경우 모두 CHILD 테이블의 기본 키 인덱스를 PARENT_ID + CHILD_ID 로 구성하면 별도의 인덱스를 생성할 필요 없이 기본 키 인덱스만 사용해도 된다.
        
        ```sql
        // 부모 아이디가 A인 모든 자식 조회
        SELECT * FROM CHILD
        WHERE PARENT_ID = 'A'
        
        // 부모 아이디가 A고 자식 아이디가 B인 자식 조회
        SELECT * FROM CHILD
        WHERE PARENT_ID = 'A' AND CHILD_ID = 'B'
        ```
        

> 비식별 관계를 선호한다
> 
> - 데이터베이스 설계 관점 : 비식별 관계 선호 이유
>     - 식별 관계는 부모 테이블의 기본 키를 자식 테이블로 전파하면서 자식 테이블의 기본 키 컬럼이 점점 늘어난다.
>         - 예를 들어, 부모 테이블은 기본 키 컬럼이 하나였지만 자식 테이블은 기본 키 컬럼이 2개, 손자 테이블은 기본 키 컬럼이 3개로 점점 늘어난다.
>         - 결국 조인할 때 SQL이 복잡해지고 기본 키 인덱스가 불필요하게 커질 수 있다.
>     - 식별 관계는 2개 이상의 컬럼을 합해서 복합 기본 키를 만들어야 하는 경우가 많다.
>     - 식별 관계를 사용할 때 기본 키로 비즈니스 의미가 있는 자연 키 컬럼을 조합하는 경우가 많다.
>         - 반면 비식별 관계의 기본 키는 비즈니스와 전혀 관계없는 대리 키를 주로 사용한다.
>         - 비즈니스 요구사항은 시간이 지남에 따라 언젠가는 변한다. 따라서 식별 관계의 자연 키 컬럼들이 자식에 손자까지 전파되면 변경하기 힘들다
>     - 식별 관계는 부모 테이블의 기본 키를 자식 테이블의 기본 키로 사용하므로 비식별 관계보다 테이블 구조가 유연하지 못하다.
> - 객체 관계 매핑 관점 : 비식별 관계 선호 이유
>     - 일대일 관계를 제외하고 식별 관계는 2개 이상의 컬럼을 묶은 복합 기본 키를 사용한다.
>         - JPA 에서 복합 키는 별도의 복합 키 클래스를 만들어서 사용해야 한다.
>         - 따라서 컬럼이 하나인 기본 키를 매핑하는 것보다 많은 노력이 필요하다.
>     - 비식별 관계의 기본 키는 주로 대리 키를 사용하는데, JPA 는 `@GeneratedValue` 처럼 대리 키를 생성하기 위한 편리한 방법을 제공한다.

👉

- 비식별 관계를 사용하고 기본 키는 `Long` 타입의 대리 키를 사용하자
    - 이 경우, 대리 키는 비즈니스와 아무 관련이 없다. 따라서 비즈니스가 변경되어도 유연한 대처가 가능하다는 장점이 있다.
    - JPA는 `@GeneratedValue`를 통해 간편하게 대리 키를 생성할 수 있다. 그리고 식별자 컬럼이 하나여서 쉽게 매핑이 가능하다.
- 선택적 비식별 관계보다 필수적 비식별 관계가 더 좋은 이유
    - 선택적 비식별 관계는 `NULL`을 허용하므로 조인할 때에 외부 조인을 사용해야 한다.
    - 반면 필수적 비식별 관계는 `NOT NULL`로 항상 관계가 있다는 것을 보장하므로 내부 조인만 사용해도 된다.


## 4. 조인 테이블

- 데이터베이스 테이블의 연관관계 설계 방법은 크게 2가지다.
    - 조인 컬럼 사용 (외래 키)
        - 테이블 간에 조인 컬럼이라 부르는 외래 키 컬럼을 사용해서 관리 - `@JoinColumn` 사용해서 매핑
        
        ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2011.png)
        
    - 조인 테이블 사용 (테이블 사용)
        - 조인 테이블이라는 별도의 테이블을 사용해서 연관관계를 관리 - `@JoinTable` 사용해서 매핑
        - 주로 다대다 관계를 일대다, 다대일 관계로 풀어내기 위해 사용한다. (일대일, 일대다, 다대일 관계에서도 사용한다)
        
        ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2012.png)
        
        - 조인 테이블의 가장 큰 단점은 테이블을 하나 추가해야 한다는 점이다.
            - 관리해야 하는 테이블이 늘어나고 회원과 사물함 두 테이블을 조인하려면 MEMBER_LOCKER 테이블까지 추가로 조인해야 한다.

기본은 조인 컬럼을 사용하고 필요하다고 판단되면 조인 테이블을 사용한다.

### 4-1. 일대일 조인 테이블

---

- 일대일 관계를 만들려면 조인 테이블의 외래 키 컬럼 각각에 총 2개의 유니크 제약조건을 걸어야 한다.(기본 키는 유니크 제약조건이 걸려있다)
- 조인 테이블 일대일
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2013.png)
    
- 일대일 조인 테이블 매핑
    
    ```java
    // 부모
    @Entity
    public class Parent {
        @Id @GeneratedValue
        @Column(name = "PARENT_ID")
        private Long id;
        private String name;
        @OneToOne
        @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
                joinColumns = @JoinColumn(name = "PARENT_ID"), // 현재 엔티티를 참조하는 외래 키
                inverseJoinColumns = @JoinColumn(name = "CHILD_ID") // 반대방향 엔티티를 참조하는 외래 키
        )
        private Child child;
        ...
    }
    
    // 자식
    @Entity
    public class Child {
        @Id @GeneratedValue
        @Column(name = "CHILD_ID")
        private Long id;
        private String name;
        ...
        // 양방향 매핑 시
        // @OneToOne(mappedBy="child")
        // private Parent parent;
    }
    ```
    
- `@JoinTable`의 속성
    - `name`: 매핑할 조인 테이블 이름
    - `joinColumns`: 현재 엔티티를 참조하는 외래 키
    - `inverseJoinColumns`: 반대방향 엔티티를 참조하는 외래 키

### 4-2. 일대다 조인 테이블

---

- 일대다 관계를 만들려면 조인 테이블의 컬럼 중 다(N)와 관련된 컬럼인 CHILD_ID 에 유니크 제약조건을 걸어야 한다.(기본 키는 유니크 제약조건이 걸려있다)
- 조인 테이블 일대다, 다대일
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2014.png)
    
- 일대다 단방향 조인 테이블 매핑
    
    ```java
    import java.util.ArrayList;
    // 부모
    @Entity
    public class Parent {
      @Id
      @GeneratedValue
      @Column(name = "PARENT_ID")
      private Long id;
      private String name;
      @OneToMany
      @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
        joinColumns = @JoinColumn(name = "PARENT_ID"), // 현재 엔티티를 참조하는 외래 키
        inverseJoinColumns = @JoinColumn(name = "CHILD_ID") // 반대방향 엔티티를 참조하는 외래 키
      )
      private List<Child> child = new ArrayList<Child>();
        ...
    }
    
    // 자식
    @Entity
    public class Child {
      @Id
      @GeneratedValue
      @Column(name = "CHILD_ID")
      private Long id;
      private String name;
        ...
    }
    ```
    

### 4-3. 다대일 조인 테이블

---

- 다대일은 일대다에서 방향만 반대이므로 조인 테이블 모양은 일대다에서 설명한 것과 같다.
- 다대일 양방향 조인 테이블 매핑
    
    ```java
    import java.util.ArrayList;
    // 부모
    @Entity
    public class Parent {
      @Id
      @GeneratedValue
      @Column(name = "PARENT_ID")
      private Long id;
      private String name;
      @OneToMany(mappedBy = "parent")
      private List<Child> child = new ArrayList<Child>();
      ...
    }
    
    // 자식
    @Entity
    public class Child {
      @Id
      @GeneratedValue
      @Column(name = "CHILD_ID")
      private Long id;
      private String name;
      @ManyToOne(optional = false)
      @JoinTable(name = "PARENT_CHILD", // 매핑할 조인 테이블 이름
            joinColumns = @JoinColumn(name = "CHILD_ID"), // 현재 엔티티를 참조하는 외래 키
            inverseJoinColumns = @JoinColumn(name = "PARENT_ID") // 반대방향 엔티티를 참조하는 외래 키
      )
      private Parent parent;
        ...
    }
    ```
    

### 4-4. 다대다 조인 테이블

---

- 다대다 관계를 만들려면 조인 테이블의 두 컬럼을 합해서 하나의 복합 유니크 제약조건을 걸어야 한다. (PARENT_ID, CHILD_ID는 복합 기본 키이므로 유니크 제약조건이 걸려 있다.)
- 조인 테이블 다대다
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2015.png)
    
- 다대다 조인 테이블 매핑
    
    ```java
    import java.util.ArrayList;
    // 부모
    @Entity
    public class Parent {
      @Id
      @GeneratedValue
      @Column(name = "PARENT_ID")
      private Long id;
      private String name;
      @ManyToMany(mappedBy = "parent")
      @JoinTable(name = "PARENT_CHILD",
            joinColumns = @JoinColumn(name = "PARENT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CHILD_ID")
      )
      private List<Child> child = new ArrayList<Child>();
      ...
    }
    
    // 자식
    @Entity
    public class Child {
        @Id
        @GeneratedValue
        @Column(name = "CHILD_ID")
        private Long id;
        private String name;
        ...
    }
    ```
    

조인 테이블에 외래키와는 별개로 새로운 컬럼을 추가한다면 `@JoinTable` 전략을 사용할 수 없다. 대신 새로운 엔티티를 만들어서 조인 테이블과 매핑해야한다.


## 5. 엔티티 하나에 여러 테이블 매핑

- 잘 사용하지는 않지만 `@SecondaryTable`을 사용하면 한 엔티티에 여러 테이블을 매핑할 수 있다.
- 하나의 엔티티에 여러 테이블 매핑하기
    
    ![Untitled](Chapter%207%20%E1%84%80%E1%85%A9%E1%84%80%E1%85%B3%E1%86%B8%20%E1%84%86%E1%85%A2%E1%84%91%E1%85%B5%E1%86%BC%20e6818ddff0834dfe8a5c1578e147bf22/Untitled%2016.png)
    
- 하나의 엔티티에 여러 테이블 매핑
    
    ```java
    @Entity
    @Table(name="BOARD")
    @SecondaryTable(name="BOARD_DETAIL",
        pkJoinColumns = @PrimaryKeyJoinColumn(name="BOARD_DETAIL_ID"))
    public class Board {
        @Id @GeneratedValue
        @Column(name="BOARD_ID")
        private Long id;
        private String title; // table을 지정하지 않으면 기본 테이블인 Board(현재 테이블)에 매핑
        @Column(table = "BOARD_DETAIL") // table을 지정하면 해당 테이블에 매핑
        private String content;
        ...
    }
    ```
    
- `@SecondaryTable` 속성
    - `name`: 매핑할 다른 테이블의 이름
        - 예제에서는 테이블명을 BOARD_DETAIL 로 지정했다.
    - `pkJoinColumns`: 매핑할 다른 테이블의 기본 키 컬럼 속성
        - 예제에서는 기본 키 컬럼명을 BOARD_DETAIL_ID 로 지정했다.

- 더 많은 테이블을 매핑하려면 `@SecondaryTables`를 사용하면 된다.
    - JPA 2.2 부터 `@Repeatable` 추가, `@SecondaryTable` 중복으로만도 가능

두 테이블을 하나의 엔티티에 매핑하는 방법보다는 테이블당 엔티티를 각각 만들어서 일대일 매핑하는 것을 권장한다.

- 참고
    - [https://www.nowwatersblog.com/jpa/ch7/7-2](https://www.nowwatersblog.com/jpa/ch7/7-2)
    - [https://velog.io/@shininghyunho/JPA프로그래밍-7.고급-매핑](https://velog.io/@shininghyunho/JPA%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-7.%EA%B3%A0%EA%B8%89-%EB%A7%A4%ED%95%91)
