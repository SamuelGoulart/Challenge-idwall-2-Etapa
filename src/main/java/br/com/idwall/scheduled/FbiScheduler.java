package br.com.idwall.scheduled;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.idwall.domain.FbiPerson;
import br.com.idwall.service.impl.FbiWantedPersonServiceImpl;
import br.com.idwall.util.DateAndAgeUtil;
import net.minidev.json.JSONObject;

@Component
@EnableScheduling
public class FbiScheduler {

	private FbiWantedPersonServiceImpl fbiWantedPersonServiceImpl;

	private void GetAndUpdateDatabaseOfPeopleWantedByFBI(String url) {
		try {
			Document doc = Jsoup.connect(url).get();

			Elements rows = doc.select("table.wanted-person-description tbody tr");
			Element aliases = doc.select(".wanted-person-aliases p").first();
			Elements caution = doc.select(".wanted-person-caution p");
			Elements links = doc.select(".thumbnail-container a");
	        Elements divNameWanted = doc.select("div.col-lg-12.wanted-person-wrapper");


			JSONObject jsonImagensPerson = new JSONObject();
			Map<String, String> dataMap = new HashMap<>();

			for (Element row : rows) {
				Elements cells = row.select("td");

				if (cells.size() >= 2) {
					String key = cells.get(0).text();

					String value = cells.get(1).text();

					dataMap.put(key, value);
				}
			}

			String dateOfBirth = dataMap.get("Date(s) of Birth Used");
			String placeOfBirth = dataMap.get("Place of Birth");
			String eyes = dataMap.get("Eyes");
			String sex = dataMap.get("Sex");
			String race = dataMap.get("Race");
			String occupation = dataMap.get("Occupation");

			int counter = 0;
			for (Element link : links) {
				String href = link.attr("href");
				jsonImagensPerson.put("images_" + counter, href);
				counter++;
			}

			String jsonImagemPersonString = jsonImagensPerson.toJSONString();

			StringBuilder description = new StringBuilder();

			for (Element cautionParagraph : caution) {
				description.append(cautionParagraph.text()).append("\n");
			}

			if (dateOfBirth == null || dateOfBirth.isEmpty() || placeOfBirth == null || placeOfBirth.isEmpty()
					|| eyes == null || eyes.isEmpty() || sex == null || sex.isEmpty()
					|| race == null || race.isEmpty() || occupation == null || occupation.isEmpty()
					|| aliases == null || divNameWanted.isEmpty()) {
			} else {
				String alias = aliases.text();
				String gender = sex == "Male" ? "Masculino" : "Feminino";
	            Element firstElement = divNameWanted.first();
	            String name = firstElement.selectFirst("h1").text();
	            int age = DateAndAgeUtil.calculateAge(dateOfBirth);
	            
				FbiPerson fbiPerson = new FbiPerson();
				fbiPerson.setAge(age);
				fbiPerson.setAliases(alias);
				fbiPerson.setDescription(description.toString());
				fbiPerson.setEyes(eyes);
				fbiPerson.setImages(jsonImagemPersonString);
				fbiPerson.setName(name);
				fbiPerson.setColoredPerson(race);
				fbiPerson.setSex(gender);
				fbiPerson.setOccupations(occupation);
				fbiPerson.setPlaceOfBirth(placeOfBirth);
				
				fbiWantedPersonServiceImpl.create(fbiPerson);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
	private void GetIdentifiersOfPersonsWantedByFBI() {
		String url = "https://www.fbi.gov/wanted";

		try {
			Document document = Jsoup.connect(url).get();

			Elements titleDivs = document.select("div.title");

			for (Element titleDiv : titleDivs) {
				Elements anchorTags = titleDiv.select("a");

				String secondHref = anchorTags.get(1).attr("href");
				this.GetAndUpdateDatabaseOfPeopleWantedByFBI(secondHref);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
