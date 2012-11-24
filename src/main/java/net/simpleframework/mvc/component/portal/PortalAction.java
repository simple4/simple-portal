package net.simpleframework.mvc.component.portal;

import static net.simpleframework.common.I18n.$m;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.HtmlUtils;
import net.simpleframework.common.html.element.ETextAlign;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JsonForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.portal.module.IPortalModuleHandler;
import net.simpleframework.mvc.component.portal.module.PortalModuleRegistryFactory;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PortalAction extends DefaultAjaxRequestHandler {

	public IForward portalRequest(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		return new UrlForward(((PortalRender) nComponentParameter.componentBean
				.getComponentRegistry().getComponentRender()).getResponseUrl(nComponentParameter));
	}

	public IForward contentRequest(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final PageletBean pagelet = PortalUtils.getPageletBean(nComponentParameter);
		if (pagelet == null) {
			return null;
		}
		final JsonForward json = new JsonForward();
		final String pageletId = pagelet.id();
		json.put("li", pageletId);
		final IPortalModuleHandler layoutModuleHandle = PortalModuleRegistryFactory.get()
				.getModuleHandler(pagelet);
		if (layoutModuleHandle == null) {
			return json;
		}
		String responseText = null;
		try {
			final IForward forward = layoutModuleHandle.getPageletContent(nComponentParameter);
			if (forward != null) {
				responseText = forward.getResponseText(nComponentParameter);
			} else {
				responseText = "<div style='text-align: center; padding: 6px 0;'><input type='button' value='"
						+ $m("PortalAction.0") + "' onclick='_lo_fireOptionAction(this);' /></div>";
			}
		} catch (final Exception e) {
			responseText = HtmlUtils.convertHtmlLines(e.getMessage());
		}
		json.put("text", StringUtils.blank(responseText));
		json.put("ajaxRequestId", cParameter.hashId());
		final String fontStyle = pagelet.getFontStyle();
		if (StringUtils.hasText(fontStyle)) {
			json.put("fontStyle", fontStyle);
		}
		return json;
	}

	public IForward optionSave(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final PageletBean pagelet = PortalUtils.getPageletBean(nComponentParameter);
		final IPortalModuleHandler mh = PortalModuleRegistryFactory.get().getModuleHandler(pagelet);
		if (mh != null) {
			mh.optionSave(nComponentParameter);
		}
		final JsonForward json = new JsonForward();
		final String title = mh.getOptionUITitle(nComponentParameter);
		if (StringUtils.hasText(title)) {
			json.put("title", title);
		}
		return json;
	}

	public IForward draggableSave(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		PortalUtils.savePortal(nComponentParameter, null,
				!Convert.toBool(nComponentParameter.getParameter("checked")));
		return null;
	}

	public IForward uiOptionSave(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final PageletBean pagelet = PortalUtils.getPageletBean(nComponentParameter);

		final JsonForward json = new JsonForward();
		final String pageletId = pagelet.id();
		json.put("li", pageletId);
		final PageletTitle pageletTitle = pagelet.getTitle();

		pageletTitle.setValue(nComponentParameter.getParameter("ui_options_title"));
		pageletTitle.setLink(nComponentParameter.getParameter("ui_options_link"));
		pageletTitle.setIcon(nComponentParameter.getParameter("ui_options_icon"));
		pageletTitle.setFontStyle(nComponentParameter.getParameter("ui_options_fontstyle"));
		pageletTitle.setDescription(nComponentParameter.getParameter("ui_options_desc"));
		json.put("ui_title", PortalUtils.getTitleString(nComponentParameter, pagelet));

		// pagelet
		final int h = Convert.toInt(nComponentParameter.getParameter("ui_options_height"));
		pagelet.setHeight(h);
		String style = "height:" + (h > 0 ? (h + "px") : "auto") + ";";
		try {
			final ETextAlign ta = ETextAlign.valueOf(nComponentParameter
					.getParameter("ui_options_align"));
			pagelet.setAlign(ta);
			style += "text-align: " + pagelet.getAlign() + ";";
		} catch (final Exception e) {
		}
		json.put("ui_c_style", style);

		final String fontStyle = nComponentParameter.getParameter("ui_options_c_fontstyle");
		pagelet.setFontStyle(fontStyle);
		if (StringUtils.hasText(fontStyle)) {
			json.put("ui_c_fontstyle", fontStyle);
		}
		pagelet.setSync(Convert.toBool(nComponentParameter.getParameter("ui_options_sync")));
		PortalUtils.savePortal(nComponentParameter);
		return json;
	}

	public IForward columnSizeSave(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final int columnCount = Convert.toInt(nComponentParameter.getParameter("_columns_select"), 1);
		final ArrayList<ColumnBean> columns = new ArrayList<ColumnBean>(
				PortalUtils.getColumns(nComponentParameter));
		final ArrayList<PageletBean> removes = new ArrayList<PageletBean>();
		int size;
		XmlElement xmlElement = null;
		while ((size = columns.size()) != columnCount) {
			if (size > columnCount) {
				final ColumnBean column = columns.remove(size - 1);
				removes.addAll(column.getPagelets());
			} else {
				if (xmlElement == null) {
					if (size > 0) {
						xmlElement = columns.get(0).getBeanElement().getParent().addElement("column");
					} else {
						// new XmlDocument("");
					}
				}
				columns.add(new ColumnBean(xmlElement, (PortalBean) nComponentParameter.componentBean));
			}
		}
		if (removes.size() > 0) {
			columns.get(size - 1).getPagelets().addAll(removes);
		}
		for (int i = 0; i < columns.size(); i++) {
			final ColumnBean column = columns.get(i);
			column.setWidth(nComponentParameter.getParameter("_cw" + (i + 1)));
		}

		final JsonForward json = new JsonForward();
		PortalUtils.savePortal(nComponentParameter, columns);
		json.put("layout", nComponentParameter.getBeanProperty("name"));
		return json;
	}

	public IForward positionSave(final ComponentParameter cParameter) {
		final String[] ulArr = StringUtils.split(cParameter.getParameter("ul"), "#");
		if (ulArr == null) {
			return null;
		}
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final ArrayList<ColumnBean> columns = new ArrayList<ColumnBean>();
		final Map<ColumnBean, Collection<PageletBean>> pagelets = new HashMap<ColumnBean, Collection<PageletBean>>();
		for (final String ul : ulArr) {
			final ColumnBean column = PortalUtils.getColumnBeanByHashId(nComponentParameter, ul);
			final String[] liArr = StringUtils.split(cParameter.getParameter(ul), "#");
			if (liArr != null) {
				final Collection<PageletBean> coll = new ArrayList<PageletBean>();
				pagelets.put(column, coll);
				for (final String li : liArr) {
					final PageletBean pagelet = PortalUtils.getPageletByHashId(nComponentParameter, li);
					if (pagelet != null) {
						coll.add(pagelet);
					}
				}
			}
			columns.add(column);
		}

		for (final Map.Entry<ColumnBean, Collection<PageletBean>> entry : pagelets.entrySet()) {
			final Collection<PageletBean> _pagelets = entry.getKey().getPagelets();
			_pagelets.clear();
			_pagelets.addAll(entry.getValue());
		}

		PortalUtils.savePortal(nComponentParameter, columns);
		return null;
	}

	public IForward addModule(final ComponentParameter cParameter) {
		final ComponentParameter nComponentParameter = PortalUtils.get(cParameter);
		final JsonForward json = new JsonForward();
		ColumnBean column = null;
		PageletBean pagelet = PortalUtils.getPageletBean(nComponentParameter);
		if (pagelet == null) {
			final Collection<ColumnBean> coll = PortalUtils.getColumns(nComponentParameter);
			if (coll != null && coll.size() > 0) {
				column = coll.iterator().next();
				final ArrayList<PageletBean> al = column.getPagelets();
				if (al.size() > 0) {
					pagelet = al.get(0);
				}
			} else {
				return json;
			}
		}
		if (pagelet != null && column == null) {
			column = pagelet.getColumnBean();
		}
		final Collection<PageletBean> pagelets = column.getPagelets();
		final ArrayList<PageletBean> al = new ArrayList<PageletBean>(pagelets);
		final int j = pagelet != null ? al.indexOf(pagelet) : -1;
		final PageletBean created = new PageletBean(column);
		created.setModule(nComponentParameter.getParameter("module"));
		al.add(j + 1, created);
		pagelets.clear();
		pagelets.addAll(al);
		json.put("created", PortalUtils.createPagelet(nComponentParameter, created));
		json.put("column", column.id());
		json.put("draggable", nComponentParameter.getBeanProperty("draggable"));
		PortalUtils.savePortal(nComponentParameter);
		return json;
	}

	public IForward urlIconsSelect(final ComponentParameter cParameter) {
		final String rPath = ComponentUtils.getResourceHomePath(PortalBean.class);
		return new UrlForward(rPath + "/jsp/icons_select.jsp?iconPath=" + rPath + "/jsp/icons/");
	}
}
