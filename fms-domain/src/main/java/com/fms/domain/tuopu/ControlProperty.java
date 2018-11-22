package com.fms.domain.tuopu;

import java.math.BigDecimal;
import com.handu.apollo.base.TreeVo;
import lombok.Data;

@Data
public class ControlProperty {
	private long id;
	private String property;
	private String value;
	private String controlId;

}
