package br.com.idwall.scheduled;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private FbiWantedPersonServiceImpl fbiWantedPersonServiceImpl;

	private void GetAndUpdateDatabaseOfPeopleWantedByFBI(String url) {
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.setJavaScriptTimeout(40000);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);

			webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());

			HtmlPage page = webClient.getPage(url);
			
			webClient.waitForBackgroundJavaScript(10000);

			int statusCode = page.getWebResponse().getStatusCode();

			if (statusCode == 200) {

				String html = page.asXml();
				Document doc = Jsoup.parse(html);

				Elements rows = doc.select("table.wanted-person-description tbody tr");
				Element aliasesTxt = doc.select(".wanted-person-aliases p").first();
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

				String aliases = aliasesTxt == null ? "" : aliasesTxt.text();

				Element firstElement = divNameWanted.first();
				String name = firstElement.selectFirst("h1").text();

				int age = DateAndAgeUtil.calculateAge(dateOfBirth);

				if(name != null) {
					FbiPerson fbiPerson = new FbiPerson();
					fbiPerson.setAge(age);
					fbiPerson.setAliases(aliases);
					fbiPerson.setDescription(description.toString());
					fbiPerson.setEyes(eyes);
					fbiPerson.setImages(jsonImagemPersonString);
					fbiPerson.setName(name);
					fbiPerson.setColoredPerson(race);
					fbiPerson.setSex(sex);
					fbiPerson.setOccupations(occupation);
					fbiPerson.setPlaceOfBirth(placeOfBirth);

					fbiWantedPersonServiceImpl.create(fbiPerson);
				}
				
			
			}

			webClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
	private void GetIdentifiersOfPersonsWantedByFBI() {
		String url = "https://www.fbi.gov/wanted";

		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

			webClient.setJavaScriptTimeout(10000);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);

			webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());

			HtmlPage page = webClient.getPage(url);

			String html = page.asXml();
			Document doc = Jsoup.parse(html);

			Elements titleDivs = doc.select("div.title");

			for (Element titleDiv : titleDivs) {
				Elements anchorTags = titleDiv.select("a");

				String secondHref = anchorTags.get(1).attr("href");

				this.GetAndUpdateDatabaseOfPeopleWantedByFBI(secondHref);
			}

			webClient.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
