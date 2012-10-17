/**
 * 
 */
package cn.bc.netdisk.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 网络文件
 * 
 * @author zxr
 */
@Entity
@Table(name = "BC_NETDISK_FILE")
public class NetdiskFile extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(NetdiskFile.class);
	public static final String ATTACH_TYPE = NetdiskFile.class.getSimpleName();
	/** 模板存储的子路径，开头末尾不要带"/" */
	public static String DATA_SUB_PATH = "netdisk";

	private Long pid;// 所在文件夹ID
	private int type;// 类型 : 0-文件夹,1-文件
	private String name;// 名称 : (不带路径的部分)
	private Long size;// 大小 : 字节单位,文件大小或文件夹的总大小
	private String ext;// 扩展名 : 仅适用于文件类型
	private String orderNo;// 排序号
	private String path;// 保存路径 : 相对于[NETDISK]目录下的子路径,开头不要带符号/,仅适用于文件类型'
	private int editRole;// 编辑权限 : 0-编辑者可修改,1-只有拥有者可修改

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "SIZE_")
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "EDIT_ROLE")
	public int getEditRole() {
		return editRole;
	}

	public void setEditRole(int editRole) {
		this.editRole = editRole;
	}
}