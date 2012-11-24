package net.simpleframework.mvc.component.portal;

import java.util.Map;

import net.simpleframework.common.coll.KVMap;
import net.simpleframework.mvc.component.AbstractComponentHandler;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractPortalHandler extends AbstractComponentHandler implements
		IPortalHandler {

	@Override
	public Object getBeanProperty(final ComponentParameter cParameter, final String beanProperty) {
		if ("roleManager".equals(beanProperty)) {
			return "sys_manager";
		}
		return super.getBeanProperty(cParameter, beanProperty);
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cParameter) {
		return ((KVMap) super.getFormParameters(cParameter)).add(PortalUtils.BEAN_ID,
				cParameter.getParameter(PortalUtils.BEAN_ID));
	}
}
