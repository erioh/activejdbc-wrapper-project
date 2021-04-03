import org.junit.Test;

public class SimpleTests {
    @Test
    public void test() {
        String rhyme = "Humpty Dumpty sat <%s> on a wall.%nHumpty Dumpty had a great fall.";
        System.out.println(String.format(rhyme, "SOME_STRING"));
    }
}
