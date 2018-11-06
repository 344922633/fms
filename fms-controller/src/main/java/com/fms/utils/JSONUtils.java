package com.fms.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handu.apollo.utils.exception.ApolloRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JSON转换
 */
@Component
public class JSONUtils {
	private static ObjectMapper objectMapper=null;
	@Autowired
	public JSONUtils(ObjectMapper objectMapper) {
		JSONUtils.objectMapper=objectMapper;
	}
	/**
	 * 将集合类型json字符串转换为java对象
	 */
	public static <T> T jsonToObject(String json, Class<T> collectionClass, Class<?> elementClass){
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApolloRuntimeException("数据不符合JSON格式");
		}
	}

	/**
	 * 简单json对象序列化为java对象
	 */
	public static <T> T jsonToObject(String json, Class<T> elementClass){
		try {
			return objectMapper.readValue(json, elementClass);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApolloRuntimeException("数据不符合JSON格式");
		}
	}
	/**
	 * 简单java对象转json
	 */
	public static String objectToJson(Object object){
		try {
			return objectMapper.writeValueAsString(object);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApolloRuntimeException("JSON序列化异常");
		}
	}
}
