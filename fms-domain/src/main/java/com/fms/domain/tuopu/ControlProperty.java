package com.fms.domain.tuopu;

import java.math.BigDecimal;
import com.handu.apollo.base.TreeVo;
import lombok.Data;

@Data
public class ControlProperty {
	private long id;
	private String propertyChinese;
	private String propertyEnglish;
	private String controlId;
	private String valueDataType;
	private String dicName;
	private String dicInfo;
	private int isDic;
}
