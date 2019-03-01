/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
  @Test
  public void test() {
    String[] args = {"-u", "10"};
    var parser = new ArgParser(args);
    var app = new App(parser);
    app.run();
    assertEquals(app.primeNumbers.size(), 4);
  }
}