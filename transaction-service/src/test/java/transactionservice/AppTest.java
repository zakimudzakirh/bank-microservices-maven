package transactionservice;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testSum() {
        App a = new App();
        assertEquals(5, a.sum(2,3));
    }
}
