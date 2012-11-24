package net.simpleframework.mvc.component.portal.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import net.simpleframework.common.xml.XmlDocument;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.portal.PageletBean;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class WeatherModuleHandler extends AbstractPortalModuleHandler {
	public WeatherModuleHandler(final PageletBean pagelet) {
		super(pagelet);
	}

	private static String[] defaultOptions = new String[] { "_weather_code=CHXX0008" };

	@Override
	protected String[] getDefaultOptions() {
		return defaultOptions;
	}

	@Override
	public IForward getPageletOptionContent(final ComponentParameter cParameter) {
		return new UrlForward(getModuleHomeUrl() + "/weather_option.jsp");
	}

	@Override
	public IForward getPageletContent(final ComponentParameter cParameter) {
		return new TextForward(output(cParameter));
	}

	public String output(final ComponentParameter cParameter) {
		InputStream inputStream;
		try {
			final URL url = new URL("http://xml.weather.yahoo.com/forecastrss?p="
					+ getPagelet().getOptionProperty("_weather_code") + "&u=c");
			inputStream = url.openStream();
		} catch (final IOException e) {
			return e.getMessage();
		}

		final StringBuilder sb = new StringBuilder();
		new XmlDocument(inputStream) {

			@Override
			protected void init() throws Exception {
				final XmlElement root = getRoot();
				final XmlElement channel = root.element("channel");
				final String link = channel.elementText("link");
				final XmlElement item = channel.element("item");
				XmlElement ele = item.element("yweather:condition");
				if (ele == null) {
					sb.append("ERROR");
					return;
				}

				final String imgPath = ComponentUtils.getCssResourceHomePath(cParameter)
						+ "/images/yahoo/";
				String text, image;
				Date date = new SimpleDateFormat(YahooWeatherUtils.RFC822_MASKS[1], Locale.US)
						.parse(ele.attributeValue("date"));
				final int temp = Integer.parseInt(ele.attributeValue("temp"));
				int code = Integer.valueOf(ele.attributeValue("code")).intValue();
				if (code == 3200) {
					text = YahooWeatherUtils.yahooTexts[YahooWeatherUtils.yahooTexts.length - 1];
					image = imgPath + "3200.gif";
				} else {
					text = YahooWeatherUtils.yahooTexts[code];
					image = imgPath + code + ".gif";
				}
				sb.append("<div style=\"line-height: normal;\"><a target=\"_blank\" href=\"")
						.append(link).append("\"><img src=\"");
				sb.append(image).append("\" /></a>");
				sb.append(YahooWeatherUtils.formatHour(date)).append(" - ");
				sb.append(text).append(" - ").append(temp).append("℃").append("<br>");

				final Iterator<?> it = item.elementIterator("yweather:forecast");
				while (it.hasNext()) {
					ele = (XmlElement) it.next();
					date = new SimpleDateFormat("dd MMM yyyy", Locale.US).parse(ele
							.attributeValue("date"));
					final int low = Integer.parseInt(ele.attributeValue("low"));
					final int high = Integer.parseInt(ele.attributeValue("high"));
					code = Integer.valueOf(ele.attributeValue("code")).intValue();
					if (code == 3200) {
						text = YahooWeatherUtils.yahooTexts[YahooWeatherUtils.yahooTexts.length - 1];
						image = imgPath + "3200.gif";
					} else {
						text = YahooWeatherUtils.yahooTexts[code];
						image = imgPath + code + ".gif";
					}
					sb.append(YahooWeatherUtils.formatWeek(date)).append(" ( ");
					sb.append(text).append(". ");
					sb.append(low).append("℃~").append(high).append("℃");
					sb.append(" )<br>");
				}
				sb.append("</div>");
			}
		};
		return sb.toString();
	}

	@Override
	public OptionWindowUI getPageletOptionUI(final ComponentParameter cParameter) {
		return new OptionWindowUI(getPagelet()) {

			@Override
			public int getHeight() {
				return 150;
			}
		};
	}
}
