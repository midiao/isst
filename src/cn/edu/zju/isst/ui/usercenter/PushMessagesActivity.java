/**
 * 
 */
package cn.edu.zju.isst.ui.usercenter;

import static cn.edu.zju.isst.constant.Constants.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.zju.isst.R;
import cn.edu.zju.isst.api.PushMessageApi;
import cn.edu.zju.isst.db.PushMessage;
import cn.edu.zju.isst.net.CSTResponse;
import cn.edu.zju.isst.net.RequestListener;
import cn.edu.zju.isst.ui.main.BaseActivity;
import cn.edu.zju.isst.util.J;
import cn.edu.zju.isst.util.L;
import cn.edu.zju.isst.util.TimeString;

/**
 * @author theasir
 * 
 */
public class PushMessagesActivity extends BaseActivity {

    private List<PushMessage> mMessages = new ArrayList<PushMessage>();
    private Handler mHandler;
    private MsgListAdapter mAdapter;

    private ListView mMsgListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.push_messages_activity);

	setTitle(R.string.message_center);

	setUpActionbar();
	
	initComponent();
	
	initHandler();
	
	setUpAdapter();
	
	requestData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case android.R.id.home:
	    PushMessagesActivity.this.finish();
	    return true;

	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    private void setUpActionbar() {
	ActionBar actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);
	actionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    private void initComponent(){
	mMsgListView = (ListView) findViewById(R.id.push_msg_list);
    }

    private void initHandler() {
	mHandler = new Handler() {

	    @Override
	    public void handleMessage(Message msg) {
		switch (msg.what) {
		case STATUS_REQUEST_SUCCESS:
		    refreshData((JSONObject) msg.obj);
		    mAdapter.notifyDataSetChanged();
		    break;

		default:
		    break;
		}
	    }

	};
    }

    private void setUpAdapter() {
	mAdapter = new MsgListAdapter(PushMessagesActivity.this);
	mMsgListView.setAdapter(mAdapter);
    }

    private void requestData() {
	PushMessageApi.getMsgList(1, 20, new RequestListener() {

	    @Override
	    public void onHttpError(CSTResponse response) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onException(Exception e) {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void onComplete(Object result) {
		Message msg = mHandler.obtainMessage();
		try {
		    if (!J.isValidJsonValue("status", (JSONObject) result)) {
			return;
		    }
		    msg.what = ((JSONObject) result).getInt("status");
		    msg.obj = (JSONObject) result;
		} catch (JSONException e) {
		    L.i(this.getClass().getName() + " onComplete!");
		    e.printStackTrace();
		}

		mHandler.sendMessage(msg);

	    }
	});
    }

    private void refreshData(JSONObject jsonObject) {
	if (!mMessages.isEmpty()) {
	    mMessages.clear();
	}
	try {
	    if (!J.isValidJsonValue("body", jsonObject)) {
		return;
	    }
	    JSONArray jsonArray = jsonObject.getJSONArray("body");

	    for (int i = 0; i < jsonArray.length(); i++) {
		mMessages.add(new PushMessage((JSONObject) jsonArray.get(i)));
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }

    private final class ViewHolder {
	TextView titleTxv;
	TextView contentTxv;
	TextView createdTimeTxv;
    }

    private class MsgListAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	public MsgListAdapter(Context context) {
	    this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
	    return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

	    ViewHolder holder = null;
	    if (convertView == null) {
		holder = new ViewHolder();

		convertView = inflater
			.inflate(R.layout.archive_list_item, null);
		holder.titleTxv = (TextView) convertView
			.findViewById(R.id.archive_list_item_title_txv);
		holder.contentTxv = (TextView) convertView
			.findViewById(R.id.archive_list_item_description_txv);
		holder.createdTimeTxv = (TextView) convertView
			.findViewById(R.id.archive_list_item_date_txv);

		convertView.setTag(holder);
	    } else {
		holder = (ViewHolder) convertView.getTag();
	    }

	    holder.titleTxv.setText(mMessages.get(position).title);
	    holder.createdTimeTxv.setText(TimeString.toYMD(mMessages
		    .get(position).createdTime));
	    holder.contentTxv.setText(mMessages.get(position).content);

	    convertView.findViewById(R.id.archive_list_item_publisher_txv)
		    .setVisibility(View.GONE);

	    return convertView;
	}

    }

}
