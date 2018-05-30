import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

import javafx.util.Pair;

public class EmailClient
{
  private static JPanel panel;
  private static JButton goButton;
  private static JFrame frame;

  private static JTextField emailTextField;
  private static JPasswordField passwordTextField;
  private static JTextField destinationTextField;

  private static Font font;

  private static HashMap<String, String> emails;
  private static ArrayList<FileEntryPair> finalEntries;
  private static ArrayList<String> missingServices;
  private static String writeToCSV;
  private static int currentSum = 0;

  // ====================================================================================
  // ======================= ARRAY INITIALIZING FUNCTIONS ===============================
  // ====================================================================================



  // ====================================================================================
  // ================================== MAIN FUNCTION ===================================
  // ====================================================================================

  public static void main(String[] args)
  {
    StaticLists.fillServices();
    StaticLists.fillDaysOfWeek();
    createView();
  }

  // ====================================================================================
  // ================================= CLICK LISTENER ===================================
  // ====================================================================================

  private static void doTheClick()
  {
    // GRAB TEXT FIELD VALUES
    String email = emailTextField.getText();
    char[] passwordArray = passwordTextField.getPassword();
    String password = String.valueOf(passwordArray);
    writeToCSV = destinationTextField.getText();

    // ENSURE THAT THE DESTINATION TEXT FIELD IS A CSV FILE
    if(!writeToCSV.endsWith(".csv"))
      Popup.displayMessage(500, 50, "Destination File must be a .csv file", "ERROR: Invalid Destination File", JOptionPane.ERROR_MESSAGE, true);

    // RETRIEVE THE EMAILS FROM OUTLOOK USING THE USERNAME AND PASSWORD
    OutlookReader outlookReader = new OutlookReader(email, password);
    emails = outlookReader.retrieveEmails();

    // ENSURE THAT ALL OF THE SERVICES WERE IMPORTED
    missingServices = new ArrayList<>();
    if(emails.size() != StaticLists.services.size())
      checkMissingServices();

    // PARSE THE EMAILS INTO USABLE CSV FILES
    CSVParser parser = new CSVParser();
    finalEntries = parser.parseCSVs(emails);

    // OPEN UP A WRITER TO WRITE TO THE FILE
    PrintWriter pw = initializePrintWriter();

    // WRITE THE RESULTS TO THE OUTPUT FILE
    writeToDestinationCSV(pw);

    // FLUSH AND CLOSE THE WRITER
    pw.flush();
    pw.close();

    // EXIT
    if(missingServices.size() == 0)
      Popup.displayMessage(500, 50, "Success! All reports imported", "Task Completed", JOptionPane.INFORMATION_MESSAGE, true);
    else
      Popup.displayMessage(500, 100, "Finished importing. Reports for some services could not be located.", "Check Final Report", JOptionPane.INFORMATION_MESSAGE, true);
  }



  // ====================================================================================
  // =============================== WRITER FUNCTIONS ===================================
  // ====================================================================================

  // SET UP A PRINT WRITER TO WRITE TO THE CSV DESTINATION FILE
  private static PrintWriter initializePrintWriter()
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

    BufferedWriter bw = new BufferedWriter(fw);
    return new PrintWriter(bw);
  }

  // WRITE THE RESULTS TO THE DESTINATION FILE
  private static void writeToDestinationCSV(PrintWriter pw)
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
        writeSpecificDayInformation(pw, date, service);
        pw.write(",");
      }

      String sumString = Integer.toString(currentSum);
      pw.write(sumString);
      pw.write("\n");
    }

  }

  private static void writeSpecificDayInformation(PrintWriter pw, String dayString, String service)
  {
    // FOR EACH FILE & DATA COMBINATION THAT WAS PARSED
    for(Pair<String, TimeCountEntry> pair : finalEntries)
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
        return;
      }
    }
  }

  // ====================================================================================
  // ============================= HELPER FUNCTIONS =====================================
  // ====================================================================================

  private static void checkMissingServices()
  {
    // FOR EACH SERVICE WE ARE LOOKING FOR
    StringBuilder missing = new StringBuilder();
    missing.append("OutlookReader did not locate emails for the following services: \n\n");

    for(String service : StaticLists.services)
    {
      // CHECK TO SEE THAT THERE IS A FILE THAT REPRESENTS THAT SERVICE
      Boolean serviceParsed = false;
      for(String fileName : emails.keySet())
      {
        if(fileName.contains(service))
        {
          serviceParsed = true;
          break;
        }
      }

      // IF IT'S NOT FOUND, DISPLAY
      if(!serviceParsed)
      {
        missing.append(service);
        missing.append("\n");
        missingServices.add(service);
      }
    }
    Popup.displayMessage(600, 600, missing.toString(), "Missing Service", JOptionPane.ERROR_MESSAGE, false);
  }





  // ====================================================================================
  // ================================== GUI FUNCTIONS ===================================
  // ====================================================================================

  private static void createView()
  {
    frame = new JFrame();
    createPanel();
    createEmailFields();
    createPasswordFields();
    createDestinationFields();
    createButtons();
    setFrameSettings();
  }

  private static void createPanel()
  {
    panel = new JPanel();
    panel.setBackground(Color.BLACK);
    frame.getContentPane().add(panel);
  }

  private static void createEmailFields()
  {
    font = new Font("SansSerif", Font.BOLD, 25);
    JLabel emailLabel = new JLabel();
    emailLabel.setText("Email Address");
    emailLabel.setForeground(Color.WHITE);
    emailLabel.setFont(font);
    panel.add(emailLabel);

    emailTextField = new JTextField();
    emailTextField.setPreferredSize(new Dimension(630, 60));
    emailTextField.setFont(font);
    panel.add(emailTextField);
  }

  private static void createPasswordFields()
  {
    JLabel passwordLabel = new JLabel();
    passwordLabel.setText("Password        ");
    passwordLabel.setForeground(Color.WHITE);
    passwordLabel.setFont(font);
    panel.add(passwordLabel);

    passwordTextField = new JPasswordField();
    passwordTextField.setPreferredSize(new Dimension(630, 60));
    passwordTextField.setFont(font);
    panel.add(passwordTextField);
  }

  private static void createDestinationFields()
  {
    JLabel destinationLabel = new JLabel();
    destinationLabel.setText("Destination CSV  ");
    destinationLabel.setForeground(Color.WHITE);
    destinationLabel.setFont(font);
    panel.add(destinationLabel);

    destinationTextField = new JTextField();
    destinationTextField.setPreferredSize(new Dimension(600, 60));

    destinationTextField.setFont(font);
    panel.add(destinationTextField);
  }

  private static void createButtons()
  {
    goButton = new JButton("GO");
    goButton.setPreferredSize(new Dimension(80, 60));
    goButton.setFont(font);
    goButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e)
      {
        doTheClick();
        frame.dispose();
      }
    });
    panel.add(goButton);
  }

  private static void setFrameSettings()
  {
    frame.setTitle("CSV Email Client");
    frame.setPreferredSize(new Dimension(830, 300));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);
    frame.getRootPane().setDefaultButton(goButton);
  }
}
