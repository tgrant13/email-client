import javafx.util.Pair;

public class FileEntryPair extends Pair<String, TimeCountEntry>
{
  /**
   * Creates a new pair
   *
   * @param key   The key for this pair
   * @param value The value to use for this pair
   */
  public FileEntryPair(String key, TimeCountEntry value)
  {
    super(key, value);
  }
}
