import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CSVParser
{
  public ArrayList<FileEntryPair> parseCSVs(HashMap<String, String> emails)
  {
    ArrayList<FileEntryPair> finalEntries = new ArrayList<>();
    for(Map.Entry<String, String> pair : emails.entrySet())
    {
      String fileName = pair.getKey();
      String csv = pair.getValue();
      try (Scanner scanner = new Scanner(csv))
      {
        scanner.nextLine();
        while(scanner.hasNextLine())
        {
          String line = scanner.nextLine();
          String[] values = line.split(",");
          TimeCountEntry tce = new TimeCountEntry(values[0], values[1]);
          FileEntryPair fep = new FileEntryPair(fileName, tce);
          finalEntries.add(fep);
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }

    return finalEntries;
  }

}
