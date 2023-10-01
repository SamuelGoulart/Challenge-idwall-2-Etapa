package br.com.idwall.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

public class DateAndAgeUtil {
	public static String extractDateOfBirth(String text) {
		Pattern datePattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
		Matcher dateMatcher = datePattern.matcher(text);

		if (dateMatcher.find()) {
			String dateStr = dateMatcher.group(1);
			SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = inputFormat.parse(dateStr);
				return outputFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static int extractAge(String text) {
		Pattern agePattern = Pattern.compile("(\\d+) years old");
		Matcher ageMatcher = agePattern.matcher(text);

		if (ageMatcher.find()) {
			return Integer.parseInt(ageMatcher.group(1));
		}

		return 0;
	}

	public static int calculateAge(String birthDate) {
		if (birthDate == null)
			return 0;

		String[] dateParts = birthDate.split(",");

		if (dateParts.length < 2) {
			return -1;
		}

		String[] monthAndYear = dateParts[0].trim().split(" ");
		if (monthAndYear.length < 2) {
			return -1;
		}

		String englishMonth = monthAndYear[0].trim();
		int day = Integer.parseInt(monthAndYear[1].trim());
		int year = Integer.parseInt(dateParts[1].trim());

		String uppercasedMonth = englishMonth.toUpperCase();

		Month month = null;
		for (Month m : Month.values()) {
			if (uppercasedMonth.equals(m.toString())) {
				month = m;
				break;
			}
		}

		if (month != null) {
			LocalDate birthDateObj = LocalDate.of(year, month, day);

			LocalDate currentDate = LocalDate.now();

			Period period = Period.between(birthDateObj, currentDate);
			return period.getYears();
		} else {
			return -1;
		}
	}

}
