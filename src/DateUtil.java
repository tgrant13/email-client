import java.util.ArrayList;

public class DateUtil
{
  public static String formatDate(String timeIn)
  {
    String[] splitTime = timeIn.split("  |\\ ");
    return splitTime[2] + "-" + splitTime[1];
  }

  public static String getSpecificDayString(ArrayList<FileEntryPair> finalEntries, String dayOfWeek)
  {
    // FOR EACH ENTRY STORED
    for(FileEntryPair pair : finalEntries)
    {
      TimeCountEntry TCE = pair.getValue();
      String timeString = TCE.getTimeEntry();

      // CHECK TO SEE IF THE DAY IS THE DATE WE ARE LOOKING FOR
      if(timeString.contains(dayOfWeek))
      {
        return timeString;
      }
    }

    // IF FOR SOME REASON THE DAY WASN'T FOUND, RETURN AN ERROR STRING
    return "ERROR: date not found";
  }
}
