package cn.edu.zju.isst.ui.main;

import static cn.edu.zju.isst.constant.Constants.NETWORK_NOT_CONNECTED;
import static cn.edu.zju.isst.constant.Constants.STATUS_NOT_LOGIN;
import static cn.edu.zju.isst.constant.Constants.STATUS_REQUEST_SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.edu.zju.isst.R;
import cn.edu.zju.isst.api.JobApi;
import cn.edu.zju.isst.api.JobCategory;
import cn.edu.zju.isst.db.Job;
import cn.edu.zju.isst.db.DataManager;
import cn.edu.zju.isst.exception.ExceptionWeeder;
import cn.edu.zju.isst.exception.HttpErrorWeeder;
import cn.edu.zju.isst.net.CSTResponse;
import cn.edu.zju.isst.net.NetworkConnection;
import cn.edu.zju.isst.net.RequestListener;
import cn.edu.zju.isst.ui.contact.ContactDetailActivity;
import cn.edu.zju.isst.ui.job.JobDetailActivity;
import cn.edu.zju.isst.ui.job.PublishRecommendActivity;
import cn.edu.zju.isst.ui.job.RecommendDetailActivity;
import cn.edu.zju.isst.util.J;
import cn.edu.zju.isst.util.L;
import cn.edu.zju.isst.util.TimeString;

/**
 * 归档列表基类
 * 
 * @author theasir
 * 
 */
public class BaseJobsListFragment extends ListFragment implements
		OnScrollListener {

	private int m_nVisibleLastIndex;
	private int m_nCurrentPage;
	private boolean m_bIsFirstTime;

	private JobCategory m_jobCategory;
	private LoadType m_loadType;
	private final List<Job> m_listAchive = new ArrayList<Job>();
	private Handler m_handlerJobList;
	private JobListAdapter m_adapterJobList;
	private View m_viewContainer;

	private ListView m_lsvJobList;
	
	

	public BaseJobsListFragment() {
		m_nCurrentPage = 1;
		m_bIsFirstTime = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater
	 * , android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.job_list_fragment, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View,
	 * android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initComponent(view);

		if (m_bIsFirstTime) {
			initJobList();
		}

		initHandler();

		setUpAdapter();

		setUpListener();

		if (m_bIsFirstTime) {
			requestData(LoadType.REFRESH);
			m_bIsFirstTime = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.news_list_fragment_ab_menu, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			requestData(LoadType.REFRESH);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		L.i(this.getClass().getName() + " onListItemClick postion = "
				+ position);
		Intent intent = new Intent(getActivity(), JobDetailActivity.class);
		if (m_jobCategory == JobCategory.RECOMMEND) {
			intent = new Intent(getActivity(), RecommendDetailActivity.class);
		}
		intent.putExtra("id", m_listAchive.get(position).getId());
		getActivity().startActivity(intent);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE
				&& m_nVisibleLastIndex == m_adapterJobList.getCount() - 1) {
			requestData(LoadType.LOADMORE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		m_nVisibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	public void setJobCategory(JobCategory jobCategory) {
		m_jobCategory = jobCategory;
	}

	protected void initComponent(View view) {
		
		
		m_lsvJobList = (ListView) view.findViewById(android.R.id.list);
		m_viewContainer = (View) view.findViewById(R.id.job_recommend_imgbtn_container);
//		if(m_jobCategory ==JobCategory.RECOMMEND){
//			m_viewContainer.setVisibility(view.VISIBLE);
//			Button btnPublish =(Button) view.findViewById(R.id.job_recommend_imgbtn);
//			btnPublish.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(getActivity(),
//							PublishRecommendActivity.class);
//					startActivity(intent);
//				}
//			});
//		}
//		else{
			m_viewContainer.setVisibility(view.GONE);
//		}
	}

	/**
	 * 初始化归档列表，若有缓存则读取缓存
	 */
	protected void initJobList() {
		List<Job> dbJobList = getJobList();
		if (!J.isNullOrEmpty(dbJobList)) {
			for (Job job : dbJobList) {
				m_listAchive.add(job);
			}
		}
	}

	protected void initHandler() {
		m_handlerJobList = new Handler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATUS_REQUEST_SUCCESS:
					switch (m_loadType) {
					case REFRESH:
						refresh((JSONObject) msg.obj);
						break;
					case LOADMORE:
						loadMore((JSONObject) msg.obj);
						break;
					default:
						break;
					}
					m_adapterJobList.notifyDataSetChanged();
					break;
				case STATUS_NOT_LOGIN:// TODO
					((NewMainActivity) getActivity()).updateLogin();
					requestData(m_loadType);
					break;
				default:
					((NewMainActivity) getActivity()).dispose(msg);
					break;
				}
			}

		};
	}

	protected void setUpAdapter() {
		m_adapterJobList = new JobListAdapter(getActivity());
		setListAdapter(m_adapterJobList);
	}

	protected void setUpListener() {
		m_lsvJobList.setOnScrollListener(this);
	}

	/**
	 * 刷新列表
	 * 
	 * @param jsonObject
	 *            数据源
	 */
	protected void refresh(JSONObject jsonObject) {
		if (!m_listAchive.isEmpty()) {
			m_listAchive.clear();
		}
		try {
			if (!J.isValidJsonValue("body", jsonObject)) {
				return;
			}
			JSONArray jsonArray = jsonObject.getJSONArray("body");

			for (int i = 0; i < jsonArray.length(); i++) {
				m_listAchive.add(new Job((JSONObject) jsonArray.get(i)));
			}
			syncJobList();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载更多
	 * 
	 * @param jsonObject
	 *            数据源
	 */
	protected void loadMore(JSONObject jsonObject) {
		JSONArray jsonArray;
		try {
			if (!J.isValidJsonValue("body", jsonObject)) {
				return;
			}
			jsonArray = jsonObject.getJSONArray("body");

			for (int i = 0; i < jsonArray.length(); i++) {
				m_listAchive.add(new Job((JSONObject) jsonArray.get(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 请求数据
	 * 
	 * @param type
	 *            加载方式
	 */
	protected void requestData(LoadType type) {
		if (NetworkConnection.isNetworkConnected(getActivity())) {
			m_loadType = type;
			switch (type) {
			case REFRESH:
				JobApi.getJobList(m_jobCategory, 1, 20, null,
						new JobListRequestListener());
				m_nCurrentPage = 1;
				break;
			case LOADMORE:
				JobApi.getJobList(m_jobCategory, ++m_nCurrentPage,
						20, null, new JobListRequestListener());
				break;
			default:
				break;
			}
		} else {
			Message msg = m_handlerJobList.obtainMessage();
			msg.what = NETWORK_NOT_CONNECTED;
			m_handlerJobList.sendMessage(msg);
		}
	}

	protected List<Job> getJobList() {
		return DataManager.getJobList(m_jobCategory);
	}

	protected void syncJobList() {
		DataManager.syncJobList(m_jobCategory, m_listAchive);
	}

	/**
	 * 加载方式枚举类
	 * 
	 * @author theasir
	 * 
	 */
	public enum LoadType {
		REFRESH, LOADMORE;
	}

	/**
	 * 归档列表RequestListener类
	 * 
	 * @author theasir
	 * 
	 */
	public class JobListRequestListener implements RequestListener {

		@Override
		public void onComplete(Object result) {
			Message msg = m_handlerJobList.obtainMessage();
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

			m_handlerJobList.sendMessage(msg);
		}

		@Override
		public void onHttpError(CSTResponse response) {
			L.i(this.getClass().getName() + " onHttpError!");
			Message msg = m_handlerJobList.obtainMessage();
			HttpErrorWeeder.fckHttpError(response, msg);
			m_handlerJobList.sendMessage(msg);
		}

		@Override
		public void onException(Exception e) {
			L.i(this.getClass().getName() + " onException!");
			Message msg = m_handlerJobList.obtainMessage();
			ExceptionWeeder.fckException(e, msg);
			m_handlerJobList.sendMessage(msg);
		}

	}

	/**
	 * View容器类
	 * 
	 * @author theasir
	 * 
	 */
	protected final class ViewHolder {
		public TextView titleTxv;
		public TextView dateTxv;
		public TextView publisherTxv;
		public TextView descriptionTxv;
		public View indicatorView;
	}

	/**
	 * 归档列表自定义适配器类
	 * 
	 * @author theasir
	 * 
	 */
	public class JobListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public JobListAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return m_listAchive.size();
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
						.inflate(R.layout.job_list_item, null);
				holder.titleTxv = (TextView) convertView
						.findViewById(R.id.job_list_item_title_txv);
				holder.dateTxv = (TextView) convertView
						.findViewById(R.id.job_list_item_date_txv);
				holder.publisherTxv = (TextView) convertView
						.findViewById(R.id.job_list_item_publisher_txv);
				holder.descriptionTxv = (TextView) convertView
						.findViewById(R.id.job_list_item_description_txv);
				holder.indicatorView = (View) convertView
						.findViewById(R.id.job_list_item_indicator_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.titleTxv.setText(m_listAchive.get(position).getTitle());
			holder.dateTxv.setText(TimeString.toYMD(m_listAchive.get(position)
					.getUpdatedAt()));
			holder.publisherTxv.setText(m_listAchive.get(position)
					.getPublisher().getName());
			holder.descriptionTxv.setText(m_listAchive.get(position)
					.getDescription());
			// holder.indicatorView.setBackgroundColor(Color.BLUE);

			return convertView;
		}

	}

}
