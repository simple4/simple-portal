package net.simpleframework.mvc.component.portal;

import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.IComponentRegistry;
import net.simpleframework.mvc.component.IComponentResourceProvider.AbstractComponentResourceProvider;
import net.simpleframework.mvc.impl.DefaultPageResourceProvider;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PortalResourceProvider extends AbstractComponentResourceProvider {
	public PortalResourceProvider(final IComponentRegistry componentRegistry) {
		super(componentRegistry);
	}

	@Override
	public String[] getJavascriptPath(final PageParameter pParameter) {
		final String rPath = getResourceHomePath(DefaultPageResourceProvider.class);
		return new String[] { rPath + DefaultPageResourceProvider.EFFECTS_FILE,
				rPath + DefaultPageResourceProvider.DRAGDROP_FILE,
				getResourceHomePath() + "/js/portal.js" };
	}

	@Override
	public String[] getCssPath(final PageParameter pParameter) {
		return new String[] { getCssResourceHomePath(pParameter) + "/portal.css" };
	}
}
