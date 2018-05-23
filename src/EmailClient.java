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
import javax.swing.plaf.FontUIResource;

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

  private static ArrayList<String> services;
  private static ArrayList<String> daysOfWeek;

  // ====================================================================================
  // ======================= ARRAY INITIALIZING FUNCTIONS ===============================
  // ====================================================================================

  private static void fillServices()
  {
    services = new ArrayList<>();
    services.add("CiscServiceVolume"); services.add("CascServiceVolume"); services.add("WatchServiceVolume"); services.add("WatchWorkersVolume");
    services.add("FtuserServiceVolume"); services.add("LinksServiceVolume"); services.add("DiscussionsServiceVolume"); services.add("pwnServiceVolume");
    services.add("FtiServiceVolume"); services.add("UnitsServiceVolume"); services.add("DollyServiceVolume");
  }

  private static void fillDaysOfWeek()
  {
    daysOfWeek = new ArrayList<>();
    daysOfWeek.add("Sun"); daysOfWeek.add("Mon"); daysOfWeek.add("Tue"); daysOfWeek.add("Wed"); daysOfWeek.add("Thu"); daysOfWeek.add("Fri"); daysOfWeek.add("Sat");
  }

  private static ArrayList<String> fillDates()
  {
      ArrayList<String> dates = new ArrayList<>();
      for(String day : daysOfWeek)
      {
        dates.add(getSpecificDayString(day));
      }
      return dates;
  }

  // ====================================================================================
  // ================================== MAIN FUNCTION ===================================
  // ====================================================================================

  public static void main(String[] args)
  {
    fillServices();
    fillDaysOfWeek();
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
      displayMessage(500, 50, "Destination File must be a .csv file", "ERROR: Invalid Destination File", JOptionPane.ERROR_MESSAGE, true);

    // RETRIEVE THE EMAILS FROM OUTLOOK USING THE USERNAME AND PASSWORD
    OutlookReader outlookReader = new OutlookReader(email, password);
    emails = outlookReader.retrieveEmails();

    // ENSURE THAT ALL OF THE SERVICES WERE IMPORTED
    missingServices = new ArrayList<String>();
    if(emails.size() != services.size())
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
      displayMessage(500, 50, "Success! All reports imported", "Task Completed", JOptionPane.INFORMATION_MESSAGE, true);
    else
      displayMessage(500, 100, "Finished importing. Reports for some services could not be located.", "Check Final Report", JOptionPane.INFORMATION_MESSAGE, true);
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
      displayMessage(300, 300, "Invalid File: Print Writer did not open", "ERROR: PrintWriter Failure", JOptionPane.ERROR_MESSAGE, true);
    }

    BufferedWriter bw = new BufferedWriter(fw);
    return new PrintWriter(bw);
  }

  // WRITE THE RESULTS TO THE DESTINATION FILE
  private static void writeToDestinationCSV(PrintWriter pw)
  {
    // GET DATE STRINGS FROM CSV DATES
    ArrayList<String> dates = fillDates();

    // MAKE SURE ALL DATE STRINGS WERE FOUND
    if(dates.contains("ERROR: date not found"))
    {
      pw.flush();
      pw.close();
      displayMessage(300, 300, "Missing at least one day of reports", "ERROR: Missing Date", JOptionPane.ERROR_MESSAGE, true);
    }


    // FOR EACH DATE STRING
    for(String date : dates)
    {
      currentSum = 0;
      // WRITE THE DATE STRING IN THE FIRST COLUMN
      String formattedDate = formatDate(date);
      pw.write(formattedDate + ",");

      // FOR EACH SERVICE (FILE NAME) WE RETRIEVED
      for(String service : services)
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

    for(String service : services)
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
    displayMessage(600, 600, missing.toString(), "Missing Service", JOptionPane.ERROR_MESSAGE, false);
  }

  private static String formatDate(String timeIn)
  {
    String[] splitTime = timeIn.split("  |\\ ");
    return splitTime[2] + "-" + splitTime[1];
  }

  private static String getSpecificDayString(String dayOfWeek)
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
    emailTextField.setText("tylergrant13@ldschurch.org");
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
    destinationTextField.setText("C:\\Users\\tylergrant13\\Desktop\\serviceVolume.csv");
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

  private static void displayMessage(int width, int height, String message, String title, int messageType, boolean exit)
  {
    JTextPane jtp = new JTextPane();

    jtp.setPreferredSize(new Dimension(width, height));
    jtp.setFont(new Font("SansSerif", Font.BOLD, 25));
    jtp.setText(message);

    UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 35)));
    JOptionPane.showMessageDialog(null, jtp, title, messageType);
    if(exit) System.exit(0);
  }
}
