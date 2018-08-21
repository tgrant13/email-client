package javasrc;

import javafx.util.Pair;

class FileEntryPair extends Pair<String, TimeCountEntry>
{
  /**
   * Creates a new pair
   *
   * @param key   The key for this pair
   * @param value The value to use for this pair
   */
  FileEntryPair(String key, TimeCountEntry value)
  {
    super(key, value);
  }
}
