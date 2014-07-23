/**
 * 
 */
package cn.edu.zju.isst.db;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.zju.isst.util.J;

/**
 * 归档解析类
 * 
 * @author theasir
 * 
 */
public class Archive implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3086117832107474161L;

    /**
     * 以下字段详见服务器接口文档
     */
    private int id;
    private String title;
    private String description;
    private long updatedAt;
    private int publisherId;
    private Publisher publisher;
    private String content;

    /**
     * 默认值初始化并更新
     * 
     * @param jsonObject
     *            数据源
     * @throws JSONException
     *             未处理异常
     */
    public Archive(JSONObject jsonObject) throws JSONException {
	id = -1;
	title = "";
	description = "";
	updatedAt = 0;
	publisher = new Publisher(new JSONObject("{}"));// TODO 怎样初始化比较好？
	publisherId = 0;
	content = "";
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
	    if (J.isValidJsonValue("id", jsonObject)) {
		id = jsonObject.getInt("id");
	    }
	    if (J.isValidJsonValue("title", jsonObject)) {
		title = jsonObject.getString("title");
	    }
	    if (J.isValidJsonValue("description", jsonObject)) {
		description = jsonObject.getString("description");
	    }
	    if (J.isValidJsonValue("updatedAt", jsonObject)) {
		updatedAt = jsonObject.getLong("updatedAt");
	    }
	    if (J.isValidJsonValue("user", jsonObject)) {
		publisher = new Publisher(jsonObject.getJSONObject("user"));
	    }
	    if (J.isValidJsonValue("userId", jsonObject)) {
		publisherId = jsonObject.getInt("userId");
	    } else {
		publisherId = publisher.getId();
	    }
	    if (J.isValidJsonValue("content", jsonObject)) {
		content = jsonObject.getString("content");
	    }
	}
    }

    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * @return the updateAt
     */
    public long getUpdatedAt() {
	return updatedAt;
    }

    /**
     * @return the publisherId
     */
    public int getPublisherId() {
	return publisherId;
    }

    /**
     * @return the publisher
     */
    public Publisher getPublisher() {
	return publisher;
    }

    /**
     * @return the content
     */
    public String getContent() {
	return content;
    }

}
