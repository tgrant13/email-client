package javasrc;

import java.io.PrintWriter;
import java.io.StringWriter;

class StackTraceUtil
{
  static String buildStackTrace(Throwable t)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
}
