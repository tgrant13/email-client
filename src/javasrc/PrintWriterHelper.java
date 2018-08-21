package javasrc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

class PrintWriterHelper
{
  // SET UP A PRINT WRITER TO WRITE TO THE CSV DESTINATION FILE
  static PrintWriter initializePrintWriter(String writeToCSV)
  {
    FileWriter fw = null;
    try
    {
      fw = new FileWriter(writeToCSV, true);
    }
    catch (IOException e)
    {
      Popup.displayMessage(300, 300, "Invalid File: Print Writer did not open", "ERROR: PrintWriter Failure", JOptionPane.ERROR_MESSAGE, true);
    }

    assert fw != null;
    BufferedWriter bw = new BufferedWriter(fw);
    return new PrintWriter(bw);
  }

  // WRITE THE RESULTS TO THE DESTINATION FILE
  static void writeToDestinationCSV(PrintWriter pw, ArrayList<FileEntryPair> finalEntries, int currentSum)
  {
    // GET DATE STRINGS FROM CSV DATES
    StaticLists.fillDates(finalEntries);
    ArrayList<String> dates = StaticLists.dates;

    // MAKE SURE ALL DATE STRINGS WERE FOUND
    if(dates.contains("ERROR: date not found"))
    {
      pw.flush();
      pw.close();
      Popup.displayMessage(300, 300, "Missing at least one day of reports", "ERROR: Missing Date", JOptionPane.ERROR_MESSAGE, true);
    }


    // FOR EACH DATE STRING
    for(String date : dates)
    {
      currentSum = 0;
      // WRITE THE DATE STRING IN THE FIRST COLUMN
      String formattedDate = DateUtil.formatDate(date);
      pw.write(formattedDate + ",");

      // FOR EACH SERVICE (FILE NAME) WE RETRIEVED
      for(String service : StaticLists.services)
      {
        // WRITE THE INFORMATION ABOUT THAT SPECIFIC SERVICE TO THE FILE
        currentSum = writeSpecificDayInformation(pw, date, service, finalEntries, currentSum);
        pw.write(",");
      }

      String sumString = Integer.toString(currentSum);
      pw.write(sumString);
      pw.write("\n");
    }

  }

  private static int writeSpecificDayInformation(PrintWriter pw, String dayString, String service, ArrayList<FileEntryPair> finalEntries, int currentSum)
  {
    // FOR EACH FILE & DATA COMBINATION THAT WAS PARSED
    for(FileEntryPair pair : finalEntries)
    {
      // GRAB THE INFORMATION FOR THAT ENTRY
      String fileName = pair.getKey();
      TimeCountEntry timeCountInfo = pair.getValue();
      String timeString = timeCountInfo.getTimeEntry();
      String countString = timeCountInfo.getCountEntry();

      // IF THIS IS THE DATE & SERVICE WE ARE LOOKING FOR
      if(fileName.contains(service) && timeString.equals(dayString))
      {
        // WRITE IT TO THE FILE
        pw.write(countString);

        // ADD THE COUNT TO THE PORTFOLIO COUNT
        long toAdd = Integer.parseInt(countString);
        currentSum += toAdd;
        return currentSum;
      }
    }
    return currentSum;
  }
}
