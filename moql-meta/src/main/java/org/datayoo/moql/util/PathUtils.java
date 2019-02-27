/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.util;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tang Tadin
 *
 */
public abstract class PathUtils {

  public static final String FOLDER_SEPARATOR = "/";
  public static final String WINDOWS_FOLDER_SEPARATOR = "\\";
  public static final String TOP_PATH = "..";
  public static final String CURRENT_PATH = ".";
  public static final char DOT_CHAR = '.';
  public static final char FOLDER_SEPARATOR_CHAR = '/';

  public static int lastIndexOfFolderSeparator(String path) {
    int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
    if (separatorIndex == -1) {
      return path.lastIndexOf(WINDOWS_FOLDER_SEPARATOR);
    }
    return separatorIndex;
  }

  /**
   * Extract the filename from the given path, e.g. "mypath/myfile.txt" -&gt;
   * "myfile.txt".
   *
   * @param path  the file path
   * @return the extracted filename, or <code>null</code> if none
   */
  public static String getFileName(String path) {
    if (path == null) {
      return null;
    }
    int separatorIndex = lastIndexOfFolderSeparator(path);
    return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
  }

  /**
   * Extract the path from the given path, e.g. "mypath/myfile.txt" -&gt;
   * "mypath/".
   *
   * @param path  the file path
   * @return the extracted file's path, or <code>null</code> if none
   */
  public static String getFilePath(String path) {
    if (path == null) {
      return null;
    }
    int separatorIndex = lastIndexOfFolderSeparator(path);
    return (separatorIndex != -1 ?
        path.substring(0, separatorIndex + 1) :
        path);
  }

  /**
   * Extract the filename extension from the given path, e.g.
   * "mypath/myfile.txt" -&gt; "txt".
   *
   * @param path  the file path
   * @return the extracted filename extension, or <code>null</code> if none
   */
  public static String getFileNameExtension(String path) {
    if (path == null) {
      return null;
    }
    int separatorIndex = path.lastIndexOf(DOT_CHAR);
    return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : null);
  }

  /**
   * Join the given relative path to the given path, assuming standard Java
   * folder separation (i.e. "/" separators);
   *
   * @param path  the path to start from (usually a full file path)
   * @param relativePath  the relative path to apply
   * @return the full file path that results from applying the relative path
   */
  public static String joinPath(String path, String relativePath) {
    int separatorIndex = lastIndexOfFolderSeparator(path);
    if (separatorIndex != -1) {
      String newPath = path.substring(0, separatorIndex);
      if (!(relativePath.charAt(0) == FOLDER_SEPARATOR_CHAR)) {
        newPath += FOLDER_SEPARATOR;
      }
      return newPath + relativePath;
    } else {
      return relativePath;
    }
  }

  /**
   * Normalize the path by removing the ".." and "." from path.
   * <p>
   * The result is convenient for path comparison. For other uses, notice that
   * Windows separators ("\") are replaced by simple slashes.
   *
   * @param path  the original path
   * @return the normalized path
   */
  public static String toCanonicalPath(String path) {
    if (path == null) {
      return null;
    }
    String pathToUse = StringUtils
        .replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		/* Strip prefix from path to analyze, to not treat it as part of the
	 	first path element. This is necessary to correctly parse paths like
		"file:root/../root/util/utils.xml", where the ".." should just
		strip the first "root" directory while keeping the "file:" prefix.*/
    int prefixIndex = pathToUse.indexOf(":");
    String prefix = "";
    if (prefixIndex != -1) {
      prefix = pathToUse.substring(0, prefixIndex + 1);
      pathToUse = pathToUse.substring(prefixIndex + 1);
    }
    if (pathToUse.charAt(0) == FOLDER_SEPARATOR_CHAR) {
      if (pathToUse.charAt(1) == FOLDER_SEPARATOR_CHAR) {
        prefix = new StringBuffer(prefix).append(FOLDER_SEPARATOR_CHAR).
            append(FOLDER_SEPARATOR_CHAR).toString();
        pathToUse = pathToUse.substring(2);
      } else {
        prefix = prefix + FOLDER_SEPARATOR;
        pathToUse = pathToUse.substring(1);
      }
    }

    String[] pathArray = StringUtils.split(pathToUse, FOLDER_SEPARATOR);
    List<String> pathElements = new LinkedList<String>();
    int tops = 0;

    for (int i = pathArray.length - 1; i >= 0; i--) {
      String element = pathArray[i];
      if (CURRENT_PATH.equals(element)) {
        // Points to current directory - drop it.
      } else if (TOP_PATH.equals(element)) {
        // Registering top path found.
        tops++;
      } else {
        if (tops > 0) {
          // Merging path element with element corresponding to top
          // path.
          tops--;
        } else {
          // Normal path element found.
          pathElements.add(0, element);
        }
      }
    }

    // Remaining top paths need to be retained.
    for (int i = 0; i < tops; i++) {
      pathElements.add(0, TOP_PATH);
    }
    pathElements.add(0, prefix);
    return StringUtils.join(pathElements, FOLDER_SEPARATOR);
  }

  /**
   * Return whether the path is a relative path which start
   * with "." or "..".
   * @param path  the given path
   * @return the relative path
   */
  public static boolean isRelativePath(String path) {
    if (path.charAt(0) == DOT_CHAR)
      return true;
    return false;
  }

}
