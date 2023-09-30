package br.com.idwall.scheduled;

import org.htmlunit.BrowserVersion;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.javascript.SilentJavaScriptErrorListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.idwall.domain.InterpolPerson;
import br.com.idwall.service.impl.InterpolWantedPersonServiceImpl;
import br.com.idwall.util.DateAndAgeUtil;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@EnableScheduling
public class InterpolScheduler {

	@Autowired
	private InterpolWantedPersonServiceImpl interpolWantedPersonServiceImpl;

	private void GetAndUpdateDatabaseOfPeopleWantedByInterpol(String href) {
		String url = "https://www.interpol.int/How-we-work/Notices/Red-Notices/View-Red-Notices" + href;

		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.setJavaScriptTimeout(40000);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(true);

			webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());

			HtmlPage page = webClient.getPage(url);

			webClient.waitForBackgroundJavaScript(10000);

			int statusCode = page.getWebResponse().getStatusCode();

			if (statusCode == 200) {
				String html = page.asXml();
				Document doc = Jsoup.parse(html);

				Element infosWrapper = doc.select(".wantedsingle__infosWrapper").first();
				String languages = doc.selectFirst("strong#languages_spoken_ids").text();
				Element wrapperImg = doc.select(".redNoticeLargePhoto__wrapperImg").first();
				Elements rows = infosWrapper.select("table tbody tr");
				Element sexStrong = doc.selectFirst("strong#sex_id");

				String srcValue = (wrapperImg != null && wrapperImg.select("img").size() >= 2)
						? wrapperImg.select("img").get(1).attr("src")
						: null;

				Map<String, String> infoMap = new HashMap<>();

				for (Element row : rows) {
					String key = row.select("td").first().text().trim();
					String value = row.select("td").get(1).text().trim();
					infoMap.put(key, value);
				}

				String gender = null;

				if (sexStrong != null) {
					Element sexSpanElement = sexStrong.selectFirst("span:not(.hidden)");

					if (sexSpanElement != null) {
						gender = sexSpanElement.text();
					}
				}

				String forename = infoMap.get("Forename");
				String dateOfBirth = infoMap.get("Date of birth");
				String placeOfBirth = infoMap.get("Place of birth");
				String nationality = infoMap.get("Nationality");

				int age = DateAndAgeUtil.extractAge(dateOfBirth);
				Date dateOfBirthFormat = DateAndAgeUtil.extractDateOfBirth(dateOfBirth);

				Optional<InterpolPerson> personWanted = interpolWantedPersonServiceImpl.getByName(forename);

				if (personWanted.isPresent() && srcValue != null && languages != null && forename != null
						&& gender != null && dateOfBirthFormat != null && placeOfBirth != null && nationality != null
						&& age != 0) {
					InterpolPerson person = personWanted.get();

					String sex = gender != "Male" ? "Masculino" : "Feminino";

					person.setName(forename);
					person.setSex(sex);
					person.setAge(age);
					person.setDateOfBirth(dateOfBirthFormat);
					person.setPlaceOfBirth(placeOfBirth);
					person.setNationality(nationality);
					person.setLanguages(languages);
					person.setImageUrl(srcValue);
					person.setUpdateAt(new Date());

					interpolWantedPersonServiceImpl.updateById(person);
				} else {

					if (srcValue != null && languages != null && forename != null && gender != null
							&& dateOfBirthFormat != null && placeOfBirth != null && nationality != null && age != 0) {

						String sex = gender.toString() != "Male" ? "Masculino" : "Feminino";
						
						InterpolPerson interpolPerson = new InterpolPerson();
						interpolPerson.setName(forename);
						interpolPerson.setSex(sex);
						interpolPerson.setAge(age);
						interpolPerson.setDateOfBirth(dateOfBirthFormat);
						interpolPerson.setPlaceOfBirth(placeOfBirth);
						interpolPerson.setNationality(nationality);
						interpolPerson.setLanguages(languages);
						interpolPerson.setImageUrl(srcValue);

						interpolWantedPersonServiceImpl.create(interpolPerson);
					}
				}
			} else {
				System.out.println("Pelo menos um dos campos é nulo.");
			}

			webClient.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Scheduled(cron = "0 42 17 30 9 ?", zone = "America/Sao_Paulo")
	@Scheduled(cron = "*/10 * * * * *", zone = "America/Sao_Paulo")
	private void GetIdentifiersOfPersonsWantedByInterpol() {
		String url = "https://www.interpol.int/How-we-work/Notices/Red-Notices/View-Red-Notices";

		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.setJavaScriptTimeout(40000);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(true);

			webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());

			HtmlPage page = webClient.getPage(url);

			List<HtmlElement> redNoticeItems = new ArrayList<>();
			int timeoutInSeconds = 10;
			int pollingInterval = 500;
			long startTime = System.currentTimeMillis();

			while (redNoticeItems.isEmpty() && (System.currentTimeMillis() - startTime) < (timeoutInSeconds * 1000)) {
				Thread.sleep(pollingInterval);
				redNoticeItems = page.getByXPath("//div[contains(@class, 'redNoticeItem__text')]");
			}

			for (HtmlElement redNoticeItem : redNoticeItems) {
				List<HtmlElement> aTags = redNoticeItem.getElementsByTagName("a");
				for (HtmlElement aTag : aTags) {
					String href = aTag.getAttribute("href");
					this.GetAndUpdateDatabaseOfPeopleWantedByInterpol(href);
				}
			}

			webClient.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
