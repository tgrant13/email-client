package javasrc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;


public class EmailClient
{
  // ====================================================================================
  // ================================== MAIN FUNCTION ===================================
  // ====================================================================================

  public static void main(String[] args)
  {
    StaticLists.fillServices();
    StaticLists.fillDaysOfWeek();
    GUIFrame.createView();
  }

  // ====================================================================================
  // ================================= CLICK LISTENER ===================================
  // ====================================================================================

  static void doTheClick()
  {
    // GRAB TEXT FIELD VALUES
    String email = GUIFrame.getEmailTextField().getText();
    char[] passwordArray = GUIFrame.getPasswordTextField().getPassword();
    String password = String.valueOf(passwordArray);
    String writeToCSV = GUIFrame.getDestinationTextField().getText();

    // ENSURE THAT THE DESTINATION TEXT FIELD IS A CSV FILE
    if(!writeToCSV.endsWith(".csv"))
      javasrc.Popup.displayMessage(500, 50, "Destination File must be a .csv file", "ERROR: Invalid Destination File", JOptionPane.ERROR_MESSAGE, true);

    // RETRIEVE THE EMAILS FROM OUTLOOK USING THE USERNAME AND PASSWORD
    OutlookReader outlookReader = new OutlookReader(email, password);
    HashMap<String, String> emails = outlookReader.retrieveEmails();

    // ENSURE THAT ALL OF THE SERVICES WERE IMPORTED
    ArrayList<String> missingServices = new ArrayList<>();
    if(emails.size() != StaticLists.services.size())
      MissingServicesUtil.check(emails, missingServices);

    // PARSE THE EMAILS INTO USABLE CSV FILES
    CSVParser parser = new CSVParser();
    ArrayList<FileEntryPair> finalEntries = parser.parseCSVs(emails);

    // OPEN UP A WRITER TO WRITE TO THE FILE
    PrintWriter pw = PrintWriterHelper.initializePrintWriter(writeToCSV);

    // WRITE THE RESULTS TO THE OUTPUT FILE
    int currentSum = 0;
    PrintWriterHelper.writeToDestinationCSV(pw, finalEntries, currentSum);
    pw.flush(); pw.close();

    // EXIT
    if(missingServices.size() == 0)
      javasrc.Popup.displayMessage(500, 50, "Success! All reports imported", "Task Completed", JOptionPane.INFORMATION_MESSAGE, true);
    else
      Popup.displayMessage(500, 100, "Finished importing. Reports for some services could not be located.", "Check Final Report", JOptionPane.INFORMATION_MESSAGE, true);
  }
}
