package net.simpleframework.mvc.component.portal.module;

import net.simpleframework.common.StringUtils;
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
public class UrlModuleHandler extends AbstractPortalModuleHandler {

	public UrlModuleHandler(final PageletBean pagelet) {
		super(pagelet);
	}

	private static String[] defaultOptions = new String[] { "_url_name=" };

	@Override
	protected String[] getDefaultOptions() {
		return defaultOptions;
	}

	@Override
	public IForward getPageletOptionContent(final ComponentParameter cParameter) {
		return new UrlForward(getModuleHomeUrl() + "/url_option.jsp");
	}

	@Override
	public IForward getPageletContent(final ComponentParameter cParameter) {
		final String url = getPagelet().getOptionProperty("_url_name");
		if (!StringUtils.hasText(url)) {
			return null;
		}
		if (url.charAt(0) == '/') {
			return new UrlForward(url);
		} else {
			final int height = getPagelet().getHeight();
			final TextForward tf = new TextForward();
			tf.append("<iframe width='99.9%' frameborder='0' marginheight='0' ");
			if (height > 0) {
				tf.append("height='").append(height - 2).append("px' ");
			}
			tf.append("marginwidth='0' src='").append(url).append("'></iframe>");
			return tf;
		}
	}

	@Override
	public OptionWindowUI getPageletOptionUI(final ComponentParameter cParameter) {
		return new OptionWindowUI(getPagelet()) {

			@Override
			public int getHeight() {
				return 120;
			}
		};
	}
}
