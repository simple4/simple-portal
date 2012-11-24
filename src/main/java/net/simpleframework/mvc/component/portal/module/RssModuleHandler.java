package net.simpleframework.mvc.component.portal.module;

import net.simpleframework.common.Convert;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.PageletBean;
import net.simpleframework.mvc.component.portal.PageletTitle;
import net.simpleframework.mvc.component.portal.PortalUtils;
import net.simpleframework.mvc.component.portal.module.RssUtils.RssChannel;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class RssModuleHandler extends AbstractPortalModuleHandler {
	public RssModuleHandler(final PageletBean pagelet) {
		super(pagelet);
	}

	private static String[] defaultOptions = new String[] { "_rss_url=", "_rss_rows=6",
			"_rss_times=0", "_rss_tip=true" };

	@Override
	protected String[] getDefaultOptions() {
		return defaultOptions;
	}

	public RssChannel getRssChannel() {
		return RssUtils.getRssChannel(getPagelet().getOptionProperty("_rss_url"));
	}

	public int getRows() {
		return Convert.toInt(getPagelet().getOptionProperty("_rss_rows"));
	}

	public boolean isShowTip() {
		return Convert.toBool(getPagelet().getOptionProperty("_rss_tip"));
	}

	@Override
	public IForward getPageletContent(final ComponentParameter cParameter) {
		if (getRssChannel() == null) {
			return null;
		}
		return new UrlForward(getModuleHomeUrl() + "/rss.jsp");
	}

	@Override
	public IForward getPageletOptionContent(final ComponentParameter cParameter) {
		return new UrlForward(getModuleHomeUrl() + "/rss_option.jsp");
	}

	@Override
	public String getOptionUITitle(final ComponentParameter cParameter) {
		final PageletBean pagelet = getPagelet();
		final RssChannel channel = RssUtils.getRssChannel(getPagelet().getOptionProperty("_rss_url"),
				false);
		final PageletTitle title = pagelet.getTitle();
		title.setValue(channel.getTitle());
		title.setLink(channel.getLink());
		return PortalUtils.getTitleString(cParameter, pagelet);
	}
}
