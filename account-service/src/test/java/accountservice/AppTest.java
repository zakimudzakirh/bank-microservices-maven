package accountservice;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testHello() {
        App a = new App();
        assertEquals("hello", a.hello());
    }
}
