package net.simpleframework.mvc.component.portal;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.HtmlUtils;
import net.simpleframework.common.html.element.LinkElement;
import net.simpleframework.common.html.js.JavascriptUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.web.Browser;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.MVCContextFactory;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRenderUtils;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.portal.module.IPortalModuleHandler;
import net.simpleframework.mvc.component.portal.module.OptionWindowUI;
import net.simpleframework.mvc.component.portal.module.PortalModule;
import net.simpleframework.mvc.component.portal.module.PortalModuleRegistryFactory;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class PortalUtils {
	static Log log = LogFactory.getLogger(PortalUtils.class);

	static Collection<ColumnBean> getColumns(final ComponentParameter cParameter) {
		Collection<ColumnBean> columns = null;
		final IPortalHandler lh = (IPortalHandler) cParameter.getComponentHandler();
		if (lh != null) {
			columns = lh.getPortalColumns(cParameter);
		}
		if (columns == null) {
			columns = ((PortalBean) cParameter.componentBean).getColumns();
		}
		return columns == null ? new ArrayList<ColumnBean>() : columns;
	}

	static boolean isManager(final ComponentParameter cParameter) {
		final IPagePermissionHandler permission;
		return (Boolean) cParameter.getBeanProperty("showMenu")
				|| (permission = MVCContextFactory.permission()).isMember(
						permission.getLoginId(cParameter), cParameter.getBeanProperty("roleManager"));
	}

	public static String renderHtml(final ComponentParameter cParameter) {
		final Collection<ColumnBean> columns = getColumns(cParameter);
		final StringBuilder sb = new StringBuilder();
		String columnIds = "";
		for (final ColumnBean column : columns) {
			columnIds += column.id() + " ";
		}
		final PortalBean portalBean = (PortalBean) cParameter.componentBean;
		sb.append("<div id=\"layout_").append(portalBean.hashId());
		sb.append("\" class=\"portal\"");
		final boolean isManager = isManager(cParameter);
		if (isManager) {
			sb.append(" isDraggable=\"");
			sb.append(cParameter.getBeanProperty("draggable"));
			sb.append("\"");
		}
		sb.append(" isManager=\"").append(isManager);
		sb.append("\" columnIds=\"").append(columnIds).append("\">");
		sb.append(ComponentRenderUtils.genParameters(cParameter));

		for (final ColumnBean c : columns) {
			sb.append("<div class=\"column\" style=\"width:").append(c.getWidth()).append("\">");
			sb.append("<ul id=\"").append(c.id()).append("\" class=\"sortable\">");
			final ArrayList<PageletBean> pagelets = c.getPagelets();
			if (pagelets.size() == 0) {
				final String module = (String) cParameter.getBeanProperty("autoPagelet");
				if (StringUtils.hasText(module) && !module.equals("false")) {
					final PageletBean pagelet = new PageletBean(c);
					pagelet.setModule(module);
					pagelets.add(pagelet);
				}
			}
			for (final PageletBean pagelet : pagelets) {
				sb.append(createPagelet(cParameter, pagelet, isManager));
			}
			sb.append("</ul></div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	static String createPagelet(final ComponentParameter cParameter, final PageletBean pagelet) {
		return createPagelet(cParameter, pagelet, isManager(cParameter));
	}

	static String createPagelet(final ComponentParameter cParameter, final PageletBean pagelet,
			final boolean isManager) {
		final StringBuilder sb = new StringBuilder();
		final IPortalModuleHandler lmHandle = pagelet.getModuleHandler();
		if (lmHandle == null) {
			return sb.toString();
		}
		sb.append("<div class=\"move\">");
		sb.append("<table style=\"width: 100%;\" cellpadding=\"0\" cellspacing=\"0\">");
		sb.append("<tr>");
		sb.append("<td class=\"titlebar\">").append(getTitleString(cParameter, pagelet))
				.append("</td>");
		sb.append("<td class=\"act\">");
		if (isManager) {
			sb.append("<div class=\"tb\" style=\"display:none;\">");
			sb.append("<div class=\"action_delete\"></div>");
			sb.append("<div class=\"action_refresh\"></div>");
			sb.append("<div class=\"action_menu\"></div>");
			sb.append("</div>");
		}
		sb.append("</td></tr>");
		sb.append("</table>");
		sb.append("</div>");
		final int h = pagelet.getHeight();
		sb.append("<div class=\"content\" style=\"height:");
		sb.append(h > 0 ? h + "px" : "auto").append(";text-align:").append(pagelet.getAlign())
				.append("\">");

		final String pageletId = pagelet.id();
		if (pagelet.isSync()) {
			final IForward forward = lmHandle.getPageletContent(cParameter);
			if (forward != null) {
				HttpUtils.putParameter(cParameter.request, PortalUtils.PAGELET_ID, pageletId);
				try {
					sb.append(UrlForward.encodeResponseText(forward.getResponseText(cParameter)));
				} catch (final Exception e) {
					sb.append(e.getMessage());
				}
			}
		} else {
			sb.append($m("LayoutUtils.renderHtml.0"));
		}
		sb.append("</div>");

		final Properties properties = new Properties();
		properties.setProperty("class", "pagelet");
		properties.setProperty("id", pageletId);
		properties.setProperty("sync", String.valueOf(pagelet.isSync()));
		properties.setProperty("showMenu", String.valueOf(isManager));
		final IPortalModuleHandler mh = PortalModuleRegistryFactory.get().getModuleHandler(pagelet);
		if (mh != null) {
			if (mh.getPageletOptionContent(cParameter) != null) {
				properties.setProperty("showOption", "true");
			}
			OptionWindowUI optionWindowUI;
			if ((optionWindowUI = mh.getPageletOptionUI(cParameter)) != null) {
				final String title = optionWindowUI.getTitle();
				if (StringUtils.hasText(title)) {
					properties.setProperty("window_title", title);
				}
				final int height = optionWindowUI.getHeight();
				if (height > 0) {
					properties.setProperty("window_height", String.valueOf(height));
				}
				final int width = optionWindowUI.getWidth();
				if (width > 0) {
					properties.setProperty("window_width", String.valueOf(width));
				}
			}
		}
		return HtmlUtils.tag("li", wrapRound(cParameter, sb.toString()), properties);
	}

	public static String getTitleString(final ComponentParameter cParameter,
			final PageletBean pagelet) {
		String titleValue = null;
		final PageletTitle pageletTitle = pagelet.getTitle();
		final String value = pageletTitle != null ? pageletTitle.getValue() : null;
		if (StringUtils.hasText(value)) {
			titleValue = value;
		} else {
			final PortalModule moduleBean = pagelet.getModuleBean();
			if (moduleBean != null) {
				final String text = moduleBean.getText();
				if (StringUtils.hasText(text)) {
					titleValue = text;
				}
			}
		}
		titleValue = StringUtils.text(titleValue, HttpUtils.HTML_BLANK_STRING);
		final StringBuilder sb = new StringBuilder();
		if (pageletTitle != null) {
			final String id = pagelet.id();
			sb.append("<table style=\"width: 100%;\" id=\"title_").append(id);
			sb.append("\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
			final String icon = pageletTitle.getIcon();
			if (StringUtils.hasText(icon)) {
				sb.append("<td class=\"icon\">");
				sb.append("<img src=\"");
				sb.append(ComponentUtils.getResourceHomePath(PortalBean.class));
				sb.append("/jsp/icons/").append(icon).append("\"/>");
				sb.append("</td>");
			}
			sb.append("<td class=\"title\">");
			final String link = pageletTitle.getLink();
			if (StringUtils.hasText(link)) {
				sb.append(new LinkElement(titleValue).setHref(link)
						.setStyle(pageletTitle.getFontStyle()).setTarget("_blank"));
			} else {
				sb.append("<span");
				final String fontStyle = pageletTitle.getFontStyle();
				if (StringUtils.hasText(fontStyle)) {
					sb.append(" style=\"").append(fontStyle).append("\"");
				}
				sb.append(">").append(titleValue).append("</span>");
			}
			sb.append("</td>");
			sb.append("</tr></table>");
			final StringBuilder js = new StringBuilder();
			final String desc = pageletTitle.getDescription();
			if (StringUtils.hasText(desc)) {
				js.append("new Tip($(\"title_").append(id).append("\"), \"");
				js.append(JavascriptUtils.escape(HtmlUtils.convertHtmlLines(desc)));
				js.append("\", { delay: 1 });");
			}
			if (js.length() > 0) {
				sb.append(JavascriptUtils.wrapScriptTag(js.toString()));
			}
		} else {
			sb.append(titleValue);
		}
		return sb.toString();
	}

	private static String wrapRound(final ComponentParameter cParameter, final String s) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"jbox\">");
		final Browser browser = Browser.get(cParameter.request);
		final boolean j = browser.isTrident() && browser.getVersion() == 8;
		if (j) {
			sb.append("<div class=\"j1\"></div><div class=\"j2\"></div>");
		}
		sb.append(s);
		if (j) {
			sb.append("<div class=\"j3\"></div><div class=\"j4\"></div>");
		}
		sb.append("</div>");
		return sb.toString();
	}

	public static void savePortal(final ComponentParameter cParameter) {
		savePortal(cParameter, null);
	}

	public static void savePortal(final ComponentParameter cParameter,
			final Collection<ColumnBean> columns) {
		savePortal(cParameter, columns, (Boolean) cParameter.getBeanProperty("draggable"));
	}

	public static void savePortal(final ComponentParameter cParameter,
			Collection<ColumnBean> columns, final boolean draggable) {
		final IPortalHandler lh = (IPortalHandler) cParameter.getComponentHandler();
		if (lh == null) {
			return;
		}
		if (columns == null) {
			columns = getColumns(cParameter);
		}
		lh.updatePortal(cParameter, columns, draggable);
	}

	public static ColumnBean getColumnBeanByHashId(final ComponentParameter cParameter,
			final String hashId) {
		if (hashId != null) {
			for (final ColumnBean column : getColumns(cParameter)) {
				if (hashId.equals(column.id())) {
					return column;
				}
			}
		}
		return null;
	}

	public static PageletBean getPageletByHashId(final ComponentParameter cParameter,
			final String hashId) {
		if (hashId != null) {
			for (final ColumnBean column : getColumns(cParameter)) {
				for (final PageletBean pagelet : column.getPagelets()) {
					if (hashId.equals(pagelet.id())) {
						return pagelet;
					}
				}
			}
		}
		return null;
	}

	public static final String PAGELET_ID = "pageletId";

	public static PageletBean getPageletBean(final PageRequestResponse rRequest) {
		final ComponentParameter nComponentParameter = get(rRequest);
		return getPageletByHashId(nComponentParameter, nComponentParameter.getParameter(PAGELET_ID));
	}

	public static final String BEAN_ID = "portal_@bid";

	public static ComponentParameter get(final PageRequestResponse rRequest) {
		return ComponentParameter.get(rRequest, BEAN_ID);
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return ComponentParameter.get(request, response, BEAN_ID);
	}
}
