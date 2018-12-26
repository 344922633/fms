package com.fms.domain.tuopu;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.handu.apollo.base.TreeVo;
import lombok.Data;
import org.omg.CORBA.Object;

@Data
public class ControlProperty {
	private long id;
	private String propertyChinese;
	private String property;
	private String controlId;
	private String dataType;
	private String dicName;
	private int isDic;
	private String dicList;
	private String value;
	private int propertyFlag;

}
