package com.dzf.model.pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import com.dzf.pub.CircularlyAccessibleValueObject;
import com.dzf.pub.SuperVO;

public class MultSuperVO extends SuperVO implements IExAggVO {
	private HashMap hmChildVOs = new HashMap();

	public CircularlyAccessibleValueObject[] getAllChildrenVO() {
		ArrayList al = new ArrayList();
		for (int i = 0; i < getTableCodes().length; i++) {
			CircularlyAccessibleValueObject[] cvos = getTableVO(getTableCodes()[i]);
			if (cvos != null) {
				al.addAll((Collection) Arrays.asList(cvos));
			}
		}
		return (SuperVO[]) al.toArray(new SuperVO[0]);
	}

	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {
		return (CircularlyAccessibleValueObject[]) this.hmChildVOs
				.get(tableCode);
	}

	public void setTableVO(String tableCode,
			CircularlyAccessibleValueObject[] vos) {
		this.hmChildVOs.put(tableCode, vos);
	}


	@Override
	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return null;
	}

	@Override
	public CircularlyAccessibleValueObject getParentVO() {
		return null;
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parent) {

	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

	@Override
	public String[] getTableCodes() {
		return null;
	}

	@Override
	public String[] getTableNames() {
		return null;
	}
}
