package net.simpleframework.mvc.component.portal;

import java.util.Collection;
import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.portal.module.IPortalModuleHandler;
import net.simpleframework.mvc.component.portal.module.PortalModuleRegistryFactory;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PortalEditPageLoad extends DefaultPageHandler {

	public void optionLoaded(final PageParameter pParameter, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final IPortalModuleHandler mh = PortalModuleRegistryFactory.get().getModuleHandler(
				PortalUtils.getPageletBean(pParameter));
		if (mh != null) {
			mh.optionLoaded(pParameter, dataBinding);
		}
	}

	public void uiOptionLoaded(final PageParameter pParameter,
			final Map<String, Object> dataBinding, final PageSelector selector) {
		final PageletBean pagelet = PortalUtils.getPageletBean(pParameter);
		if (pagelet == null) {
			return;
		}

		final PageletTitle title = pagelet.getTitle();
		if (title != null) {
			dataBinding.put("ui_options_title", title.getValue());
			dataBinding.put("ui_options_link", title.getLink());
			final String icon = title.getIcon();
			if (StringUtils.hasText(icon)) {
				dataBinding.put("__homepath", ComponentUtils.getResourceHomePath(PortalBean.class)
						+ "/jsp/icons/");
				dataBinding.put("ui_options_icon", icon);
			}
			final String f = title.getFontStyle();
			if (StringUtils.hasText(f)) {
				dataBinding.put("ui_options_fontstyle", f);
			}
			dataBinding.put("ui_options_desc", title.getDescription());
		}

		final int height = pagelet.getHeight();
		if (height > 0) {
			dataBinding.put("ui_options_height", height);
		}
		dataBinding.put("ui_options_align", pagelet.getAlign());
		final String fontStyle = pagelet.getFontStyle();
		if (StringUtils.hasText(fontStyle)) {
			dataBinding.put("ui_options_c_fontstyle", fontStyle);
		}

		dataBinding.put("ui_options_sync", pagelet.isSync());
	}

	public void columnSizeLoaded(final PageParameter pParameter,
			final Map<String, Object> dataBinding, final PageSelector selector) {
		final ComponentParameter nComponentParameter = PortalUtils.get(pParameter);
		if (nComponentParameter.componentBean == null) {
			return;
		}
		final Collection<ColumnBean> columns = PortalUtils.getColumns(nComponentParameter);
		dataBinding.put("_columns_select", columns.size());
		int i = 1;
		for (final ColumnBean column : columns) {
			dataBinding.put("_cw" + i++, column.getWidth());
		}
	}
}
