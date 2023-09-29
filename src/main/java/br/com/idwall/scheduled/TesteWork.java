package br.com.idwall.scheduled;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.idwall.repository.InterpolWantedPersonRepository;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
public class TesteWork {

	@Autowired
	InterpolWantedPersonRepository interpolWantedPersonRepository;
	
	@Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
	public void GetAndUpdateDatabaseOfPeopleWantedByInterpol() {
		
		String url = "https://www.interpol.int/How-we-work/Notices/Red-Notices/View-Red-Notices#2023-61947";

		try {
			Document doc = Jsoup.connect(url).get();
			
			System.out.println(doc);
			

			Element infosWrapper = doc.select(".wantedsingle__infosWrapper").first();

			Element languageRow = infosWrapper.select("table tbody tr:contains(Language(s) spoken)").first();

			String languageSpokenMessage = (languageRow != null) ? languageRow.select("strong").first().text() : null;

			Elements rows = infosWrapper.select("table tbody tr");

			Map<String, String> infoMap = new HashMap<>();

			for (Element row : rows) {
				String key = row.select("td").first().text().trim();
				String value = row.select("td").get(1).text().trim();
				infoMap.put(key, value);
			}

			String familyName = infoMap.get("Family name");
			String forename = infoMap.get("Forename");
			String gender = infoMap.get("Gender");
			String dateOfBirth = infoMap.get("Date of birth");
			String placeOfBirth = infoMap.get("Place of birth");
			String nationality = infoMap.get("Nationality");

			System.out.println("Family name: " + familyName);
			System.out.println("Forename: " + forename);
			System.out.println("Gender: " + gender);
			System.out.println("Date of birth: " + dateOfBirth);
			System.out.println("Place of birth: " + placeOfBirth);
			System.out.println("Nationality: " + nationality);

			System.out.println(languageSpokenMessage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
