package br.com.idwall.scheduled;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.idwall.domain.InterpolPerson;
import br.com.idwall.service.impl.InterpolWantedPersonServiceImpl;

import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
@EnableScheduling
public class InterpolScheduler {

	@Autowired
	private InterpolWantedPersonServiceImpl interpolWantedPersonServiceImpl;

	@Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
	public void GetAndUpdateDatabaseOfPeopleWantedByInterpol() {

		String url = "https://www.interpol.int/How-we-work/Notices/Red-Notices/View-Red-Notices#2023-61947";

		try {
			Document doc = Jsoup.connect(url).get();

			Element infosWrapper = doc.select(".wantedsingle__infosWrapper").first();

			Element languageRow = infosWrapper.select("table tbody tr:contains(Language(s) spoken)").first();
	        Element wrapperImg = doc.select(".redNoticeLargePhoto__wrapperImg").first();
	        Element imgTag = wrapperImg.select("img").first();

			Elements rows = infosWrapper.select("table tbody tr");

			Map<String, String> infoMap = new HashMap<>();

			for (Element row : rows) {
				String key = row.select("td").first().text().trim();
				String value = row.select("td").get(1).text().trim();
				infoMap.put(key, value);
			}

			String forename = infoMap.get("Forename");
			String gender = infoMap.get("Gender");
			String dateOfBirth = infoMap.get("Date of birth");
			String placeOfBirth = infoMap.get("Place of birth");
			String nationality = infoMap.get("Nationality");
			

			System.out.println("Forename: " + forename);
			System.out.println("Gender: " + gender);
			System.out.println("Date of birth: " + dateOfBirth);
			System.out.println("Place of birth: " + placeOfBirth);
			System.out.println("Nationality: " + nationality);


			if (imgTag != null && languageRow != null && forename != null && gender != null && dateOfBirth != null && placeOfBirth != null
					&& nationality != null) {

				String sex = gender == "Male" ? "Masculino" : "Feminino";
				
	            String imageUrl = imgTag.attr("src");

				Pattern pattern = Pattern.compile("(\\d{2}/\\d{2}/\\d{4}) \\((\\d+) years old\\)");
				Matcher matcher = pattern.matcher(dateOfBirth);

				if (!matcher.find())
					throw new Error("ERROR");
				
				String dateOfBirthString = matcher.group(1);
				int age = Integer.parseInt(matcher.group(2));

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date dateOfBirthFormat = dateFormat.parse(dateOfBirthString);
				
				String languages = languageRow.select("strong").first().text();

				InterpolPerson interpolPerson = new InterpolPerson();
				interpolPerson.setName(forename);
				interpolPerson.setSex(sex);
				interpolPerson.setAge(age);
				interpolPerson.setDateOfBirth(dateOfBirthFormat);
				interpolPerson.setPlaceOfBirth(placeOfBirth);
				interpolPerson.setNationality(nationality);
				interpolPerson.setLanguages(languages);
				interpolPerson.setImageUrl(imageUrl);

				interpolWantedPersonServiceImpl.create(interpolPerson);

			} else {
				System.out.println("Pelo menos um dos campos é nulo.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
