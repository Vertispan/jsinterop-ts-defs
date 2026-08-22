/*
 * Copyright © 2023 Vertispan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vertispan.tsdefs.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

public class Formatting {

  public static final String INDENT = "\t";
  public static final String NONE = "";

  public static final String NEW_LINE = "\n";
  public static final String CR = "\r";
  public static final String SPACE = " ";
  public static final String END_LINE = ";";
  public static final String COMMA = ", ";

  /**
   * @param list
   * @return a new line character if the list is empty or else return empty string
   */
  public static String optionalln(Collection<?> list) {
    return optionalAffix(NEW_LINE, list);
  }

  /**
   * @param affix
   * @param list
   * @return the affix if the collection is empty otherwise return empty string.
   */
  public static String optionalAffix(String affix, Collection<?> list) {
    return list.size() > 0 ? affix : NONE;
  }

  /**
   * Capitalize the first letter of a {@link String}
   *
   * @param input the string
   * @return The new string with capital first letter
   */
  public static String capitalizeFirstLetter(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }

  /**
   * Lowercase the first letter of a {@link String}
   *
   * @param input the string
   * @return The new string with lowercase first letter
   */
  public static String lowerFirstLetter(String input) {
    if (input.length() > 1) {
      return input.substring(0, 1).toLowerCase() + input.substring(1);
    } else {
      return input.toLowerCase();
    }
  }

  /**
   * Prints an exception stack trace as a string
   *
   * @param e
   * @return the exception stack trace string
   */
  public static String formatException(Exception e) {
    StringWriter out = new StringWriter();
    e.printStackTrace(new PrintWriter(out));
    return out.getBuffer().toString();
  }

  /**
   * @param originalName
   * @return the name of the method with the get,set and is prefixes and lowercase the first letter.
   */
  public static String nonGetSetName(String originalName) {
    if (originalName.startsWith("get")
        || originalName.startsWith("set") && originalName.length() > 3) {
      return Formatting.lowerFirstLetter(originalName.substring(3));
    }
    if (originalName.startsWith("is") && originalName.length() > 2) {
      return Formatting.lowerFirstLetter(originalName.substring(2));
    }
    return originalName;
  }

  /**
   * @param original
   * @param alternative
   * @return a special name replacement of the original name or if the original name does not match
   *     any special name returns the alternative name.
   */
  public static String resolveNameOr(String original, String alternative) {
    return Arrays.stream(SpecialNames.values())
        .filter(specialNames -> specialNames.specialName.equals(original))
        .findFirst()
        .map(specialNames -> specialNames.fix(original))
        .orElse(alternative);
  }

  /**
   * @param original
   * @return a special name replacement of the original name or if the original name does not match
   *     any special name returns the original name.
   */
  public static String resolveName(String original) {
    return resolveNameOr(original, original);
  }

  public enum SpecialNames {
    function("function", s -> "func"),
    star("*", s -> "any"),
    questionMark("?", s -> "any");

    private String specialName;
    private NameFix keywordFix;

    SpecialNames(String specialName, NameFix nameFix) {
      this.specialName = specialName;
      this.keywordFix = nameFix;
    }

    public String fix(String original) {
      return keywordFix.apply(original);
    }

    public interface NameFix {
      String apply(String original);
    }
  }
}
