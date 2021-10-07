# Chapter 2. JPA 시작

## 1. 인텔리제이 설치와 프로젝트 불러오기

- [https://github.com/holyeye/jpabook.git](https://github.com/holyeye/jpabook.git) 클론
- `jpa-study` 루트 프로젝트 생성
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled.png)
    
    - `.gitignore` 추가
        
        ```yaml
        build/
        .idea/
        .gradle/
        *.env
        .run/
        ```
        
    - 루트 `build.gradle`
        
        ```yaml
        plugins {
            id 'org.springframework.boot' version '2.5.5'
            id 'io.spring.dependency-management' version '1.0.11.RELEASE'
            id 'java'
        }
        
        repositories {
            mavenCentral()
        }
        
        allprojects {
            group 'com.jenny'
            version '1.0-SNAPSHOT'
            sourceCompatibility = '11'
        }
        
        subprojects {
            apply plugin: 'org.springframework.boot'
            apply plugin: 'io.spring.dependency-management'
            apply plugin: 'java'
        
            repositories {
                mavenCentral()
            }
        
            dependencies {
                implementation 'org.springframework.boot:spring-boot-starter'
                runtimeOnly 'com.h2database:h2' // 1.4.200
                testImplementation 'org.springframework.boot:spring-boot-starter-test'
            }
        }
        
        test {
            useJUnitPlatform()
        }
        ```
        
    - 서브모듈 ch02-jpa-start1 추가➕  그 `build.gradle`에 의존성 추가
        
        ```yaml
        dependencies {
            implementation 'org.hibernate:hibernate-entitymanager' // 5.4.32
        }
        ```
        
- `resources/META-INF/persistence.xml` 추가
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!--<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">-->
    <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">
    
        <persistence-unit name="jpabook">
            **<class>jpabook.start.Member</class>**
            <properties>
                <!-- 필수 속성 -->
                <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
                <property name="javax.persistence.jdbc.user" value="sa"/>
                <property name="javax.persistence.jdbc.password" value=""/>
                **<property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/IdeaProjects/jpa-study/test"/>**
                <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" />
    
                <!-- 옵션 -->
                <property name="hibernate.show_sql" value="true" />
                <property name="hibernate.format_sql" value="true" />
                <property name="hibernate.use_sql_comments" value="true" />
                <property name="hibernate.id.new_generator_mappings" value="true" />
    
                <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
            </properties>
        </persistence-unit>
    
    </persistence>
    ```
    

## 2. H2 데이터베이스 설치

- [http://www.h2database.com/html/download.html](http://www.h2database.com/html/download.html) 에서 1.4.200 버전 `Platform-Independent Zip` 파일 다운로드
    
    ```bash
    jenny@gim-yunhuiui-MacBookPro Downloads % mv ~/Downloads/h2 ../IdeaProjects/jpa-study
    jenny@gim-yunhuiui-MacBookPro Downloads % cd ../IdeaProjects/jpa-study/h2/bin
    jenny@gim-yunhuiui-MacBookPro bin % ll
    h2-1.4.199.jar	h2.bat		h2.sh		h2w.bat
    ```
    
- h2 실행 권한 주기
    
    ```bash
    jenny@gim-yunhuiui-MacBookPro bin % chmod 755 h2.sh
    ```
    
- 설치된 H2 데이터베이스 실행
    
    ```bash
    jenny@gim-yunhuiui-MacBookPro bin % java -jar h2-1.4.200.jar
    ```
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%201.png)
    
    - JDBC URL: `jdbc:h2:~/IdeaProjects/jpa-study/test`로 변경해서 연결한다.
        - 권한 주기: chmod 755 h2.sh
            - 권한 문제 때문에 tcp로 접근하면, 데이터베이스가 없는 경우 자동 생성하지 않는다.
        
        👉  ...왜 않되지..??
        
        - 저 같은 분들을 위한 직접 데이터베이스 생성하기
            - [https://h2database.com/html/tutorial.html#creating_new_databases](https://h2database.com/html/tutorial.html#creating_new_databases)
            
            ```bash
            jenny@gim-yunhuiui-MacBookPro bin % java -cp h2-*.jar org.h2.tools.Shell
            
            Welcome to H2 Shell 1.4.200 (2019-10-14)
            Exit with Ctrl+C
            [Enter]   jdbc:h2:mem:base
            URL       jdbc:h2:~/IdeaProjects/jpa-study/test
            [Enter]   org.h2.Driver
            Driver
            [Enter]   base
            User      sa
            Password
            Type the same password again to confirm database creation.
            Password
            Connected
            Commands are case insensitive; SQL statements end with ';'
            help or ?      Display this help
            list           Toggle result list / stack trace mode
            maxwidth       Set maximum column width (default is 100)
            autocommit     Enable or disable autocommit
            history        Show the last 20 statements
            quit or exit   Close the connection and exit
            
            sql> quit
            Connection closed
            jenny@gim-yunhuiui-MacBookPro bin %
            ```
            
        - `test.mv.db` 가 생겼고 접근 가능한 것을 확인할 수 있다.
            
            ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%202.png)
            

### 예제 테이블 생성

---

- MEMBER 테이블을 생성해보자
- 회원테이블 DDL
    
    ```sql
    CREATE TABLE MEMBER (
        ID LONG AUTO_INCREMENT NOT NULL,    -- 아이디(기본키)
        NAME VARCHAR(255),                  -- 이름
        AGE INTEGER NOT NULL,               -- 나이
        PRIMARY KEY (ID)
    )
    ```
    
- 회원 정보 일부 입력
    
    ```sql
    INSERT INTO MEMBER(NAME, AGE) VALUES('jenny', 32);
    ```
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%203.png)
    

## 3. 라이브러리와 프로젝트 구조

- 저는 Gradle 을 사용하여 필요한 라이브러리 다운로드 할 예정입니다.
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%204.png)
    

- JPA 구현체 하이버네이트 핵심 라이브러리
    - `hibernate-core`: 하이버네이트 라이브러리
    - `hibernate-entitymanager`: 하이버네이트가 JPA 구현체로 동작하도록 JPA 표준을 구현한 라이브러리
    - `hibernate-jpa-2.1-api`: JPA 2.1 표준 API를 모아둔 라이브러리

### 3-1. 메이븐과 사용 라이브러리 관리

---

- 메이븐: 자바 애플리케이션은 메이븐 같은 도구를 사용해서 라이브러리를 관리, 빌드
    - 라이브러리 관리 기능
        - 자바 애플리케이션을 개발하려면 jar 파일로 된 여러 라이브러리가 필요하다.
        - 예전에는 직접 내려 받아 사용했다.
        - 메이븐은 사용할 라이브러리 이름과 버전만 명시하면 라이브러리를 자동으로 내려받고 관리해준다.
    - 빌드 기능
        - 애플리케이션을 직접 빌드하는 것은 고된 작업이다.
        - 애플리케이션을 빌드하는 표준화된 방법을 제공한다.

- 메이븐 설정 파일: `pom.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
    
        <groupId>jpabook</groupId>
        <artifactId>ch02-jpa-start1</artifactId>
        <version>1.0-SNAPSHOT</version>
    
        <properties>
    
            <!-- 기본 설정 -->
            <java.version>1.6</java.version>
            <!-- 프로젝트 코드 인코딩 설정 -->
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    
            <!-- JPA, 하이버네이트 버전 -->
            <hibernate.version>4.3.10.Final</hibernate.version>
            <!-- 데이터베이스 버전 -->
            <h2db.version>1.4.187</h2db.version>
    
        </properties>
    
        <dependencies>
            <!-- JPA, 하이버네이트 -->
            <dependency>
                <groupId>org.hibernate</groupId>
                <artifactId>hibernate-entitymanager</artifactId>
                <version>${hibernate.version}</version>
            </dependency>
            <!-- H2 데이터베이스 -->
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <version>${h2db.version}</version>
            </dependency>
        </dependencies>
    
    </project>
    ```
    
- `<dependencies>`에 사용할 라이브러리 지정
    - `groupId + artifactId + version`만 적어주면 라이브러리를 메이븐 공식 저장소([http://www.mvnrepository.com/](http://www.mvnrepository.com/))에서 내려받아 라이브러리를 추가해준다.
- JPA, 하이버네이트(`hibernate-entitymanager`) 의존성
    - JPA 표준과 하이버네이트를 포함하는 라이브러리 `hibernate-entitymanager`를 라이브러리로 지정하면 다음 중요 라이브러리를 함께 내려받는다.
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%205.png)
    
    - `hibernate-core`.jar
    - `hibernate-jpa-2.1-api`.jar


## 4. 객체 매핑 시작

- 회원 테이블 생성
    
    ```sql
    CREATE TABLE MEMBER (
        ID VARCHAR(255) NOT NULL, --아이디(기본 키)
        NAME VARCHAR(255),        --이름
        AGE INTEGER NOT NULL,     --나이
        PRIMARY KEY (ID)
    )
    ```
    
- 회원 클래스 생성
    
    ```java
    package jpabook.start;
    
    public class Member {
    
        private Long id;
        private String username;
        private Integer age;
    
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
    
        public String getUsername() {
            return username;
        }
    
        public void setUsername(String username) {
            this.username = username;
        }
    
        public Integer getAge() {
            return age;
        }
    
        public void setAge(Integer age) {
            this.age = age;
        }
    }
    ```
    
- 매핑정보
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%206.png)
    

- 매핑 정보가 포함된 회원 클래스
    
    ```java
    package jpabook.start;
    
    import javax.persistence.Column;
    import javax.persistence.Entity;
    import javax.persistence.Id;
    import javax.persistence.Table;
    
    @Entity
    @Table(name = "MEMBER")
    public class Member {
    
        @Id
        @Column(name = "ID")
        private String id;
    
        @Column(name = "NAME")
        private String username;
    
        private Integer age;
    
        public String getId() {
            return id;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public String getUsername() {
            return username;
        }
    
        public void setUsername(String username) {
            this.username = username;
        }
    
        public Integer getAge() {
            return age;
        }
    
        public void setAge(Integer age) {
            this.age = age;
        }
    }
    ```
    
    - 클래스와 테이블 매핑
        
        ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%207.png)
        
    

### 매핑 어노테이션

---

- JPA 는 매핑 어노테이션을 분석해서 어떤 객체가 어떤 테이블과 관계가 있는지 알아낸다.
    - 위의 예시에서 `@Entity`, `@Table`, `@Column`이 매핑 정보다.
- `@Entity`
    - 테이블과 매핑한다고 JPA에 알려준다.
    - `@Entity`가 사용된 클래스를 엔티티 클래스라고 한다.
- `@Table`
    - 엔티티 클래스와 매핑할 테이블 정보를 알려준다.
    - name 속성을 사용해서 Member 엔티티를 `MEMBER` 테이블에 매핑
    - 이 어노테이션을 생략하면 클래스 이름을 그대로 테이블 이름으로 매핑한다.
        - 정확히는 엔티티 이름을 사용한다.
- `@Id`
    - 엔티티 클래스의 필드를 테이블의 기본 키(Primary Key)에 매핑
    - 엔티티의 id 필드를 테이블의 ID 기본 키 컬럼에 매핑
    - `@Id`가 사용된 필드를 식별자 필드라 한다.
- `@Column`
    - 필드를 컬럼에 매핑한다.
    - name 속성을 사용해서 Member 엔티티의 username 필드를 테이블의 `NAME` 컬럼에 매핑
- 매핑 정보가 없는 필드
    - 매핑 어노테이션을 생략하면 필드명을 사용해서 컬럼명으로 매핑 (default)
    - 필드명이 age 이므로 age 컬럼으로 매핑.
    - 만약 대소문자를 구분하는 데이타베이스를 사용하면 `@Column(name="AGE")`처럼 명시적으로 매핑해야 한다.


## 5. persistence.xml 설정

- JPA는 `persistence.xml`을 사용해서 필요한 설정 정보를 관리
- META-INF/persistence.xml 클래스 패스 경로에 있으면 별도 설정없이 JPA 가 인식할 수 있다.
- JPA 환경설정 파일 `persistence.xml`
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!--<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.1">-->
    <persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" version="2.2">
    
        <persistence-unit name="jpabook">
            **<class>jpabook.start.Member</class>**
            <properties>
                <!-- 필수 속성 -->
                <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
                <property name="javax.persistence.jdbc.user" value="sa"/>
                <property name="javax.persistence.jdbc.password" value=""/>
                **<property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/IdeaProjects/jpa-study/test"/>**
                <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" />
    
                <!-- 옵션 -->
                <property name="hibernate.show_sql" value="true" />
                <property name="hibernate.format_sql" value="true" />
                <property name="hibernate.use_sql_comments" value="true" />
                <property name="hibernate.id.new_generator_mappings" value="true" />
    
                <!--<property name="hibernate.hbm2ddl.auto" value="create" />-->
            </properties>
        </persistence-unit>
    
    </persistence>
    ```
    

- 설정 파일은 `<persistence>`로 시작
- JPA 설정은 영속성 유닛(`persistence-unit`)으로 시작
    - 일반적으로 연결할 데이터베이스 하나당 영속성 유닛 등록한다.
    - 고유한 이름을 부여, 여기서는 jpabook

### 사용한 속성

---

- JPA 표준 속성
    - `javax.persistence.jdbc.driver`: JDBC 드라이버
    - `javax.persistence.jdbc.user`: 데이터베이스 접속 아이디
    - `javax.persistence.jdbc.password`: 데이터베이스 접속 비밀번호
    - `javax.persistence.jdbc.url`: 데이터베이스 접속 URL
- 하이버네이트 설정
    - `hibernate.dialect`: 데이터베이스 방언 설정

### 5-1. 데이터베이스 방언

---

- JPA 는 특정 데이터베이스에 종속적이지 않은 기술이다. → 다른 데이터베이스로 손쉽게 교체할 수 있다.
- 데이터베이스마다 차이점
    - 데이터 타입
    - 다른 함수명
    - 페이징 처리

→ SQL 표준을 지키지 않거나 특정 데이터베이스만의 고유한 기능을 JPA 에서는 Dialect(방언)이라고 한다.

### 하이버네이트 설정 옵션

---

- 옵션
    - `hibernate.show_sql`: 실행한 SQL을 출력한다.
    - `hibernate.format_sql`: SQL을 보기 쉽게 정렬한다.
    - `hibernate.use_sql_comments`: 쿼리 출력 시 주석도 함께 출력한다.
    - `hibernate.id.new_generator_mappings`: JPA 표준에 맞는 새로운 키 생성 전략을 사용한다.
- 하이버네이트 설정
    - `create`: Session factory 가 실행될 때에 스키마를 지우고 다시 생성한다. 클래스패스에 `import.sql` 이 존재하면 찾아서 해당 SQL 도 함께 실행한다.
    - `create-drop`: `create`와 같지만 session factory 가 내려갈 때 스키마 삭제
    - `update`: 시작 시, 도메인과 스키마 비교하여 필요한 컬럼 추가 등 작업 실행. 데이터는 삭제하지 않음.
    - `validate`: Session factory 실행 시 스키마가 적합한지 검사한다. 문제가 있으면 예외 발생
    
    → 개발시에는 `create` 가, 운영시에는 auto 설정을 빼거나 `validate` 정도로 두는 것이 좋아보인다.(`update`로 둘 경우, 개발자들의 스키마가 마구 꼬여서 결국은 drop 후 새로 만들어야 하는 사태가 발생)
    

## 6. 애플리케이션 개발

- 객체 매핑 완료
- `persistence.xml`로 JPA 설정 완료

- 시작 코드
    
    ```java
    package jpabook.start;
    
    import javax.persistence.EntityManager;
    import javax.persistence.EntityManagerFactory;
    import javax.persistence.EntityTransaction;
    import javax.persistence.Persistence;
    import java.util.List;
    
    public class JpaMain {
    
        public static void main(String[] args) {
            //엔티티 매니저 팩토리 생성
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
            EntityManager em = emf.createEntityManager(); //엔티티 매니저 생성
    
            EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득
    
            try {
    
                tx.begin(); //트랜잭션 시작
                logic(em);  //비즈니스 로직
                tx.commit();//트랜잭션 커밋
    
            } catch (Exception e) {
                e.printStackTrace();
                tx.rollback(); //트랜잭션 롤백
            } finally {
                em.close(); //엔티티 매니저 종료
            }
    
            emf.close(); //엔티티 매니저 팩토리 종료
        }
    
        public static void logic(EntityManager em) {
            String id = "id1";
            Member member = new Member();
            member.setId(id);
            member.setUsername("제니");
            member.setAge(2);
    
            //등록
            em.persist(member);
    
            //수정
            member.setAge(20);
    
            //한 건 조회
            Member findMember = em.find(Member.class, id);
            System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());
    
            //목록 조회
            List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
            System.out.println("members.size=" + members.size());
    
            //삭제
            em.remove(member);
    
        }
    }
    ```
    
- 코드 구성
    - 엔티티 매니저 설정
    - 트랜잭션 관리
    - 비지니스 로직

### 6-1. 엔티티 매니저 설정

---

- 엔티티 매니저의 생성 과정
    
    ![Untitled](Chapter%202%20JPA%20%E1%84%89%E1%85%B5%E1%84%8C%E1%85%A1%E1%86%A8%2086a43ed87400411ab10ad3c32ae248d5/Untitled%208.png)
    
    - 엔티티 매니저 팩토리 생성 👉  애플리케이션 전체에 딱 한 번만 생성하고 공유해서 사용해야 한다.
        - `persistence.xml`의 설정 정보를 사용해서 엔티티 매니저 팩토리 생성 → `Persistence` 클래스 사용
        - `Persistence` 클래스: 엔티티 매니저 팩토리를 생성해서 JPA 를 사용할 수 있게 준비한다.
        
        ```java
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
        ```
        
        - META-INF/persistence.xml 에서 이름이 jpabook 인 영속성 유닛을 찾아서 엔티티 매니저 팩토리를 생성한다.
            - `persistence.xml` 설정 정보 읽어서
            - JPA 를 동작시키기 위한 기반 객체 만들고
            - JPA 구현체에 따라서 데이터베이스 커넥션 풀도 생성
        
        → 비용이 아주 크다.
        
    - 엔티티 매니저 생성
        
        ```java
        EntityManager em = emf.createEntityManager();
        ```
        
        - 엔티티 매니저를 사용해서 엔티티를 데이터베이스에 등록/수정/삭제/조회할 수 있다.
        - 엔티티 매니저는 데이터베이스 커넥션과 밀접한 관계가 있으므로 스레드 간에 공유하거나 재사용하면 안된다.
    - 종료
        - 사용이 끝난 엔티티 매니저는 반드시 종료해야 한다.
            
            ```java
            em.close();     // 엔티티 매니저 종료
            ```
            
        - 애플리케이션을 종료할 때 엔티티 매니저 팩토리도 종료해야 한다.
            
            ```java
            emf.close();    // 엔티티 매니저 팩토리 종료
            ```
            

### 6-2. 트랜잭션 관리

---

- JPA 를 사용하면 항상 트랜잭션 안에서 데이터를 변경해야 한다.
- 트랜잭션 없이 데이터를 변경하면 예외 발생
- 트랜잭션을 시작하려면 엔티티 매니저에서 트랜잭션 API 를 받아와야 한다.

- 트랜잭션 코드 부분
    
    ```java
    EntityTransaction tx = em.getTransaction(); //트랜잭션 기능 획득
    
    try {
    
        tx.begin(); //트랜잭션 시작
        logic(em);  //비즈니스 로직
        tx.commit();//트랜잭션 커밋
    
    } catch (Exception e) {
        tx.rollback(); //트랜잭션 롤백
    }
    ```
    

### 6-3. 비즈니스 로직

---

- 회원 엔티티를 하나 생성한 다음 엔티티 매니저를 통해 데이터베이스에 등록, 수정, 삭제, 조회한다.

- 비즈니스 로직 코드
    
    ```java
    public static void logic(EntityManager em) {
        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("제니");
        member.setAge(2);
    
        //등록
        em.persist(member);
    
        //수정
        member.setAge(20);
    
        //한 건 조회
        Member findMember = em.find(Member.class, id);
        System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());
    
        //목록 조회
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        System.out.println("members.size=" + members.size());
    
        //삭제
        em.remove(member);
    
    }
    ```
    
- 수정
    - `em.update()`를 호출할 것 같은데 없다.
    - 단순하게 엔티티의 값만 변경한다.
    - JPA 는 어떤 엔티티가 변경되었는지 추적하는 기능을 갖추고 있다.

### 6-4. JPQL

---

- 하나 이상의 회원 목록 조회하는 코드
    
    ```java
    //목록 조회
    List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
    System.out.println("members.size=" + members.size());
    ```
    
    - from Member 는 `MEMBER` 테이블이 아닌 Member 회원 엔티티 객체
- 문제점
    - JPA 는 엔티티 객체를 중심으로 개발하므로, 검색할 때도 엔티티 대상으로 검색해야 한다.
    - 테이블이 아닌 엔티티 객체를 대상으로 검색하려면 → 사실상 불가능하다.
        - 데이터베이스 모든 데이터를 애플리케이션으로 불러와 엔티티 객체로 변경해서 검색해야 함

- JPA 는 SQL 을 추상화한 JPQL(Java Persistence Query Language)라는 쿼리 언어를 제공한다.
    - 차이점
        - JPQL: **엔티티 객체**(클래스와 필드)를 대상으로 쿼리
        - SQL: **데이터베이스 테이블**을 대상으로 쿼리
    - JPQL 표현: `select m from Member m`
    - 특징
        - JPQL 은 데이터베이스 테이블을 전혀 알지 못한다.
        - JPA 는 JPQL 을 분석, 적절한 SQL 을 만들어 데이터베이스에서 데이터를 조회한다.
            
            ```sql
            SELECT M.ID, M.NAME, M.AGE FROM MEMBER M
            ```
            
        - JPQL 은 대소문자를 구분한다.

- 참고
    - [https://ultrakain.gitbooks.io/jpa/content/chapter2/chapter2.6.html](https://ultrakain.gitbooks.io/jpa/content/chapter2/chapter2.6.html)
    - [https://medium.com/@oopchoi/jpa-프로그래밍-fc443b647ec8#.i1urphfhi](https://medium.com/@oopchoi/jpa-%ED%94%84%EB%A1%9C%EA%B7%B8%EB%9E%98%EB%B0%8D-fc443b647ec8#.i1urphfhi)