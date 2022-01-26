package test;

import domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/junit.xml")
public class JunitTest {

    @Autowired
    ApplicationContext context;

    static Set<JunitTest> testObjects = new HashSet<JunitTest>();
    static ApplicationContext contextObject = null;

    @Test
    public void test1() {
        Assertions.assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        Assertions.assertThat(contextObject == null || contextObject == this.context)
                .isTrue();
        contextObject = context;
    }

    @Test
    public void test2() {
        Assertions.assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        Assertions.assertThat(contextObject == null || contextObject == this.context)
                .isTrue();
        contextObject = context;
    }

    @Test
    public void test3() {
        Assertions.assertThat(testObjects).doesNotContain(this);
        testObjects.add(this);

        Assertions.assertThat(contextObject == null || contextObject == this.context)
                .isTrue();
        contextObject = context;
    }

    @Test
    public void assertjContainsTest() {
        User user = new User("gyumee","박성철","springno1");
        User user_copy = new User("gyumee","박성철","springno1");

        List<User> userList = new ArrayList<>();

        userList.add(user);

        Assertions.assertThat(userList).doesNotContain(user_copy);
    }
}
