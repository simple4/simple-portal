package net.simpleframework.mvc.component.portal.module;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.portal.PageletBean;
import net.simpleframework.mvc.component.portal.PortalBean;
import net.simpleframework.mvc.component.portal.PortalUtils;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractPortalModuleHandler implements IPortalModuleHandler {
	private final PageletBean pagelet;

	public AbstractPortalModuleHandler(final PageletBean pagelet) {
		this.pagelet = pagelet;

		final String defaultOptions = StringUtils.join(getDefaultOptions(), "\n");
		if (!StringUtils.hasText(pagelet.getOptions())) {
			pagelet.setOptions(defaultOptions);
		} else {
			Properties properties = Convert.toProperties(defaultOptions);
			if (properties == null) {
				properties = new Properties();
			}
			properties.putAll(pagelet.getOptionProperties());
			pagelet.setOptions(Convert.toString(properties));
		}
	}

	protected abstract String[] getDefaultOptions();

	@Override
	public PortalModule getModuleBean() {
		return PortalModuleRegistryFactory.get().getModule(pagelet.getModule());
	}

	@Override
	public PageletBean getPagelet() {
		return pagelet;
	}

	@Override
	public IForward getPageletOptionContent(final ComponentParameter cParameter) {
		return null;
	}

	@Override
	public void optionSave(final ComponentParameter cParameter) {
		final PageletBean pagelet = getPagelet();
		final Properties properties = pagelet.getOptionProperties();
		if (properties == null) {
			return;
		}
		final Enumeration<?> names = properties.propertyNames();
		while (names.hasMoreElements()) {
			final String name = (String) names.nextElement();
			properties.setProperty(name, StringUtils.blank(cParameter.getParameter(name)));
		}
		pagelet.setOptions(Convert.toString(properties));
		PortalUtils.savePortal(cParameter);
	}

	@Override
	public void optionLoaded(final PageParameter pParameter, final Map<String, Object> dataBinding) {
		final PageletBean pagelet = getPagelet();
		final Properties properties = pagelet.getOptionProperties();
		if (properties == null) {
			return;
		}
		for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
			dataBinding.put((String) entry.getKey(), entry.getValue());
		}
	}

	@Override
	public OptionWindowUI getPageletOptionUI(final ComponentParameter cParameter) {
		return new OptionWindowUI(getPagelet());
	}

	@Override
	public String getOptionUITitle(final ComponentParameter cParameter) {
		return null;
	}

	protected String getModuleHomeUrl() {
		return ComponentUtils.getResourceHomePath(PortalBean.class) + "/jsp/module";
	}
}
