package javasrc;

import java.util.ArrayList;

class DateUtil
{
  static String formatDate(String timeIn)
  {
    String[] splitTime = timeIn.split(" {2}| ");
    return splitTime[2] + "-" + splitTime[1];
  }

  static String getSpecificDayString(ArrayList<FileEntryPair> finalEntries, String dayOfWeek)
  {
    // FOR EACH ENTRY STORED
    for(FileEntryPair pair : finalEntries)
    {
      TimeCountEntry timeCountEntry = pair.getValue();
      String timeString = timeCountEntry.getTimeEntry();

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
