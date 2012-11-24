package net.simpleframework.mvc.component.portal.module;

import java.util.Map;

import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.PageletBean;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IPortalModuleHandler {
	PortalModule getModuleBean();

	PageletBean getPagelet();

	IForward getPageletContent(ComponentParameter cParameter);

	/** ----- options ----- **/

	IForward getPageletOptionContent(ComponentParameter cParameter);

	void optionSave(ComponentParameter cParameter);

	void optionLoaded(PageParameter pParameter, final Map<String, Object> dataBinding);

	/** ----- options ui ----- **/

	String getOptionUITitle(ComponentParameter cParameter);

	OptionWindowUI getPageletOptionUI(ComponentParameter cParameter);
}
