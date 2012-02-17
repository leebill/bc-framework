package cn.bc.placeorigin.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.placeorigin.service.PlaceOriginService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 籍贯表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PlaceOriginAction extends FileEntityAction<Long, PlaceOrigin> {
	private static final long serialVersionUID = 1L;
	private PlaceOriginService placeOriginService;
	
	@Autowired
	public void setPlaceOriginService(PlaceOriginService placeOriginService){
			this.placeOriginService=placeOriginService;
			this.setCrudService(placeOriginService);
	}
	
	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(600)
				.setHeight(400);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
				pageOption
				.addButton(new ButtonOption(getText("label.save"), "save"));
		}
	}

	public Json json;
	@Override
	public String delete() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", new Integer(BCConstants.STATUS_DISABLED));
		attributes.put("modifier", context.getUserHistory());
		attributes.put("modifiedDate", Calendar.getInstance());

		if (this.getId() != null) {// 处理一条
			this.placeOriginService.update(this.getId(), attributes);
		} else {// 处理一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.placeOriginService.update(ids, attributes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		json = new Json();
		json.put("msg", getText("form.disabled.success"));
		return "json";
	}
	
	
	
}