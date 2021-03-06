# 토비의 스프링



# 1. 스프링이란 무엇인가?

- 스프링 컨테이너 : 애플리케이션의 기본 틀;;

  스프링은 Spring Container, Application Context 라고도 불리는 스프링 런타임 엔진을 제공.

  Spring Container는 설정정보를 참고로 해서 애플리케이션을 구성하는 오브젝트를 생성, 관리.

- 공통 프로그래밍 모델

  - IoC/DI : 오브젝트의 생명주기와 의존관계에 대한 프로그래밍 모델.

    스프링 프레임워크에서 동작하는 코드는 IoC/DI 방식을 따라서 작성돼어야 스프링이 제공하는 가치를 제대로 누릴 수 있다.

    스프링이 제공하는 모든 기술, API, 컨테이너는 IoC/DI 방식으로 작성돼 있다. 스프링을 이해하는 기본, 중요한 기술.

  - 서비스 추상화 : 특정 환경, 서버에 종속되지 않고 이식성이 뛰어나며 유연한 어플리케이션을 만들 수 있다.

  - AOP : 스프링은 AOP를 이용해서 다양한 엔터프라이즈 서비스를 적용하고도 깔끔한 코드를 유지할 수 있게 해준다.



# 2. 스프링의 성공 요인

- 단순함 : 자바가 복잡해지며 객체지향 언어라는 특징을 점점 잃어버리게 되었으나 -> 스프링은 객체지향 언어의 장점을 살릴 수 있도록 도와주는 도구.

  -> POJO 프로그래밍

- 유연성 : 스프링의 유연성으로 인해 다른 프레임워크와도 편리하게 접목돼어 사용가능.



# 3. 스프링 3.1에 추가된 기능

- 강화된 자바 코드를 이용한 빈 설정

  기존에 XML로 작성했던 스프링 설정 정보를 자바 코드로 대체할 수 있다.

- 런타임 환경 추상화

  실행환경에 따라 달라지는 빈 설정을 효과적으로 관리할 수 있는 프로파일,

  각종 프로퍼티 정보를 컨테이너를 통해 일관된 방식으로 제공할 수 있게 해주는 프로퍼티 소스.

- JPA 지원 확장, 하이버제이트 4 지원.

- 새로운 DispatcherServlet 전략, 플래시 맵

  MVC 기능을 확장하기가 편리해짐. Post/Redirect/Get 패턴에 사용할 수 있는 플래시 맵 기능도 추가.

- 캐시 추상화

  AOP를 이용한 메소드 레벨의 캐시 추상화 기능 추가.



# 4. DAO

- JDBC를 이용하는 작업의 일반적 순서
  1. DB 연결을 위한 Connection을 가져온다.
  2. SQL을 담은 Statement(또는 PreparedStatement)를 만든다.
  3. 만들어진 Statement실행.
  4. 조회의 경우, SQL 쿼리의 실행 결과를 ResultSet으로 받아서 정보를 저장할 오브젝트에 옮겨준다.
  5. Connection, Statement, ResultSet 같은 리소스는 작업을 마친 후 반드시 닫는다.
  6. JDBC API가 만들어내는 Exception은 직접 처리하거나 메소드 밖으로 던지게 한다.

```java
package dao;

import domain.User;

import java.sql.*;

public class UserDao {
    public void add(User user) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/study_db?serverTimezone=UTC",
                "root",
                "1234"
        );

        PreparedStatement ps = c.prepareStatement(
                "insert into users(id, name, password) " +
                        "values(?,?,?)"
        );

        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/study_db?serverTimezone=UTC",
                "root",
                "1234"
        );

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDao dao = new UserDao();

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }
}

```



# DAO 수정하기

- 관심사의 분리 : 변화는 한 가지에 집중돼서 일어난다.

  -> 관심사가 같은 것끼리 모아두는 것. 다른 것들은 따로 떨어뜨리는 것.

위의 UserDao 의 관심사항

- DB Connection
- SQL 문장을 만들고 실행하는 것
- 작업이 끝난 리소스를 Close 하는 것



### 첫번째 리팩토링. DB Connection 분리

```java
private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/study_db?serverTimezone=UTC",
                "root",
                "1234"
        );

        return c;
    }
```

-> 공통의 기능을 담당하는 메소드로 중복된 코드를 추출 : 메소드 추출



```java
public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
```

-> 추상 메서드로 상속을 통해 확장을 하도록 구현.

-> 같은 getConnection 메서드를 상황에 따라 다르게 구현해서 사용이 가능!

-> 슈퍼클래스에 기본적 로직의 흐름을 만들고, 그 기능의 일부를 서브클래스에서

필요에 맞게 구현하는 디자인 패턴 : 템플릿 메서드 패턴, 또는 팩토리 메서드 패턴



#### 템플릿 메소드 패턴

- 상속을 통해 슈퍼클래스의 기능을 확장하는 가장 대표적 방법
- 변하지 않는 기능은 슈퍼클래스에 만들고, 자주 변경, 확장되는 기능은 서브클래스에 만든다.
- 훅 메서드 : 슈퍼클래스에서 디폴트 기능을 정의하거나, 비워둠으로써 서브클래스에서 선택적으로 오버라이드할 수 있도록 만든 메서드.

```java
public abstract class Super {
    public void templateMethod() {
        // 기본 알고리즘 코드
        hookMethod();
        abstractMethod();
    }
    
    //선택적으로 오버라이드 가능한 훅 메서드
    protected void hookMethod() {}
    //서브클래스에서 반드시 구현해야 하는 추상 메서드
    public abstract void abstractMethod();
}
```



#### 팩토리 메소드 패턴

- 상속을 통해 기능을 확장 -> 템플릿 패턴과 비슷
- 슈퍼클래스에서는 서브클래스에서 구현할 메서드를 호출해서 필요한 타입의 오브젝트를 가져와 사용. 주로 오브젝트를 리턴하도록 만들어져서 서브클래스에서 어떤 타입으로 리턴할지는 관심을 두지 않는다.



상속의 문제점 : 슈퍼 클래스, 서브 클래스 간의 종속관계가 강하게 맺어져 있으므로 변경에 취약하게 됨.





# 클래스의 분리

```java
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionMaker {
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost/study_db?serverTimezone=UTC",
                "root",
                "1234"
        );

        return c;
    }
}

```

-> 아예 다른 클래스로 빼버려서 DB Connection을 정의.

-> 이때, 발생하는 문제점

: 상속을 통해서는 N사, D사에 UserDao 클래스만 공급하고 상속을 통해 DB Connection을 확장해서 사용하게 했던 것이 불가능.

UserDao의 코드가 SimpleConnectionMaker 클래스에 종속돼버림. 즉, UserDao 코드의 수정없이 DB 커넥션 생성 기능을 변경하지 못함.

-> UserDao가 바뀔 수 있는 정보, 즉 DB 커넥션을 가져오는 클래스에 대해 너무 많이 알고 있기 때문. -> 인터페이스를 통해 해결.

![image-20211227012523501](readme_images/image-20211227012523501.png)



```java
package dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
    Connection getConnection() throws ClassNotFoundException, SQLException;
}

```

-> ConnectionMaker 라는 인터페이스를 만듦으로써 N사, D사가 UserDao의

코드 변경없이 가져다 쓸 수 있게 하고자 만드는 의도. But, 그러나



```java
private ConnectionMaker connectionMaker;

public UserDao() {
    connectionMaker = new NConnectionMaker();
}
```

UserDao 클래스를 생성할 때, 어떤 ConnectionMaker 타입을 가져와야 할지를 명시해야 하므로 여전히, D사가 가져다가 DConnectionMaker를 만들고 싶으면 코드를 수정해야 함.

-> 아직 UserDao 클래스 안에는 어떤 ConnectionMaker를 가져다 쓸 것인지에 대한 관심이 남아 있기 때문임.

즉, 다음과 같은 의존관계를 해결해야함.

![image-20211227013710272](readme_images/image-20211227013710272.png)

UserDao의 모든 코드가 ConnectionMaker 인터페이스 외에는 어떤 클래스와도 관계를 가지지 않게 해야함.

그러므로, UserDao가 DConnectionMaker, NConnectionMaker 와 관계를 맺는 것은 불가피하나, 런타임 사용관계, 또는 의존관계를 만들어주면 된다. 그래서 클래스 관계를 맺는 것이 아니라 오브젝트 간 관계를 맺어야 한다.



그래서 UserDao 클래스를 사용하는 main() 메서드 즉, UserDao의 클라이언트를 떨어뜨려놔야 한다. UserDaoTest 라는 이름의 클래스를 만들고 main 메서드를 추가한다.

```java
package dao;

import domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        ConnectionMaker connectionMaker = new SimpleConnectionMaker();

        UserDaoJdbc dao = new UserDaoJdbc(connectionMaker);

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공");
    }
}

```

-> UserDao와 ConnectionMaker 구현 클래스와의 런타임 오브젝트 의존 관계를 설정하는 책임을 담당.



# 원칙과 패턴

### 개방 폐쇄 원칙(Open-Closed Principle)

- 클래스나 모듈은 확장에는 열려있어야 하고, 변경에는 닫혀 있어야 한다



### 높은 응집도와 낮은 결합도

- 높은 응집도 : 관심사가 같은 로직들만 모아둠.
- 낮은 결합도 : 관심사가 다른 오브젝트, 모듈과는 낮은 결합도를 유지한다.



### 전략 패턴(Strategy Pattern)

- 위처럼 UserDaoTest-UserDao-ConnectionMaker 구조처럼 돼있는 패턴을

  Strategy Pattern 이라고 부른다.

- Context에서 필요에 따라 변경이 필요한 알고리즘을 필요에 따라 바꿔서 사용할 수 있게 하는 디자인 패턴.

  UserDao가 컨텍스트에 해당하고, DB 커넥션을 맺는 ConnectionMaker를 인터페이스로 정의하여 이를 구현한 클래스를 바꿔가면서 사용할 수 있게 분리하였음. 그리고 UserDaoTest라는 클라이언트를 통해 컨텍스트가 사용할 전략을 제공하였음.





# 제어의 역전(IoC)



## 오브젝트 팩토리

##### 팩토리

객체 생성 방법을 결정하고 오브젝트를 돌려주는 오브젝트.

DaoFactory.java

```java
package dao;

public class DaoFactory {
    public UserDaoJdbc userDao() {

        ConnectionMaker connectionMaker = new SimpleConnectionMaker();
        UserDaoJdbc userDao = new UserDaoJdbc(connectionMaker);

        return userDao;
    }
}
```



UserDaoTest.java

```java
public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDao dao = new DaoFactory().userDao();
}
```

이제 main() 메서드에서 UserDao가 어떻게 생성되는지 신경쓰지 않아도 된다.
![캡처](readme_images/캡처.PNG)

N사, D사에 UserDao를 공급할 때, UserDao, ConnectionMaker와 함께 
DaoFactory도 제공한다. UserDao는 변경하지 않고, DaoFactory를 필요에 맞게
변경해서 쓰면 된다.
-> DaoFactory를 UserDaoTest에서 분리함으로써 얻는 장점은
컴포넌트 역할의 오브젝트와 애플리케이션 구조를 결정하는 오브젝트를
분리한 것이 가장 큰 의미가 있다.

```java
public class DaoFactory {
    public UserDao userDao() {
        return new UserDao(new DConnectionMaker());
    }

    public AccountDao accountDao() {
        return new AccountDao(new DConnectionMaker());
    }
}
```

이처럼 Dao가 많아지면 ConnectionMaker의 구현 클래스를 바꿀 때마다
모든 메서드를 일일이 수정해야 한다. 따라서 이것도 밖으로 빼준다.

```java
public ConnectionMaker connectionMaker() {
    return new DConnectionMaker();    
}
```


# 제어권의 이전을 통한 제어관계 역전

- 제어역전 : 프로그램의 제어 흐름 구조가 뒤바뀌는 것.
- 일반적인 프로그램의 흐름
  - main() -> 다음에 사용할 오브젝트 결정. -> 오브젝트 생성
    -> 오브젝트에 있는 메소드 호출 하는 일련의 과정을 반복.
  - 즉, 모든 오브젝트가 능동적으로 자신이 사용할 클래스를 결정하고
    언제, 어떻게 오브젝트를 만들지를 스스로 관장한다. 작업을 사용하는 쪽에서
    제어하는 구조.
  - 제어 역전이란 이런 제어 흐름을 거꾸로 뒤집는 것.
  - 오브젝트는 자기가 사용할 오브젝트를 스스로 선택하지 않고, 생성하지
    않으며, 자기 자신도 언제 만들어지는지 알지 못한다.
    -> 모든 제어 권한을 다른 대상에게 위임하기 때문이다.

# 오브젝트 팩토리를 이용한 스프링 IoC

- Bean : 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트
- 애플리케이션 컨텍스트와 빈 팩토리는 거의 동의어
- Application Context : 빈 생성, 관계설정 등의 제어작업 총괄


### DaoFactory를 사용하는 애플리케이션 컨텍스트

- @Configuration : 스프링이 빈 팩토리를 위한 오브젝트 설정을 담당하는 클래스라고 인식하게
하는 애노테이션
- @Bean : 오브젝트를 만들어주는 메소드에 붙여줌.

```java
@Configuration
public class DaoFactory {
    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new SimpleConnectionMaker();
    }
}

public static void main(String[] args) throws ClassNotFoundException, SQLException {

  ApplicationContext context =
          new AnnotationConfigApplicationContext(DaoFactory.class);
  UserDao dao = context.getBean("userDao", UserDao.class);
}
```

- getBean() : ApplicationContext가 관리하는 오브젝트를 요청.
- "userDao"는 컨텍스트에 등록된 빈의 이름, @Bean이 붙은 메소드 이름이
빈의 이름이 된다.

![캡처2](C:\Users\Administrator\IdeaProjects\spring-study\readme_images\캡처2.PNG)

애플리케이션 컨텍스트가 사용되는 방식

IoC 원리를 따르는 애플리케이션 컨텍스트를 사용할 때의 장점
- 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다.
  - XML처럼 단순한 방법을 사용해 IoC 설정정보를 만들 수도 있다.
- 종합 IoC 서비스를 제공해준다.
- 빈을 검색하는 다양한 방법을 제공.


# 싱글톤 레지스트리와 오브젝트 스코프

```java
DaoFactory factory = new DaoFactory();
UserDao dao1 = factory.userDao();
UserDao dao2 = factory.userDao();
```
직접 생성한 UserDao
-> dao1, dao2는 동일하지 않다.

```java
ApplicationContext = 
    new AnnotationConfigApplicationContext(DaoFactory.class);
UserDao dao3 = context.getBean("userDao", UserDao.class);
UserDao dao4 = context.getBean("userDao", UserDao.class);
```
스프링 컨텍스트로부터 가져온 오브젝트
-> dao3, dao4는 같다.

- 애플리케이션 컨텍스트는 싱글톤 레지스트리이다.
- 자바가 주로 쓰이는 서버 환경에서는 초당 수십개, 수백개의 오브젝트를
요청받는 경우가 많기 때문에 서버의 부하를 최소화하기 위함.
- 서블릿 클래스당 하나의 오브젝트만 만들고, 사용자의 요청을 담당하는 여러 스레드
에서 하나의 오브젝트를 공유해서 사용.

```java
 //싱글톤 패턴
   private UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public static synchronized UserDao getInstance() {
       if (INSTANCE == null) INSTANCE = new UserDao(???);
       return INSTANCE;
    }
```

일반적 싱글톤 패턴 구현의 문제점
- private 생성자를 갖고 있기 때문에 상속할 수 없다.
따라서, 객체지향 설계의 장점을 적용하기 어렵다. 스태틱 필드, 메서드를 사용하는 점도 객체지향 설계를
어렵게 만든다.
- 싱글톤은 테스트하기 힘들다. 만들어지는 방식이 제한적이어서
목 오브젝트 등으로 대체하기 힘들다.
- 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못함.
- 싱글톤 사용은 전역 상태를 만들 수 있어서 바람직하지 못함.
스태틱 메소드를 이용해 언제든지 싱글톤에 접근할 수 있어서 애플리케이션 어느 곳에서나
이용될 수 있다.

### 싱글톤 레지스트리

- 자바의 기본적인 싱글톤 패턴은 여러 단점이 있어서
스프링이 직접 싱글톤 형태의 오브젝트를 만들고 관리한다.
-> 싱글톤 레지스트리.
따라서 public 생성자를 가지므로 상속에 제약받지 않음.

### 싱글톤과 오브젝트의 상태

- 싱글톤이 멀티스레드 환경에서 서비스 형태의 오브젝트로 사용되는 경우 :
내부에 상태정보를 갖고 있지 않은 stateless 방식으로 생성되어야 함.
- 다중 스레드들이 동시에 하나의 싱글톤 오브젝트의 인스턴스 변수를 수정하는 것은 매우 위험!
- 따라서 요청이 들어올 때마다 매번 변수를 선언하는 로컬 변수, 파라미터, 리턴값등을 이용하면 된다.

인스턴스 변수를 사용하도록 수정한 UserDao

```java
import java.sql.SQLException;

public class UserDao {
  // 스프링이 관리하는 빈 오브젝트이므로 인스턴스로 사용해도 무방함.
  // 동일하게 읽기 전용 오브젝트라면 인스턴스 변수로 사용해도 좋다.
  private ConnectionMaker connectionMaker;
  //이들은 인스턴스로 사용하면 문제가 생긴다.
  private Connection c;
  private User user;

  public User get(String id) throws ClassNotFoundException, SQLException {
      this.c = connectionMaker.getConnection();
      this.user = new User();
      this.user.setId(rs.getString("id"));
      this.user.setName(rs.getString("name"));
      this.user.setPassword(rs.getString("password"));
      
      return this.user;
  }
}
```


# 의존관계 주입(DI)

### 런타임 의존관계 설정

A가 B에 의존하고 있다 : B가 변하면 A에 영향을 미친다.

인터페이스를 통한 느슨한 의존관계의 예
![](C:\Users\Administrator\IdeaProjects\spring-study\readme_images\img.png)

위의 경우처럼 UserDao가 ConnectionMaker 인터페이스를 사용하는 경우에는
어떤 클래스가 인터페이스를 구현하고 있는지에 대해 영향을 덜 받게 된다.

설계 시점에 느슨한 의존 관계를 갖는 경우, 런타임 시 오브젝트가 어떤 클래스로 만든 것인지
미리 알 수 없다.
프로그램이 시작되고 UserDao 오브젝트가 만들어지고 런타임 시에 의존관계를 맺는 대상을 
의존 오브젝트라고 한다.
즉, 의존관계 주입은 의존 오브젝트와 이것을 사용하는 클라이언트 오브젝트를 런타임 시에
연결해주는 것을 말한다. 의존관계 주입은 세가지 조건을 충족하는 작업이다.

- 클래스 모델, 코드에는 런타임 시점의 의존관계가 드러나지 않는다. 즉, 인터페이스에만 의존하고 있어야
한다.
- 런타임 시점의 의존관계는 컨테이너, 팩토리같은 제3의 존재가 결정
- 의존관계는 사용할 오브젝트에 대한 레퍼런스를 외부에서 제공해줌으로써 만들어진다.

IoC컨테이너를 사용하는 경우의 의존관계

![](C:\Users\Administrator\IdeaProjects\spring-study\readme_images\캡처3.PNG)

여기서는 DaoFactory가 런타임 시점의 의존관계를 결정하는 DI작업을 주도하는 오브젝트이고,
IoC 방식의 오브젝트 생성, 초기화, 제공을 수행하는 컨테이너이다.
-> DI컨테이너

의존관계 주입을 위한 코드
```java
public class UserDao {
    private ConnectionMaker connectionMaker;
    
    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
}
```

![](C:\Users\Administrator\IdeaProjects\spring-study\readme_images\캡처4.PNG)

DI는 자신이 사용할 오브젝트 선택, 생성 제어권을 외부로 넘기고 자신은 수동적으로 주입받은 오브젝트를
사용한다는 점에서 IoC 개념에 잘 들어맞는다.
스프링 컨테이너의 IoC는 주로 의존관계 주입, 또는 DI에 초점이 있다.

### 의존관계 검색과 주입

스프링 컨테이너는 자신에게 필요한 의존 오브젝트를 능동적으로 찾는다.
런타임 시 의존관계 맺을 오브젝트 결정, 생성은 외부 컨테이너에게 맡기지만
이를 가져올 때는 스스로 컨테이너에게 요청하는 방법을 사용.
이때, getBean() 메소드를 사용한다.

```java
//의존관계 검색을 이용하는 UserDao 생성자
public UserDao() {
    AnnotationConfigApplicationContext context
        = new AnnotationConfigApplicationContext(DaoFactory.class);
    this.connectionMaker = context.getBean("connectionMaker", ConnectionMaker.class);
}
```

의존관계 검색 vs 의존관계 주입

의존관계 검색은 주입의 거의 모든 장점을 갖고 있다.
코드를 보면 의존관계 주입이 더 단순하고 깔끔하다. 의존관계 검색은
코드 안에 오브젝트 팩토리 클래스나 스프링 API가 나타난다.
애플리케이션 컴포넌트가 컨테이너와 같이 성격이 다른 오브젝트에 의존하게 되므로
그다지 바람직하지 못함.

검색의 장점

IoC, DI를 사용해도 애플리케이션 기동 시점에 적어도 한번은 의존관계 검색을 통해
오브젝트를 가져와야 한다.
검색과 주입의 중요한 차이점은 검색 방식에서는 검색하는 오브젝트가 스프링의 빈일 필요가
없다는 점이다. UserDao 안에서 의존관계 검색을 적용하면 UserDao는 굳이 스프링이 관리하는
빈일 필요가 없다.

반면, 컨테이너가 UserDao에 ConnectionMaker를 주입하려면 UserDao에 대한 생성, 초기화 권한을 갖고 있어야 한다.
즉, DI를 원하는 오브젝트는 자기 자신이 컨테이너가 관리하는 빈이 되어야 한다.

주의 : DI 동작방식은 외부로터의 주입이지만, 이것만 가지고는 DI방식이라고 할 수 없다.
주입받는 메소드 파라미터가 특정 클래스 타입으로 고정돼있다면 DI가 일어나지 않는다.
즉, 다이내믹하게 구현 클래스를 결정해서 제공받을 수 있도록 인터페이스 타입의 파라미터를 통해 이뤄져야 한다.


### 의존관계 주입의 응용

DI의 장점 : 코드 상에는 런타임 클래스에 대한 의존관계가 나타나지 않고
인터페이스를 통한 낮은 결합도의 코드를 만드므로, 다른 책임을 가진 의존관계에 있는
대상이 바뀌거나 변경되어도 자신은 영향을 받지 않고, 변경을 통한 확장이 자유롭다


##### 기능 구현의 교환

가령, 서버가 사용하는 DB를 예로 들어보자.
개발 단계에서는 서버가 사용하는 DB를 사용해선 안된다. 그러므로
개발용 로컬 DB와 연결하는 ConnectionMaker, 실제 서버 DB와 연결하는 ConnectionMaker가
따로 필요할 것이다.

코드 상에 LocalDBConnectionMaker, ProductionDBConnectionMaker 등이 들어가있다면
개발하거나 혹은 서버에 배포할 때 모든 관련 코드를 수정해줘야만 한다.

DI 방식을 사용할 때는 다음과 같이 하면 된다.

```java
@Bean
public ConnectionMaker connectionMaker() {
    //로컬 DB
    return new LocalDBConnectionMaker();
    
    //실제 DB
    return new ProductionDBConnectionMaker();    
}
```


##### 부가기능 추가

만일, DAO가 DB를 얼마나 많이 연결해서 사용하는지 파악하고 싶다고 하자.
DAO getConnectionMaker()를 호출하는 부분에 카운트 코드를 넣으면 엄청난 낭비이고,
DAO를 수정하게 되므로 원래 스프링 취지와도 맞지 않다.

부가기능을 위해 새로운 카운팅 오브젝트를 추가하면 된다.

```java
public class CountingConnectionMaker implements ConnectionMaker{
    int counter = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.realConnectionMaker = connectionMaker;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.getConnection();
    }

    public int getCounter() {
        return this.counter;
    }
}
```

```java
@Configuration
public class CountingDaoFactory {
    @Bean
    public UserDao userDao() {
        return new UserDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new SimpleConnectionMaker();
    }
}
```


### 메소드를 이용한 의존관계 주입

의존관계 주입 시 반드시 생성자를 사용해야 하는 것은 아니다.

- 수정자 메소드(setter)를 이용한 주입
- 일반 메소드를 이용한 주입


# XML을 이용한 설정

DaoFactory를 이용한 DI 작업도 DI 구성이 바뀔 때마다 자바 코드를 수정하고
클래스를 다시 컴파일해야 하는 작업이 존재한다.
그래서 스프링은 XML을 이용한 DI도 지원한다.

XML은 단순 텍스트 파일이기 때문에 다루기 쉽다.
오브젝트의 관계가 바뀌는 경우에도 빠르게 변경사항을 반영할 수 있다.
스키마나 dtd를 이용해 정해진 포맷을 따라 작성됐는지 쉽게 확인 가능하다.



### XML 설정

DI 정보가 담긴 XML파일은 <beans>를 루트 엘리먼트로 사용한다.
<beans>안에는 여러 개의 <bean>을 정의할 수 있다.

하나의 @Bean 메소드를 통해 얻을 수 있는 빈의 DI 정보는 다음 세 가지이다.
- 빈의 이름
- 빈의 클래스
- 빈의 의존 오브젝트

즉, @Bean -> <bean>
빈 메소드 이름 : connectionMaker() -> id="connectionMaker"
빈 구현 클래스 DConnectionMaker() -> class="DConnectionMaker"



##### userDao() 전환

userDao는 connectionMaker에 의존하는 빈이다.
따라서, setConnectionMaker는 프로퍼티가 된다.
<property> 태그를 사용하고, name과 ref 두개의 어트리뷰트를 갖는다.
name은 프로퍼티의 이름, ref는 수정자 메소드를 통해 주입해줄 오브젝트의 빈 이름이다.
이 때, 주입해줄 오브젝트도 빈이다.