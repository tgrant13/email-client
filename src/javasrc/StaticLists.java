package javasrc;

import java.util.ArrayList;

public class StaticLists
{
  protected static ArrayList<String> services;
  protected static ArrayList<String> daysOfWeek;
  protected static ArrayList<String> dates;


  public static void fillServices()
  {
    services = new ArrayList<>();
    services.add("CiscServiceVolume"); services.add("CascServiceVolume"); services.add("WatchServiceVolume"); services.add("WatchWorkersVolume");
    services.add("FtuserServiceVolume"); services.add("LinksServiceVolume"); services.add("DiscussionsServiceVolume"); services.add("pwnServiceVolume");
    services.add("FtiServiceVolume"); services.add("UnitsServiceVolume"); services.add("DollyServiceVolume");
  }

  public static void fillDaysOfWeek()
  {
    daysOfWeek = new ArrayList<>();
    daysOfWeek.add("Sun"); daysOfWeek.add("Mon"); daysOfWeek.add("Tue"); daysOfWeek.add("Wed"); daysOfWeek.add("Thu"); daysOfWeek.add("Fri"); daysOfWeek.add("Sat");
  }

  public static void fillDates(ArrayList<FileEntryPair> finalEntries)
  {
    dates = new ArrayList<>();
    for(String day : daysOfWeek)
    {
      String dayStr = DateUtil.getSpecificDayString(finalEntries, day);
      dates.add(dayStr);
    }
  }


}
