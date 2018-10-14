package android.tristan.heinig.translationfun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.tristan.heinig.translationfun.database.DateConverter;
import java.util.Date;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DateConverterTest {

  @Test
  public void convertToDateIsCorrect() {
    Long timestamp = System.currentTimeMillis();
    Date date = DateConverter.toDate(timestamp);
    assertEquals(timestamp.longValue(), date.getTime());
  }

  @Test
  public void convertToTimestampIsCorrect() {
    Date date = new Date();
    Long timestamp = DateConverter.toTimestamp(date);
    assertEquals(date.getTime(), timestamp.longValue());
  }

  @Test
  public void convertNullReturnsNull() {
    Long timestamp = DateConverter.toTimestamp(null);
    assertNull(timestamp);
    Date date = DateConverter.toDate(null);
    assertNull(date);
  }
}