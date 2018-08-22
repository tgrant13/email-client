package javasrc;

import java.util.ArrayList;

class StaticLists
{
  static ArrayList<String> services;
  private static ArrayList<String> daysOfWeek;
  static ArrayList<String> dates;


  static void fillServices()
  {
    services = new ArrayList<>();
    services.add("CiscServiceVolume"); services.add("CascServiceVolume"); services.add("WatchServiceVolume"); services.add("WatchWorkersVolume");
    services.add("FtuserServiceVolume"); services.add("LinksServiceVolume"); services.add("DiscussionsServiceVolume"); services.add("pwnServiceVolume");
    services.add("FtiServiceVolume"); services.add("UnitsServiceVolume"); services.add("DollyServiceVolume");
  }

  static void fillDaysOfWeek()
  {
    daysOfWeek = new ArrayList<>();
    daysOfWeek.add("Sun"); daysOfWeek.add("Mon"); daysOfWeek.add("Tue"); daysOfWeek.add("Wed"); daysOfWeek.add("Thu"); daysOfWeek.add("Fri"); daysOfWeek.add("Sat");
  }

  static void fillDates(ArrayList<FileEntryPair> finalEntries)
  {
    dates = new ArrayList<>();
    for(String day : daysOfWeek)
    {
      String dayStr = DateUtil.getSpecificDayString(finalEntries, day);
      dates.add(dayStr);
    }
  }


}
