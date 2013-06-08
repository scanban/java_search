package examples.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class CashAccountRowTest {

    @Test
    public void accessorTests() {
        CashAccountRow c = new CashAccountRow();
        assertEquals(0, c.getCode());
        assertEquals(0, c.getGender());

        c.setCode(1000000);
        assertEquals(1000000, c.getCode());

        c.setAge(30);
        c.setAmount(100001);
        c.setCode(100);
        c.setGender(1);
        c.setHeight(299);

        assertEquals(299, c.getHeight());
        assertEquals(1, c.getGender());
        assertEquals(100, c.getCode());
        assertEquals(100001, c.getAmount());
        assertEquals(30, c.getAge());
    }

}
