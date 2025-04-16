package hu.progressus.util;


/**
 * Utility class for formatting strings.
 */

public class StringFormatter {

  /**
   * Formats a string by capitalizing the first letter of each word and converting the rest to lowercase.
   *
   * @param input the input string to format
   * @return the formatted string
   */
  public static String formatString(String input) {
    String[] words = input.split("\\s+");
    StringBuilder result = new StringBuilder();

    for (String word : words) {
      if (word.length() > 0) {
        result.append(Character.toUpperCase(word.charAt(0)))
            .append(word.substring(1).toLowerCase())
            .append(" ");
      }
    }

    return result.toString().trim();
  }
}
