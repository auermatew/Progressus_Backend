package hu.progressus.util;

public class StringFormatter {

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
