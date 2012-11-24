package net.simpleframework.mvc.component.portal.module;

import net.simpleframework.common.I18n;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.html.element.TextObject;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.portal.PortalBean;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PortalModule extends TextObject<PortalModule> {
	private static final String OTHER_MODULE_CATALOG = "OTHERs";

	private String icon;

	private String handleClass;

	private String catalog;

	private String description;

	public String getIcon() {
		if (StringUtils.hasText(icon)) {
			if (icon.charAt(0) == '/') {
				return icon;
			} else {
				return ComponentUtils.getResourceHomePath(PortalBean.class) + "/images/" + icon;
			}
		} else {
			return MVCUtils.getPageResourcePath() + "/images/none.png";
		}
	}

	public PortalModule setIcon(final String icon) {
		this.icon = icon;
		return this;
	}

	public String getHandleClass() {
		return handleClass;
	}

	public PortalModule setHandleClass(final String handleClass) {
		this.handleClass = handleClass;
		return this;
	}

	public String getCatalog() {
		return StringUtils.hasText(catalog) ? catalog : OTHER_MODULE_CATALOG;
	}

	public PortalModule setCatalog(final String catalog) {
		this.catalog = I18n.replaceI18n(catalog);
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PortalModule setDescription(final String description) {
		this.description = I18n.replaceI18n(description);
		return this;
	}
}
