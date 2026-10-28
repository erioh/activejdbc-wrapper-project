package gradleit;

import activejdbc.wrapper.ActivejdbcWrapperManipulator;
import org.javalite.activejdbc.Base;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ItemWrapper only exists if Gradle ran the processor: this class doesn't compile otherwise.
 */
public class ItemWrapperTest {

    @BeforeClass
    public static void openDatabase() {
        Base.open("org.h2.Driver", "jdbc:h2:mem:gradle;DB_CLOSE_DELAY=-1", "sa", "");
        Base.exec("CREATE TABLE ITEM (ID BIGINT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(100), PRICE DECIMAL(10, 2), "
                + "CREATED DATE, STATUS VARCHAR(10))");
    }

    @AfterClass
    public static void closeDatabase() {
        Base.close();
    }

    @Test
    public void generated_wrapper_saves_and_queries() {
        ItemWrapper item = ItemWrapper.builder()
                .name("pen")
                .price(new BigDecimal("1.50"))
                .created(LocalDate.of(2026, 10, 28))
                .status(Status.ACTIVE)
                .build();
        assertTrue(new ActivejdbcWrapperManipulator().getActivejdbcObject(item).saveIt());

        List<ItemWrapper> found = ItemWrapper.query().status().eq(Status.ACTIVE).name().like("p%").fetch();

        assertEquals(1, found.size());
        assertEquals(item, found.get(0));
        assertEquals("NAME", ItemWrapper.NAME);
    }
}
