package com.fms.domain.tuopu;


import com.handu.apollo.base.TreeVo;
import lombok.Data;
import java.util.List;

@Data
public class Control {
	private String id;
	private String name;
	private String type;
	private String image;
	private List<ControlProperty> properties;
	//private String[] uploadFileName;

}
