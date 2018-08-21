package javasrc;

public class TimeCountEntry
{
  private String timeEntry;
  private String countEntry;

  TimeCountEntry(String time, String count)
  {
    timeEntry = time;
    countEntry = count;
  }



  @Override
  public String toString()
  {
    return timeEntry + "," + countEntry + "\n";
  }

  String getTimeEntry()
  {
    return timeEntry;
  }

  String getCountEntry()
  {
    return countEntry;
  }



}
