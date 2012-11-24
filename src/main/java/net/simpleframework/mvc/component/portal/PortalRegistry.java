package net.simpleframework.mvc.component.portal;

import java.util.ArrayList;
import java.util.Iterator;

import net.simpleframework.common.script.IScriptEval;
import net.simpleframework.common.xml.XmlElement;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentBean;
import net.simpleframework.mvc.component.ComponentHtmlRenderEx;
import net.simpleframework.mvc.component.ComponentName;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRender;
import net.simpleframework.mvc.component.ComponentResourceProvider;
import net.simpleframework.mvc.component.portal.module.PortalModuleRegistryFactory;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
@ComponentName(PortalRegistry.portal)
@ComponentBean(PortalBean.class)
@ComponentRender(PortalRender.class)
@ComponentResourceProvider(PortalResourceProvider.class)
public class PortalRegistry extends AbstractComponentRegistry {

	public static final String portal = "portal";

	public PortalRegistry() {
		PortalModuleRegistryFactory.get().init();
	}

	@Override
	public PortalBean createComponentBean(final PageParameter pParameter, final Object data) {
		final PortalBean portalBean = (PortalBean) super.createComponentBean(pParameter, data);

		ComponentHtmlRenderEx.createAjaxRequest(ComponentParameter.get(pParameter, portalBean));

		if (data instanceof XmlElement) {
			portalBean.getColumns().addAll(loadBean(pParameter, portalBean, (XmlElement) data));
		}
		return portalBean;
	}

	public ArrayList<ColumnBean> loadBean(final PageParameter pParameter,
			final PortalBean portalBean, final XmlElement component) {
		final ArrayList<ColumnBean> al = new ArrayList<ColumnBean>();
		if (component == null) {
			return al;
		}
		final IScriptEval scriptEval = pParameter.getScriptEval();
		final Iterator<?> it = component.elementIterator("column");
		while (it.hasNext()) {
			final XmlElement xmlElement = (XmlElement) it.next();
			final ColumnBean column = new ColumnBean(xmlElement, portalBean);
			al.add(column);
			column.parseElement(scriptEval);

			final Iterator<?> it2 = xmlElement.elementIterator("pagelet");
			while (it2.hasNext()) {
				final XmlElement xmlElement2 = (XmlElement) it2.next();
				final PageletBean pagelet = new PageletBean(xmlElement2, column);
				pagelet.parseElement(scriptEval);
				column.getPagelets().add(pagelet);

				final XmlElement titleElement = xmlElement2.element("title");
				if (titleElement != null) {
					final PageletTitle title = new PageletTitle(titleElement, pagelet);
					title.parseElement(scriptEval);
					pagelet.setTitle(title);
				}
			}
		}
		return al;
	}
}
