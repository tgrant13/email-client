import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil
{
  public static String buildStackTrace(Throwable t)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    String stackTrace = sw.toString();
    return stackTrace;
  }
}
