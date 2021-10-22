# Chapter 9. 값 타입

### JPA의 데이터 타입 분류

---

- 엔티티 타입: `@Entity`로 정의하는 객체
- 값 타입: 단순히 값으로 사용하는 자바 기본 타입이나 객체
    - 기본값 타입
    - 임베디드 타입(복합 값 타입)
    - 컬렉션 값 타입


## 1. 기본값 타입

- 기본값 타입
    - 자바 기본 타입 (int, double)
    - 래퍼 클래스 (Integer, Long)
    - String
- 기본값 타입
    
    ```java
    @Entity
    public class Member {
        @Id @GeneratedValue
        private Long id; // 식별자 값
        **private String name;** // 값 타입 속성
        **private int age;** // 값 타입 속성
        ...
    }
    ```
    
- 값 타입은 공유하면 안된다.

자바에서 int, double 같은 기본 타입(primitive type)은 절대 공유되지 않는다. 항상 값을 복사한다.

→ Side Effect 가 발생하지 않는다.

```java
int a = 20;
int b = a; // 20이라는 값을 복사
b = 10;
```


## 2. 임베디드 타입(복합 값 타입)

- JPA 에서 새로운 값 타입을 직접 정의해서 사용하는 타입
- 임베디드 타입도 int, String 처럼 값 타입이다.

- 기본 회원 엔티티
    
    ```java
    @Entity
    public class Member {
        @Id @GeneratedValue
        private Long id;
        private String name;
    
        // 근무 기간
        @Temporal(TemporalType.DATE) java.util.Date startDate;
        @Temporal(TemporalType.DATE) java.util.Date endDate;
    
        // 집 주소 표현
        private String city;
        private String street;
        private String zipcode;
    }
    ```
    
- 상세한 데이터를 그대로 가지고 있는 것은
    - 객체지향적이지 않으며 응집력만 떨어뜨린다.
    - 값 타입들은 재사용할 수 있고 응집도도 아주 높다.
    - 해당 값 타입만 사용하는 의미있는 메소드도 만들 수 있다.

- 값 타입 적용 회원 엔티티
    
    ```java
    @Entity
    public class Member {
      @Id @GeneratedValue
      private Long id;
      private String name;
    
      // 근무 기간
      **@Embedded Period workPeriod; // 근무 기간**
      **@Embedded Address homeAddress; // 집 주소**
    }
    ```
    
- 기간 임베디드 타입
    
    ```java
    **@Embeddable**
    public class Period {
    
        @Temporal(TemporalType.DATE) java.util.Date startDate;
        @Temporal(TemporalType.DATE) java.util.Date endDate;
        // ..
    
        public boolean isWork(Date date){
            //.. 값 타입을 위한 메소드를 정의할 수 있다.
        }
    }
    ```
    
- 주소 임베디드 타입
    
    ```java
    **@Embeddable**
    public class Address {
    
      @Column(name = "city") // 매핑할 컬럼 정의 가능
      private String city;
      private String street;
      private String zipcode;
      // ..
    }
    ```
    

- 임베디드 타입을 사용하려면 필요한 것
    - `@Embeddable`: 값 타입을 정의하는 곳에 표시
    - `@Embedded`: 값 타입을 사용하는 곳에 표시
    - 기본 생성자
- 임베디드 타입을 포함한 모든 값 타입은 엔티티의 생명주기에 의존하므로, 엔티티와 임베디드 타입의 관계를 UML 로 표현하면 **컴포지션 관계**가 된다

하이버네이트는 임베디드 타입을 컴포넌트라고 한다.

### 2-1. 임베디드 타입과 테이블 매핑

---

- 임베디드 타입은 엔티티의 값일 뿐, 값이 속한 엔티티의 테이블에 매핑한다.
- 임베디드 타입을 사용하기 전과 후에 매핑하는 테이블은 같다.
- ORM을 사용하지 않고 개발하면 테이블 컬럼과 객체 필드를 대부분 1:1로 매핑한다.
- 이런 지루한 반복 작업은 JPA에 맡기고 더 세밀한 객체지향 모델을 설계하는데 집중할 수 있다.

### 2-2. 임베디드 타입과 연관관계

---

- 임베디드 타입은 값 타입을 포함하거나 엔티티를 참조할 수 있다.

엔티티는 공유될 수 있으므로 참조한다고 표현하고, 값 타입은 특정 주인에 소속되고 논리적 개념상 공유되지 않으므로 포함한다고 표현

- 임베디드 타입과 연관관계
    
    ```java
    @Entity
    public class Member {
        **@Embedded Address address; // 임베디드 타입 포함**
        **@Embedded PhoneNumber phoneNumber; // 임베디드 타입 포함**
        // ...
    }
    
    @Embeddable
    public class Address {
        String street;
        String city;
        String state;
        **@Embedded Zipcode zipcode; // 임베디드 타입 포함**
    }
    
    @Embeddable
    public class Zipcode {
        String zip;
        String plusFour;
    }
    
    @Embeddable
    public class PhoneNumber {
        String areaCode;
        String localNumber;
        **@ManyToOne PhoneServiceProvider provider; // 엔티티 참조**
        ...
    }
    
    @Entity
    public class PhoneServiceProvider {
        @Id String name;
        ...
    }
    ```
    

### 2-3. `@AttributeOverride`: 속성 재정의

---

- 임베디드 타입에 정의한 매핑정보를 재정의할 때 사용하는 어노테이션
    - JPA 2.2 [Hibernate 5.3](https://hibernate.org/orm/releases/) 이상(`spring-boot-starter-data-jpa:2.5.0` 경우, `hibernate-core:5.4.31` 사용중)부터는 `@AttributeOverride` 중복 정의로 사용 가능
    
    ![Untitled](Chapter%209%20%E1%84%80%E1%85%A1%E1%86%B9%20%E1%84%90%E1%85%A1%E1%84%8B%E1%85%B5%E1%86%B8%208471b50649ca41b388f1b935b4861c54/Untitled.png)
    
- 컬럼명이 중복되면, `MappingException: Repeated column Error`

- 임베디드 타입 재정의
    
    ```java
    @Entity
    public class Member {
        @Id @GeneratedValue
        private Long id;
        private String name;
     
        @Embedded Address homeAddress;
    
        @Embedded
        @AttributeOverrides({
            @AttributeOverride(name="city", column=@Column(name="COMPANY_CITY")),
            @AttributeOverride(name="street", column=@Column(name="COMPANY_STREET")),
            @AttributeOverride(name="zipcode", column=@Column(name="COMPANY_ZIPCODE"))
        })
        Address CompanyAddress;
    }
    ```
    
- `@AttributeOverride`를 사용하면 어노테이션을 너무 많이 사용해서 엔티티 코드가 지저분해진다.
    - 다행히도 한 엔티티에 같은 임베디드 타입을 중복해서 사용하는 일은 많지 않다.

`@AttributeOverride`는 엔티티에 설정해야 한다. 임베디드 타입이 임베디드 타입을 가지고 있어도 엔티티에 설정해야 한다.

### 2-4. 임베디드 타입과 `null`

---

- 임베디드 타입이 `null`이면 매핑한 컬럼 값은 모두 `null`이 된다.
    
    ```java
    member.setAddress(null); // null 입력
    em.persist(member);
    ```
    
    - 회원 테이블의 주소와 관련된 `CITY`, `STREET`, `ZIPCODE` 컬럼 값은 모두 `null`이 된다.


## 3. 값 타입과 불변 객체

- 값 타입은 객체 세상을 조금이라도 단순화하려고 만든 개념이다. 따라서 값 타입은 단순하고 안전하게 다룰 수 있어야 한다.

### 3-1. 값 타입 공유 참조

---

- 값 타입을 여러 엔티티에서 공유하면 위험하다.
- 값 타입 공유 시 문제 상황
    
    ```java
    member1.setHomeAddress(new Address("OldCity"));
    Address address = member1.getHomeAddress();
    
    address.setCity("NewCity"); // 회원 1의 address 값을 공유해서 사용
    member2.setHomeAddress(address);
    ```
    
    - 회원1과 회원2가 같은 `address` 인스턴스를 참조해서 영속성 컨텍스트는 회원1, 2 둘 다 city 속성이 변경된 것으로 판단해서 회원1, 회원2 각각 UPDATE SQL 을 실행한다.
- 부작용(Side Effect): 뭔가를 수정했는데 전혀 예상치 못한 곳에서 문제가 발생하는 것
    - 공유 참조로 발생하는 버그는 정말 찾아내기 어렵다.

👉 부작용을 막으려면 값을 복사해서 사용하면 된다.

### 3-2. 값 타입 복사

---

- 값 타입의 실제 인스턴스인 값을 공유하는 것은 위험하다. 대신에 값(인스턴스)을 복사해서 사용해야 한다.
    
    ```java
    member1.setHomeAddress(new Address("OldCity"));
    Address address = member1.getHomeAddress();
    
    // 회원 1의 address 값을 복사해서 새로운 newAddress 값을 생성
    Address newAddress = address.clone(); // clone 은 자신을 복사해서 반환하도록 구현되어 있다.
    
    newAddress.setCity("NewCity");
    member2.setHomeAddress(newAddress);
    ```
    

- 객체를 대입할 때마다 인스턴스를 복사해서 대입하면 공유 참조를 피할 수 있다. 하지만 근본적인 객체 공유 참조를 피할 수는 없다.
    
    자바는 기본 타입이면 값을 복사해서 넘기고, 객체면 참조를 넘긴다.
    
- 따라서 부작용을 막기 위한 가장 단순한 방법은 객체의 값을 수정하지 못하게 막는 것이다. (`Setter` 제거)

### 3-3. 불변 객체

---

- 객체를 불변하게 만들면 값을 수정할 수 없으므로 부작용을 원천 차단할 수 있다.
- 따라서 값 타입은 될 수 있으면 불변 객체로 설계해야 한다.

- 불변 객체
    - 한 번 만들면 절대 변경할 수 없는 객체. 불변 객체의 값은 조회할 수 있지만 수정할 수 없다.
    - 불변 객체도 결국은 객체여서 인스턴스의 참조 값 공유를 피할 수 없다.
    - 하지만 참조 값을 공유해도 인스턴스의 값을 수정할 수 없으므로 부작용이 발생하지 않는다.
    - 가장 간단한 방법은 생성자로만 값을 설정하고, 수정자를 만들지 않는 것이다.
- 주소 불변 객체
    
    ```java
    @Embeddable
    public class Address {
    
        private String city;
    
        protected Address() {} // JPA 에서 기본 생성자는 필수이다.
    
        // 생성자로 초기 값을 설정한다.
        public Address (String city) { this.city = city;}
    
        // 접근자 (Getter) 는 노출한다.
        public String getCity() {
            return city;
        }
    
        // 수정자 (Setter) 는 만들지 않는다.
    }
    ```
    
    - `Address`는 값을 수정할 수 없으므로 공유해도 부작용이 발생하지 않는다.
- 생성자로만 값을 설정하고, 만약 값을 수정해야 하면 새로운 객체를 생성해서 사용해야 한다.
    
    ```java
    // 불변 객체 사용
    Address address = member1.getHomeAddress();
    
    // 회원 1의 주소값을 조회해서 새로운 주소값을 생성
    Address newAddress = new Address(address.getCity());
    member2.setHomeAddress(newAddress);
    ```
    

## 4. 값 타입의 비교

- 자바가 제공하는 객체 비교 2가지
    - 동일성(Identity) 비교: 인스턴스의 참조 값을 비교, `==` 사용
    - 동등성(Equivalence) 비교: 인스턴스의 값을 비교, `equals()`사용

- 보통 값 타입의 동등성을 비교하기 위해서는 `equals()`메소드를 재정의할 때 모든 필드의 값을 비교하도록 구현한다.

`equals()` 재정의 시, `hashCode()`도 재정의하는 것이 안전하다. 그렇지 않으면 해시를 사용하는 컬렉션(`HashSet`, `HashMap`)이 정상 동작하지 않는다.


## 5. 값 타입 컬렉션

- 값 타입을 하나 이상 저장하려면 컬렉션에 보관하고 `@ElementCollection`, `@CollectionTable` 어노테이션을 사용하면 된다.
- 값 타입 컬렉션
    
    ```java
    import java.util.ArrayList;
    
    @Entity
    public class Member {
        @Id
        @GeneratedValue
        private Long id;
    
        @Embedded
        private Address homeAddress;
    
        **@ElementCollection**
        **@CollectionTable**(name = "FAVORITE_FOODS",
          joinColumns = @JoinColumn(name = "MEMBER_ID"))
        @Column(name = "FOOD_NAME") // FAVORITE_FOODS 테이블에 값으로 사용되는 컬럼이 FOOD_NAME 하나 뿐이라서 @Column을 사용해 컬럼명을 지정할 수 있다.
        private Set<String> favoriteFoods = new HashSet<String>();
    
        **@ElementCollection**
        **@CollectionTable**(name = "ADDRESS",
          joinColumns = @JoinColumn(name = "MEMBER_ID"))
        private Set<Address> addressHistory = new ArrayList<Address>();
        //...
    }
    
    @Embeddable
    public class Address {
        @Column
        private String city;
        private String street;
        private String zipcode;
        //...
    }
    ```
    
- 값 타입 컬렉션 UML, ERD
    
    ![Untitled](Chapter%209%20%E1%84%80%E1%85%A1%E1%86%B9%20%E1%84%90%E1%85%A1%E1%84%8B%E1%85%B5%E1%86%B8%208471b50649ca41b388f1b935b4861c54/Untitled%201.png)
    
- DB 테이블로 매핑 시 컬럼 안에 컬렉션을 포함할 수 없다. 따라서 별도의 테이블을 추가하고 `@CollectionTable`를 사용해서 추가한 테이블을 매핑해야 한다.
- 테이블 매핑정보는 `@AttributeOverride`를 사용해서 재정의할 수 있다.

`@CollectionTable`를 생략하면 기본값을 사용해서 매핑한다. 기본값: {엔티티 이름}_{컬렉션 속성 이름} ex> Member 엔티티의 addressHistory는 Member_addressHistory 테이블과 매핑한다.

### 5-1. 값 타입 컬렉션 사용

---

- 값 타입 컬렉션 등록
    
    ```java
    Member member = new Member();
    
    // 임베디드 값 타입
    member.setHomeAddress(new Address("통영", "뭉돌해수욕장", "660-123"));
    
    // 기본값 타입 컬렉션
    member.getFavoriteFoods().add("짬뽕");
    member.getFavoriteFoods().add("짜장");
    member.getFavoriteFoods().add("탕수육");
    
    // 임베디드 값 타입 컬렉션
    member.getAddressHistory().add(new Address("서울", "강남", "123-123"));
    member.getAddressHistory().add(new Address("서울", "강북", "000-000"));
    
    **em.persist(member);**
    ```
    
    - 마지막에 `member` 엔티티만 영속화했다 → JPA 는 이때 `member` 엔티티의 값 타입도 함께 저장한다.
    - 실제 데이터베이스에 실행되는 INSERT SQL
        - member: INSERT SQL 1번
        - member.homeAddress: 컬렉션이 아닌 임베디드 값 타입이므로 회원테이블을 저장하는 SQL에 포함.
        - member.favoriteFoods: INSERT SQL 3번
        - member.addressHistory: INSERT SQL 2번
        
        → 따라서 `em.persist(member)` 한 번 호출로 총 6번의 INSERT SQL을 실행한다. (영속성 컨텍스트 플러시할 때 SQL을 전달)
        

값 타입 컬렉션은 `영속성 전이(Cascade)` + `고아 객체 제거(ORPHAN REMOVE)` 기능을 필수로 갖는다.

- 값 타입 컬렉션도 조회 시 패치 전략을 선택할 수 있는데, `LAZY`가 기본이다.
    - `@ElementCollection(fetch = FetchType.LAZY)`
- 조회
    
    ```java
    // 1. member: 회원만 조회. 이때 임베디드 값 타입인 homeAddress도 함께 조회 -> SELECT SQL 1번 호출
    Member member = em.find(Member.class, 1L);
    
    // 2. member.homeAddress
    Address homeAddress = member.getHomeAddress();
    
    // 3. member.favoriteFoods: LAZY로 설정해서 실제 컬렉션 사용할 때 SELECT SQL 1번 호출
    Set<String> favoriteFoods = member.getFavoriteFoods(); // LAZY
    for(String favoriteFood : favoriteFoods){
        System.out.println("favoriteFood = " + favoriteFood);
    }
    
    // 4. member.addressHistory: LAZY로 설정해서 실제 컬렉션 사용할 때 SELECT SQL 1번 호출
    List<Address> addressHistory = member.getAddressHistory(); // LAZY
    
    addressHistory.get(0);
    ```
    
- 수정
    
    ```java
    Member member = em.find(Member.class, 1L);
    
    // 1. 임베디드 값 타입 수정
    member.setHomeAddress(new Address("새로운 도시", "신도시1", "123456"));
    
    // 2. 기본값 타입 컬렉션 수정
    Set<String> favoriteFoods = member.getFavoriteFoods();
    favoriteFoods.remove("탕수육");
    favoriteFoods.add("치킨");
    
    // 3. 임베디드 값 타입 컬렉션 수정
    List<Address> addressHistory = member.getAddressHistory();
    addressHistory.remove(new Address("서울", "기존 주소", "123-123"));
    addressHistory.add(new Address("새로운 도시", "새로운 주소", "123-456"));
    ```
    
    1. 임베디드 값 타입 수정
        - homeAddress 임베디드 값 타입은 MEMBER 테이블과 매핑했으므로 MEMBER 테이블만 UPDATE 한다
            - 사실 Member 엔티티를 수정하는 것과 같다.
    2. 기본값 타입 컬렉션 수정
        - 탕수육을 치킨으로 변경하려면 탕수육을 제거하고 치킨을 추가해야 한다.
        - 자바의 `String` 타입은 수정할 수 없다.
    3. 임베디드 값 타입 컬렉션 수정
        - 값 타입은 불변해야 해서 컬렉션에서 기존 주소를 삭제하고 새로운 주소를 등록한다.
        - 이때 값 타입은 `equals()`, `hashCode()`를 꼭 구현해야 한다.

### 5-2. 값 타입 컬렉션의 제약사항

---

- 엔티티는 식별자가 있어서 엔티티의 값을 변경해도 식별자로 데이터베이스에 저장된 원본 데이터를 쉽게 찾아서 변경할 수 있다.
- 반면 값 타입은 식별자라는 개념이 없고 단순한 값들의 모음이므로, 값을 변경해버리면 데이터베이스에 저장된 원본 데이터를 찾기가 어렵다.
- 특정 엔티티 하나에 소속된 값 타입은 값이 변경되어도 자신이 소속된 엔티티를 데이터베이스에서 찾고 값을 변경하면 된다.
- 문제는 값 타입 컬렉션이다. `값 타입 컬렉션`에 보관된 값 타입들은 별도의 테이블에 보관되기 때문에 값 타입의 값이 변경되면 데이터베이스에 있는 원본 데이터를 찾기 어렵다는 문제가 있다.

👉

- JPA 구현체들은 값 타입 컬렉션에 변경 사항이 발생하면, 값 타입 컬렉션이 매핑된 테이블의 연관된 모든 데이터를 삭제하고, 현재 값 타입 컬렉션 객체에 있는 모든 값을 데이터베이스에 다시 저장한다.
- 추가로 값 타입 컬렉션을 매핑하는 `테이블은 모든 컬럼을 묶어서 기본 키를 구성`해야 한다. 따라서 기본 키 제약 조건으로 인해 컬럼에 `null`을 입력할 수 없고, 같은 값을 중복으로 저장할 수 없는 제약도 있다.

👉

- 실무에서는 값 타입 컬렉션이 매핑된 테이블에 데이터가 많다면 값 타입 컬렉션 대신에 `일대다 관계`를 고려해야 한다. 추가적으로 `영속성 전이(Cascade)` + `고아 객체 제거(ORPHAN REMOVE)` 기능을 적용하면 값 타입 컬렉션처럼 사용할 수 있다.
- 값 타입 컬렉션 대신에 일대다 관계 사용
    
    ```java
    import java.util.ArrayList;
    
    @Entity
    public class AddressEntity {
      @Id
      @GeneratedValue
      private Long id;
    
      @Embedded
      Address address;
      ...
    }
    
    public class Member {
      ...
      @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
      @JoinColumn(name = "MEMBER_ID")
      private List<AddressEntity> addressHistory = new ArrayList<AddressEntity>();
    }
    ```
    

값 타입 컬렉션을 사용할 때는 모두 삭제하고 다시 저장하는 최악의 시나리오를 고려하면서 사용해야 한다. (사용하는 컬렉션이나 여러 조건에 따라 기본 키를 식별하지 못할 수도 있다)

- 참고
    - [https://www.nowwatersblog.com/jpa/ch9/9-2](https://www.nowwatersblog.com/jpa/ch9/9-2)
    - [https://lovecode.tistory.com/52](https://lovecode.tistory.com/52)
