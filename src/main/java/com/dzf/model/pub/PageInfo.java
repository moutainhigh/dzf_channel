package com.dzf.model.pub;

public class PageInfo<T> {
	
	private T result;
	
	private int pagesize;
	
	private int pagenum;
	
	private long total;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	
	

}
