package net.simpleframework.mvc.component.portal;

import net.simpleframework.mvc.DefaultPageHandler;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PortalPageLoad extends DefaultPageHandler {

	@Override
	public Object getBeanProperty(final PageParameter pParameter, final String beanProperty) {
		if ("importPage".equals(beanProperty)) {
			final ComponentParameter nComponentParameter = PortalUtils.get(pParameter);
			if (PortalUtils.isManager(nComponentParameter)) {
				return new String[] { ComponentUtils.getResourceHomePath(PortalBean.class)
						+ "/jsp/portal_admin.xml" };
			}
		}
		return super.getBeanProperty(pParameter, beanProperty);
	}
}
