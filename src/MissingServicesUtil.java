import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

public class MissingServicesUtil
{
  public static void check(HashMap<String, String> emails, ArrayList<String> missingServices)
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
}
