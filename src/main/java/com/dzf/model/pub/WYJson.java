package com.dzf.model.pub;

/**
 * 
 * JSON模型
 * 
 * 用户后台向前台返回的JSON对象
 * 
 * 
 */
@SuppressWarnings("serial")
public class WYJson implements java.io.Serializable {

//	private boolean success = false;
	
//	private String status = "200";

	private String message = "";
	
	private String code = "200";

	private Object rows = null;

	private Object response = null;
	
	private Object childs = null;
	
	private Object head = null;

	/*public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
*/

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getRows() {
		return rows;
	}

	public void setRows(Object rows) {
		this.rows = rows;
	}


	public Object getChilds() {
		return childs;
	}

	public void setChilds(Object childs) {
		this.childs = childs;
	}

	public Object getHead() {
		return head;
	}

	public void setHead(Object head) {
		this.head = head;
	}

//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}


}
