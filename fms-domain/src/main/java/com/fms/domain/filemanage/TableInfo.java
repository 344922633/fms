package com.fms.domain.filemanage;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class TableInfo {
	private String table_name;
	private List<Map<String,String>> columnInfo;

	public String getTable_name() {
		return table_name;
	}

	public TableInfo setTable_name(String table_name) {
		this.table_name = table_name;
		return this;
	}

	public List<Map<String, String>> getColumnInfo() {
		return columnInfo;
	}

	public TableInfo setColumnInfo(List<Map<String, String>> columnInfo) {
		this.columnInfo = columnInfo;
		return this;
	}
}
