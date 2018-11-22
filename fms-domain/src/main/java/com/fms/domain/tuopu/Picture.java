package com.fms.domain.tuopu;
import com.handu.apollo.base.TreeVo;
import lombok.Data;

@Data
public class Picture {
	private Long id;
	private String name;
	private String json;
	private int userId;
	private String time;

}
