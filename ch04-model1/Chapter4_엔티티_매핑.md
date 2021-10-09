# Chapter 4. 엔티티 매핑

## 1. `@Entity`

- JPA를 사용해서 테이블과 매핑할 클래스는 `@Entity` 어노테이션을 필수로 붙여야 한다.
- `@Entity`가 붙은 클래스는 JPA 가 관리한다.

- `@Entity` 적용시 주의사항
    
    ```java
    public Member() {}  // 기본 생성자
    
    public Member(String name) {
        this.name = name;
    }
    ```
    
    - 기본 생성자는 필수. (파라미터가 없는 public 또는 protected 생성자)
    - `final` 클래스, `enum`, `interface`, inner 클래스에는 사용할 수 없다.
    - 저장할 필드에 `final`을 사용하면 안된다.


## 2. `@Table`

- 엔티티와 매핑할 테이블을 지정한다.
    
    ```java
    @Entity
    @Table(name="MEMBER")
    public class Member {
        ...
    }
    ```
    

## 3. 다양한 매핑 사용

- 회원 엔티티
    
    ```java
    import javax.persistence.*;  
    import java.util.Date;
    
    @Entity
    @Table(name="MEMBER")
    public class Member {
    
        @Id
        @Column(name = "ID")
        private String id;
    
        @Column(name = "NAME", nullable = false, length = 10) //추가
        private String username;
    
        private Integer age;
    
        @Enumerated(EnumType.STRING)
        private RoleType roleType;
    
        @Temporal(TemporalType.TIMESTAMP)
        private Date createdDate;
    
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastModifiedDate;
    
        @Lob
        private String description;
    
        @Transient
        private String temp;
    
        //Getter, Setter
    
        ...
    }
    
    public enum RoleType {
        ADMIN, USER
    }
    ```
    
- 분석
    1. roleType : 자바의 enum을 사용해서 회원 타입을 구분. 자바의 enum을 사용하려면 `@Enumerated` 어노테이션으로 매핑.
    2. createDate, lastModifiedDate : 자바의 날짜 타입은 `@Temporal`을 사용해서 매핑
    3. description : 회원을 설명하는 필드는 길이 제한이 없다. 데이타베이스 VARCHAR 타입 대신에 CLOB 타입으로 저장. `@Lob`를 사용하면 CLOB, BLOB 타입을 매핑할 수 있다.
- 오라클 기준 LOB 설명
    - CLOB(CHARACTER LARGE OBJECT): 텍스트형태파일 크기를 4GB까지 지원
    - BLOB(BINARY LARGE OBJECT): 이진파일(이미지등) 형태의 파일 크기를 4BG까지 지원


## 4. 데이터베이스 스키마 자동 생성

- JPA 는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원
- `persistence.xml`
    - 어플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성한다.
        
        ```xml
        <property name="hibernate.hbm2ddl.auto" value="create" />
        ```
        
    - 콘솔에 실행되는 DDL을 출력한다.
        
        ```xml
        <property name="hibernate.show_sql" value="true" />
        ```
        
    - 이름 매핑 전략 변경: 테이블 명이나 컬럼 명이 생략되면 자바의 카멜케이스 표기법을 언더스코어 표기법으로 매핑한다.
        
        ```xml
        <property name="hibernate.ejb.naming_strategy" value="org.hibernate.cfg.ImprovedNamingStrategy" />
        ```
        
- 출력 예제
    
    ```java
    Hibernate: 
    INFO: HHH000227: Running hbm2ddl schema export
        drop table MEMBER if exists
    Hibernate: 
        create table MEMBER (
            ID varchar(255) not null,
            age integer,
            createdDate timestamp,
            description clob,
            lastModifiedDate timestamp,
            roleType varchar(255),
            NAME varchar(10) not null,
            primary key (ID)
        )
    Hibernate: 
        alter table MEMBER 
            add constraint NAME_AGE_UNIQUE  unique (NAME, age)
    1월 11, 2016 1:22:35 오후 org.hibernate.tool.hbm2ddl.SchemaExport execute
    INFO: HHH000230: Schema export complete
    findMember=지한, age=20
    Hibernate: 
        /* insert jpabook.start.Member
            */ insert 
            into
                MEMBER
                (age, createdDate, description, lastModifiedDate, roleType, NAME, ID) 
            values
                (?, ?, ?, ?, ?, ?, ?)
    Hibernate: 
        /* update
            jpabook.start.Member */ update
                MEMBER 
            set
                age=?,
                createdDate=?,
                description=?,
                lastModifiedDate=?,
                roleType=?,
                NAME=? 
            where
                ID=?
    Hibernate: 
        /* select
            m 
        from
            Member m */ select
                member0_.ID as ID1_0_,
                member0_.age as age2_0_,
                member0_.createdDate as createdD3_0_,
                member0_.description as descript4_0_,
                member0_.lastModifiedDate as lastModi5_0_,
                member0_.roleType as roleType6_0_,
                member0_.NAME as NAME7_0_ 
            from
                MEMBER member0_
    members.size=1
    Hibernate: 
        /* delete jpabook.start.Member */ delete 
            from
                MEMBER 
            where
                ID=?
    ```
    

👉

- 스키마 자동 생성 기능이 만든 DDL은 운영환경에서 사용할 만큼 완벽하지 않음.
- 개발 환경에서 사용하거나 매핑시 참고하는 용도로 사용.
- 절대 운영 환경에서 사용하지 말자.


## 5. DDL 생성 기능

- DDL 에 제약 조건을 추가할 수 있다.
    
    ```java
    @Entity
    @Table(name="MEMBER")
    public class Member {
    
        @Id
        @Column(name = "ID")
        private String id;
    
        @Column(name = "NAME", nullable = false, length = 10) //추가
        private String username;
        ...
    }
    ```
    
    - `nullable = false` : not null 제약 조건 추가
    - `length = 10` : 크기를 지정
    - 생성된 DDL
        
        ```java
        create table MEMBER (
            ...
            NAME varchar(10) not null,
            ...
        )
        ```
        

→ 단지 DDL을 자동으로 생성할 때만 사용되고, JPA 실행 로직에는 영향을 주지 않는다.


## 6. 기본 키 매핑

```java
@Entity
public class Member {

    @Id
    @Column(name = "ID")
    private String id;
    ...
}
```

- JPA 가 제공하는 데이터베이스 기본 키 생성 전략
    - 데이터베이스 벤더마다 기본 키 생성을 지원하는 방식이 다름
- 기본키 생성 전략 방식
    - 직접 할당 : 기본 키를 어플리케이션이 직접 할당
    - 자동 생성 : 대리 키 사용 방식
        - `INDENTITY`: 기본 키 생성을 데이터베이스에 위임.
        - `SEQUENCE`: 데이터베이스 시퀀스를 사용해서 기본 키를 할당.
        - `TABLE`: 키 생성 테이블을 사용한다.
- 기본키 생성 방법
    - 기본 키를 직접 할당 : `@Id`만 사용
    - 자동 생성 전략 사용 : `@GeneratedValue` 추가 및 키 생성 전략 선택.
- 키 생성 전략 사용을 위한 속성 추가 - `persistence.xml`
    
    ```xml
    <property name="hibernate.id.new_generator_mappings" value="true" />
    ```
    

### 6-1. 기본 키 직접 할당 전략

---

```java
@Id
@Column(name = "id")
private String id;
```

- `@Id` 적용 가능한 자바 타입
    - 자바 기본형
    - 자바 래퍼형
    - String
    - java.util.Date
    - java.sql.Date
    - java.math.BigDecimal
    - java.math.BigInteger
- 기본 키 할당하는 방법
    
    ```java
    Board board = new Board();
    board.setId("id1");         //기본 키 직접 할당
    em.persist(board);
    ```
    

### 6-2. IDENTITY 전략

---

- IDENTITY는 기본 키 생성을 데이타베이스에 위임하는 전략
- 주로 MySQL, PostgreSQL, SQL Server, DB2, H2 에서 사용

- MySQL의 `AUTO_INCREMENT` 기능
    
    ```java
    CREATE TABLE BOARD {
        ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        DATA VARCHAR(255)
    };
    
    INSERT INTO BOARD(DATA) VALUES('A');
    INSERT INTO BOARD(DATA) VALUES('B');
    ```
    
- IDENTITY 전략
    
    ```java
    @Entity
    public class Professor {
    
      @Id 
      @GeneratedValue(strategy=GenerationType.IDENTITY)
      private int id;
    
      private String name;
      ...
    }
    ```
    
    - 데이터베이스에 값을 저장하고 나서야 기본 키 값을 구할 수 있을 때 사용.
    - `em.persist()` 호출시 INSERT SQL을 즉시 데이터베이스에 전달.
    - 식별자를 조회해서 엔티티의 식별자에 할당.
    - 쓰기 지연이 동작하지 않는다.

### 6-3. SEQUENCE 전략

---

- 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트
- 주로 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용.

- 시퀀스 관련 SQL
    
    ```java
    CREATE TABLE BOARD (
        ID BIGINT NOT NULL PRIMARY KEY,
        DATA VARCHAR(255)
    )
    
    //시퀀스 생성
    CREATE SEQUENCE BOARD_SEQ START WITH 1 INCREMENT BY 1;
    ```
    
- 시퀀스 매핑 코드
    
    ```java
    @Entity
    @SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1,
        allocationSize = 1)
    public class Board {
    
        @Id
        @GeneraedValue(strategy = GenerationType.SEQUNCE,
                        generator = "BOARD_SEQ_GENERATOR")
        private Long id;
    }
    ```
    
- 시퀀스 사용 코드
    
    ```java
    private static void logic(EntityManager em) {
        Board board = new Board();
        em.persist(board);
        System.out.println("board.id = " + board.getId());
    }
    ```
    

1. 먼저 데이터베이스 시퀀스를 사용해서 식별자를 조회.
2. 조회한 식별자를 엔티티에 할당.
3. 엔티티를 영속성 컨텍스트에 저장.
4. 트랜잭션 커밋.
5. 플러시 - 데이터베이스에 저장.

`SequenceGenerator.allocationSize` 기본값이 50 → 반드시 1로 설정해야 한다.

### 6-4. TABLE 전략

---

- 키 생성 전용 테이블을 하나 만들고 여기에 이름과 값을 사용할 컬럼을 만들어 데이터베이스 시퀀스를 흉내내는 전략.
- 테이블을 사용하므로 모든 데이터베이스에 적용할 수 있다.

- TABLE 전략 키 생성 테이블
    
    ```java
    create table MY_SEQUENCES (
        sequence_name varchar(255) not null,
        next_val bigint,
        primary key (sequence_name)
    )
    ```
    
- TABLE 전략 매핑 코드
    
    ```java
    @Entity
    @TableGenerator(
        name = "BOARD_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "BOARD_SEQ", allocationSize = 1)
    public class Board {
    
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE,
                    generator = "BOARD_SEQ_GENERATOR")
        private Long id;
    }
    ```
    
- TABLE 전략 매핑 사용 코드
    
    ```java
    private static void logic(EntityManger em) {
        Board board = new Board();
        em.persist(board);
        System.out.println("board.id = " + board.getId()); // board.id = 1
    }
    ```
    

- MY_SEQUENCES 테이블에 값이 없으면 JPA가 값을 INSERT
- 미리 넣어둘 필요가 없다.

시퀀스 대신에 테이블을 사용한다는 것만 제외하면 SEQUENCE 전략과 동일

### 6-5. AUTO 전략

---

- `GenerationType.AUTO`는 선택한 데이터베이스 방언에 따라 INDENTITY, SEQUENCE, TABLE 전략 중 하나를 자동으로 선택.
- AUTO 전략 매핑 코드
    
    ```java
    @Entity
    public class Board {
    
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        ...
    }
    ```
    
- `@GeneratedValue.strategy`의 기본값은 AUTO
- 장점
    - 데이터베이스를 변경해도 코드를 수정할 필요가 없다.
    - 키 생성 전략이 확정되지 않은 개발 초기 단계, 프로토타입 개발시 편리.

### 6-6. 기본 키 매핑 정리

---

- 테이블의 기본 키 선택 전략 2가지
    - 자연 키
        - 비지니스에 의미가 있는 키
        - 주민등록번호, 이메일, 전화번호
    - 대리 키
        - 비지니스와 관련 없는 임의로 만들어진 키, 대체 키.
        - 오라클 시퀀스, auto_increment, 키생성 테이블

- 미래까지 충족하는 자연 키를 찾기 쉽지 않다.
- JPA 는 모든 엔티티에 일관된 방식으로 대리 키 사용을 권장한다.


## 7. 필드와 컬럼 매핑: 레퍼런스

### 7-1. `@Column`

---

- 컬럼을 매핑한다.

### 7-2. `@Enumerated`

---

- 자바의 enum 타입을 매핑한다.

### 7-3. `@Temporal`

---

- 날짜 타입을 매핑한다.

### 7-4. `@Lob`

---

- BLOB, CLOB 타입을 매핑한다.

### 7-5. `@Transient`

---

- 특정 필드를 데이터베이스에 매핑하지 않는다.
- 데이터베이스에 저장하지 않고 조회하지도 않는다.
- 객체에 임시로 어떤 값을 보관하고 싶을 때 사용

### 7-6. `@Access`

---

- JPA 가 엔티티에 접근하는 방식을 지정
    - 필드 접근
        - AccessType.FIELD로 지정
        - 필드에 직접 접근
        - `private`이어도 접근
    - 프로퍼티 접근
        - `AccessType.PROPERTY`로 접근
        - 접근자(Getter)를 사용

- 필드 접근 코드
    
    ```java
    import javax.persistence.Access;
    import javax.persistence.AccessType;
    import javax.persistence.Column;
    import javax.persistence.Entity;
    import javax.persistence.Id;
    import javax.persistence.Transient;
    
    **@Access(AccessType.FIELD)**
    @Entity
    public class Professor {
      public static String LOCAL_AREA_CODE = "999";
    
      @Id
      private int id;
    
      @Transient
      private String phoneNum;
    
      public int getId() {
        return id;
      }
    
      public void setId(int id) {
        this.id = id;
      }
    
      public String getPhoneNumber() {
        return phoneNum;
      }
    
      public void setPhoneNumber(String num) {
        this.phoneNum = num;
      }
    
      **@Access(AccessType.PROPERTY)**
      @Column(name = "PHONE")
      protected String getPhoneNumberForDb() {
        if (null != phoneNum && phoneNum.length() == 10)
          return phoneNum;
        else
          return LOCAL_AREA_CODE + phoneNum;
      }
    
      protected void setPhoneNumberForDb(String num) {
        if (num.startsWith(LOCAL_AREA_CODE))
          phoneNum = num.substring(3);
        else
          phoneNum = num;
      }
    }
    ```
    

## 기타

### Entity listeners 와 Callback methods

---

- `@PrePersist`: EntityManager `persist` 작업이 실제로 실행되거나 cascaded 전에 실행됩니다. 이 호출은 `persist` 작업과 동기화됩니다.
- `@PreRemove`: EntityManager `remove` 작업이 실제로 실행되거나 cascaded 전에 실행됩니다. 이 호출은 `remove` 작업과 동기화됩니다.
- `@PostPersist`: EntityManager `persist` 작업이 실제로 실행되거나 cascaded 후에 실행됩니다. 이 호출은 데이터베이스 INSERT 가 실행된 후에 호출됩니다.
- `@PostRemove`: EntityManager `remove` 작업이 실제로 실행되거나 cascaded 후 실행됩니다. 이 호출은 `remove` 작업과 동기화됩니다.
- `@PreUpdate`: 데이터베이스 UPDATE 작업 전에 실행됩니다.
- `@PostUpdate`: 데이터베이스 UPDATE 작업 후에 실행됩니다.
- `@PostLoad`: 엔티티가 현재 영속성 컨텍스트에 로드되거나 엔티티가 refresh 된 후 실행됩니다.

- 참고
    - [https://ultrakain.gitbooks.io/jpa/content/chapter4/chapter4.7.html](https://ultrakain.gitbooks.io/jpa/content/chapter4/chapter4.7.html)
