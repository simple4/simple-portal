package net.simpleframework.mvc.component.portal;

import java.io.IOException;
import java.util.Collection;

import net.simpleframework.mvc.component.ComponentHandleException;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * 这是一个开源的软件，请在LGPLv3下合法使用、修改或重新发布。
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultPortalHandler extends AbstractPortalHandler {

	@Override
	public Collection<ColumnBean> getPortalColumns(final ComponentParameter cParameter) {
		return ((PortalBean) cParameter.componentBean).getColumns();
	}

	@Override
	public void updatePortal(final ComponentParameter cParameter,
			final Collection<ColumnBean> columns, final boolean draggable) {
		final PortalBean portalBean = (PortalBean) cParameter.componentBean;
		portalBean.setDraggable(draggable);
		if (columns != null) {
			final Collection<ColumnBean> _columns = portalBean.getColumns();
			if (!_columns.equals(columns)) {
				_columns.clear();
				_columns.addAll(columns);
			}
		}
		try {
			portalBean.saveToFile();
		} catch (final IOException e) {
			throw ComponentHandleException.of(e);
		}
	}
}
