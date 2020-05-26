/* Copyright Prolog                                  */
/*                                                   */
/* RULP(Run a Lisp Processer) on Java                */
/* 													 */
/* Copyright (C) 2020 Todd (to0d@outlook.com)        */
/* This program comes with ABSOLUTELY NO WARRANTY;   */
/* This is free software, and you are welcome to     */
/* redistribute it under certain conditions.         */

package alpha.rulp.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	public static boolean isExistFile(String path) {

		File file = new File(path);
		return file.exists() && file.isFile();
	}

	public static List<String> openTxtFile(String fileName) throws IOException {

		ArrayList<String> lineList = new ArrayList<>();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}

	public static List<String> openTxtFile(String fileName, String charset) throws IOException {

		if (charset == null) {
			return openTxtFile(fileName);
		}

		ArrayList<String> lineList = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}

	public static ArrayList<String> openTxtFileFromJar(String path, String charset) throws IOException {

		ArrayList<String> lineList = new ArrayList<String>();

		try (InputStream is = RulpUtility.class.getClassLoader().getResourceAsStream(path);
				BufferedReader in = charset == null ? new BufferedReader(new InputStreamReader(is))
						: new BufferedReader(new InputStreamReader(is, charset))) {

			String line = null;
			while ((line = in.readLine()) != null) {
				lineList.add(line);
			}
		}

		return lineList;
	}
}
