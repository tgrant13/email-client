public class TimeCountEntry
{
  private String timeEntry;
  private String countEntry;

  public TimeCountEntry(String time, String count)
  {
    timeEntry = time;
    countEntry = count;
  }



  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(timeEntry + "," + countEntry + "\n");
    return sb.toString();
  }

  public String getTimeEntry()
  {
    return timeEntry;
  }

  public String getCountEntry()
  {
    return countEntry;
  }



}
