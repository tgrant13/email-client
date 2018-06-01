import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;


public class EmailClient
{
  private static HashMap<String, String> emails;
  private static ArrayList<FileEntryPair> finalEntries;
  private static ArrayList<String> missingServices;
  private static String writeToCSV;
  private static int currentSum = 0;
  private static GUIFrame GUI;
  private static PrintWriterHelper PWHelper;


  // ====================================================================================
  // ================================== MAIN FUNCTION ===================================
  // ====================================================================================

  public static void main(String[] args)
  {
    GUI = new GUIFrame();
    PWHelper = new PrintWriterHelper();
    StaticLists.fillServices();
    StaticLists.fillDaysOfWeek();
    GUI.createView();
  }

  // ====================================================================================
  // ================================= CLICK LISTENER ===================================
  // ====================================================================================

  public static void doTheClick()
  {
    // GRAB TEXT FIELD VALUES
    String email = GUI.getEmailTextField().getText();
    char[] passwordArray = GUI.getPasswordTextField().getPassword();
    String password = String.valueOf(passwordArray);
    writeToCSV = GUI.getDestinationTextField().getText();

    // ENSURE THAT THE DESTINATION TEXT FIELD IS A CSV FILE
    if(!writeToCSV.endsWith(".csv"))
      Popup.displayMessage(500, 50, "Destination File must be a .csv file", "ERROR: Invalid Destination File", JOptionPane.ERROR_MESSAGE, true);

    // RETRIEVE THE EMAILS FROM OUTLOOK USING THE USERNAME AND PASSWORD
    OutlookReader outlookReader = new OutlookReader(email, password);
    emails = outlookReader.retrieveEmails();

    // ENSURE THAT ALL OF THE SERVICES WERE IMPORTED
    missingServices = new ArrayList<>();
    if(emails.size() != StaticLists.services.size())
      MissingServicesUtil.check(emails, missingServices);

    // PARSE THE EMAILS INTO USABLE CSV FILES
    CSVParser parser = new CSVParser();
    finalEntries = parser.parseCSVs(emails);

    // OPEN UP A WRITER TO WRITE TO THE FILE
    PrintWriter pw = PWHelper.initializePrintWriter(writeToCSV);

    // WRITE THE RESULTS TO THE OUTPUT FILE
    PWHelper.writeToDestinationCSV(pw, finalEntries, currentSum);

    // FLUSH AND CLOSE THE WRITER
    pw.flush();
    pw.close();

    // EXIT
    if(missingServices.size() == 0)
      Popup.displayMessage(500, 50, "Success! All reports imported", "Task Completed", JOptionPane.INFORMATION_MESSAGE, true);
    else
      Popup.displayMessage(500, 100, "Finished importing. Reports for some services could not be located.", "Check Final Report", JOptionPane.INFORMATION_MESSAGE, true);
  }
}
