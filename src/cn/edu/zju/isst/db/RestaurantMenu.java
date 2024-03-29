/**
 * 
 */
package cn.edu.zju.isst.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.zju.isst.util.J;

/**
 * @author theasir
 * 
 */
public class RestaurantMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6209904585992915175L;

	/**
	 * 以下字段详见服务器接口文档
	 */
	private String name;
	private String picture;
	private String description;
	private float price;

	/**
	 * 默认值初始化并更新
	 * 
	 * @param jsonObject
	 *            数据源
	 * @throws JSONException
	 *             未处理异常
	 */
	public RestaurantMenu(JSONObject jsonObject) throws JSONException {
		name = "";
		price = 0;
		update(jsonObject);
	}

	/**
	 * 更新数据，强制判断设计
	 * 
	 * @param jsonObject
	 *            数据源
	 * @throws JSONException
	 *             未处理异常
	 */
	public void update(JSONObject jsonObject) throws JSONException {
		if (!J.isNullOrEmpty(jsonObject)) {
			if (J.isValidJsonValue("name", jsonObject)) {
				name = jsonObject.getString("name");
			}
			if (J.isValidJsonValue("price", jsonObject)) {
				price = (float) jsonObject.getDouble("price");
			}
			if (J.isValidJsonValue("picture", jsonObject)) {
				picture = jsonObject.getString("picture");
			} else {
				description = "";
			}
			if (J.isValidJsonValue("description", jsonObject)) {
				description = jsonObject.getString("description");
			} else {
				description = "";
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}
}
