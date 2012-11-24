package net.simpleframework.mvc.component.portal.module;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.HtmlUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.PageletBean;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class CustomModuleHandler extends AbstractPortalModuleHandler {
	public CustomModuleHandler(final PageletBean pagelet) {
		super(pagelet);
	}

	private static String[] defaultOptions = new String[] { "_custom_c=" };

	@Override
	protected String[] getDefaultOptions() {
		return defaultOptions;
	}

	@Override
	public IForward getPageletOptionContent(final ComponentParameter cParameter) {
		return new UrlForward(getModuleHomeUrl() + "/custom_option.jsp");
	}

	@Override
	public IForward getPageletContent(final ComponentParameter cParameter) {
		final String url = getPagelet().getOptionProperty("_custom_url");
		if (StringUtils.hasText(url)) {
			return new UrlForward(url);
		} else {
			String input = getPagelet().getOptionProperty("_custom_c");
			if (!HtmlUtils.hasTag(input)) {
				input = HtmlUtils.convertHtmlLines(input);
			}
			return StringUtils.hasText(input) ? new TextForward(input) : null;
		}
	}

	@Override
	public OptionWindowUI getPageletOptionUI(final ComponentParameter cParameter) {
		return new OptionWindowUI(getPagelet()) {

			@Override
			public int getHeight() {
				return 260;
			}
		};
	}
}
