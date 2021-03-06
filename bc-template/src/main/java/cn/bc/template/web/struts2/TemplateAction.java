package cn.bc.template.web.struts2;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.util.TemplateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.option.domain.OptionItem;
import cn.bc.template.domain.Template;
import cn.bc.template.domain.TemplateParam;
import cn.bc.template.service.TemplateParamService;
import cn.bc.template.service.TemplateService;
import cn.bc.template.service.TemplateTypeService;
import cn.bc.template.util.DocxUtils;
import cn.bc.template.util.XlsUtils;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 模板表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateAction extends FileEntityAction<Long, Template> {
	private static final long serialVersionUID = 1L;
	private TemplateService templateService;
	private TemplateTypeService templateTypeService;
	private TemplateParamService templateParamService;


	// 模板类型集合
	public List<Map<String, String>> typeList;
	
	//模板参数
	public String templateParamIds;
	public Set<TemplateParam> templateParams;

	@Autowired
	public void setTemplateTypeService(TemplateTypeService templateTypeService) {
		this.templateTypeService = templateTypeService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.setCrudService(templateService);
		this.templateService = templateService;
	}
	
	@Autowired
	public void setTemplateParamService(TemplateParamService templateParamService) {
		this.templateParamService = templateParamService;
	}


	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			pageOption.addButton(new ButtonOption(
					getText("template.preview.test"), null,
					"bc.templateForm.inline").setId("templateInline"));
			pageOption
					.addButton(new ButtonOption(
							getText("template.show.history.version"), null,
							"bc.templateForm.showVersion")
							.setId("templateShowVersion"));
			if (editable)
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.templateForm.save").setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(545)
				.setMinHeight(200).setMinWidth(300).setMaxHeight(800)
				.setHelp("mubanguanli");
	}

	@Override
	protected void afterCreate(Template entity) {
		super.afterCreate(entity);
		// 内置 默认为否
		entity.setInner(false);
		// 状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
		// 默认模板类型为自定义文本
		entity.setTemplateType(this.templateTypeService.loadByCode("custom"));
		// 默认模板不可格式化
		entity.setFormatted(false);

		// uid
		entity.setUid(this.getIdGeneratorService().next(Template.ATTACH_TYPE));

		this.typeList = this.templateTypeService.findTemplateTypeOptionRtnId(true);
	}

	@Override
	protected void afterEdit(Template entity) {
		super.afterEdit(entity);

		this.typeList = this.templateTypeService.findTemplateTypeOptionRtnId(true);
		OptionItem.insertIfNotExist(typeList, entity.getTemplateType().getId()
				.toString(), entity.getTemplateType().getName());
		this.templateParams=entity.getParams();
	}

	@Override
	protected void afterOpen(Template entity) {
		super.afterOpen(entity);
		this.typeList = this.templateTypeService.findTemplateTypeOptionRtnId(true);
		OptionItem.insertIfNotExist(typeList, entity.getTemplateType().getId()
				.toString(), entity.getTemplateType().getName());
		this.templateParams=entity.getParams();
	}
	
	
	@Override
	protected void beforeSave(Template entity) {
		super.beforeSave(entity);
		
		Long[] ids = null;
		if (this.templateParamIds != null && this.templateParamIds.length() > 0) {
			String[] tpIds = this.templateParamIds.split(",");
			ids = new Long[tpIds.length];
			for (int i = 0; i < tpIds.length; i++) 
				ids[i] = new Long(tpIds[i]);
		}

		if(ids!=null&&ids.length>0){
			Set<TemplateParam> params=null;
			TemplateParam templateParam=null;
			for(int i=0;i<ids.length;i++){
				if(i==0)
					params=new HashSet<TemplateParam>();
				templateParam=this.templateParamService.load(ids[i]);
				params.add(templateParam);
			}
			if(this.getE().getParams()!=null){
				this.getE().getParams().clear();
				this.getE().getParams().addAll(params);
			}else
				this.getE().setParams(params);
			
		}
	}

	@Override
	public String save() throws Exception {
		Template template = this.getE();
		template.setTemplateType(this.templateTypeService.load(template
				.getTemplateType().getId()));
		// 设置保存文件大小 获取文件大小
		template.setSize(template.getSizeEx());
		this.beforeSave(template);
		this.templateService.save(template);
		this.afterSave(template);
		return "saveSuccess";
	}

	public Integer type;// 类型
	public Long tid;// 模板id
	public String code;// 编码
	public String version;// 版本号

	// 检查编码与版本号唯一
	public String isUniqueCodeAndVersion() {
		Json json = new Json();
		boolean flag = this.templateService.isUniqueCodeAndVersion(this.tid,
				code, version);
		if (flag) {
			json.put("result", getText("template.save.code"));
			this.json = json.toString();
			return "json";
		} else {
			json.put("result", "save");
			this.json = json.toString();
			return "json";
		}
	}

	// 检查模板内容是否为空
	public String isContent() {
		Json json = new Json();
		Template t = this.templateService.load(tid);
		boolean flag = (t.getContent() == null || t.getContent().length() == 0);
		json.put("result", flag);
		this.json = json.toString();
		return "json";
	}

	public String path;// 物理文件保存的相对路径
	public String content;// 模板内容

	// ---- 加载配置参数 ---开始--
	public String loadTplConfigParam() throws Exception {
		Json json = new Json();
		Template tpl = this.templateService.load(tid);
		if (tpl.getTemplateType().getCode().equals("custom") && content != null
				&& content.length() > 0) {
			tpl.setContent(content);
		}
		// 附件的扩展名
		String extension = tpl.getTemplateType().getExtension();
		InputStream is = tpl.getInputStream();
		List<String> markers;
		if (tpl.isPureText()) {
			markers = TemplateUtils.findMarkers(is);
			// 保存参数的集合
			json.put("value", this.getParamStr(markers));
		} else if (tpl.getTemplateType().getCode().equals("xls")
				&& extension.equals("xls")) {
			markers = XlsUtils.findMarkers(is);
			json.put("value", this.getParamStr(markers));
		} else if (tpl.getTemplateType().getCode().equals("word-docx")
				&& extension.equals("docx")) {
			markers = DocxUtils.findMarkers(is);
			json.put("value", this.getParamStr(markers));
		} 
		is.close();
		this.json = json.toString();
		return "json";
	}

	private String getParamStr(List<String> list) {
		String param = null;
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				param = list.get(i);
			} else {
				param = param + "," + list.get(i);
			}
		}
		return param;
	}

	// ---- 加载配置参数 ---结束--
}
