package net.simpleframework.mvc.component.portal;

import java.util.ArrayList;
import java.util.UUID;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.xml.AbstractElementBean;
import net.simpleframework.common.xml.XmlElement;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ColumnBean extends AbstractElementBean {

	private final PortalBean portalBean;

	private ArrayList<PageletBean> pagelets;

	private String width;

	public ColumnBean(final XmlElement xmlElement, final PortalBean portalBean) {
		super(xmlElement == null ? portalBean.getBeanElement().addElement("column") : xmlElement);
		this.portalBean = portalBean;
	}

	@Override
	public void syncElement() {
		super.syncElement();
		removeChildren("pagelet");
		for (final PageletBean pagelet : getPagelets()) {
			pagelet.syncElement();
			addElement(pagelet);
		}
	}

	public PortalBean getPortalBean() {
		return portalBean;
	}

	public ArrayList<PageletBean> getPagelets() {
		if (pagelets == null) {
			pagelets = new ArrayList<PageletBean>();
		}
		return pagelets;
	}

	public String getWidth() {
		return StringUtils.hasText(width) ? width : "auto";
	}

	public void setWidth(final String width) {
		this.width = width;
	}

	private String id;

	public String id() {
		if (id == null) {
			id = "ul_" + UUID.randomUUID().toString();
		}
		return id;
	}
}
