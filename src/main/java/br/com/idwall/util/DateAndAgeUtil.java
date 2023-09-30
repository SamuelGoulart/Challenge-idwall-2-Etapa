package br.com.idwall.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;

public class DateAndAgeUtil {
    @SuppressWarnings("deprecation")
	public static Date extractDateOfBirth(String text) {
        Pattern datePattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
        Matcher dateMatcher = datePattern.matcher(text);
        
        String dateStr = dateMatcher.find() ? dateMatcher.group(1) : null;
        
        if(dateStr == null) return null;

    	String[] dataSplit = dateStr.trim().split("/");
    	    	
        int dia = Integer.parseInt(dataSplit[0]);
        int mes = Integer.parseInt(dataSplit[1]);
        int ano = Integer.parseInt(dataSplit[2]);
                
        return new Date(ano, mes, dia);
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
        String[] parts = birthDate.split(" ");
        String englishMonth = parts[0];
        int day = Integer.parseInt(parts[1].replace(",", ""));
        int year = Integer.parseInt(parts[2]);

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
