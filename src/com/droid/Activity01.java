package com.droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.droid.Activity02.LocateIn;
import com.droid.MyLetterListView.OnTouchingLetterChangedListener;

public class Activity01 extends Activity {
	private BaseAdapter adapter;
	private ListView personList;
	private TextView overlay; // �Ի�������ĸtextview
	private MyLetterListView letterListView; // A-Z listview
	private HashMap<String, Integer> alphaIndexer;// ��Ŵ��ڵĺ���ƴ������ĸ����֮��Ӧ���б�λ��
	private String[] sections;// ��Ŵ��ڵĺ���ƴ������ĸ
	private Handler handler;
	private OverlayThread overlayThread; // ��ʾ����ĸ�Ի���
	private ArrayList<City> allCity_lists; // ���г����б�
	private ArrayList<City> city_lists;// �����б�
	ListAdapter.TopViewHolder topViewHolder;
	private String lngCityName = "���ڶ�λ����λ��..";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		personList = (ListView) findViewById(R.id.list_view);
		allCity_lists = new ArrayList<City>();
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		Activity02.setLocateIn(new GetCityName());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		personList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
		personList.setAdapter(adapter);
		initOverlay();
		hotCityInit();
		setAdapter(allCity_lists);
	}

	/**
	 * ���ų���
	 */
	public void hotCityInit() {
		City city = new City("", "-");   
		allCity_lists.add(city);
		city = new City("", "-");
		allCity_lists.add(city);
		city = new City("�Ϻ�", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city = new City("�人", "");
		allCity_lists.add(city);
		city = new City("���", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city = new City("�Ͼ�", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city = new City("�ɶ�", "");
		allCity_lists.add(city);
		city = new City("����", "");
		allCity_lists.add(city);
		city_lists = getCityList();
		allCity_lists.addAll(city_lists);
	}

	private ArrayList<City> getCityList() {
		DBHelper dbHelper = new DBHelper(this);
		ArrayList<City> list = new ArrayList<City>();
		try {
			dbHelper.createDataBase();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from city", null);
			City city;
			while (cursor.moveToNext()) {
				city = new City(cursor.getString(1), cursor.getString(2));
				list.add(city);
			}
			cursor.close();
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * a-z����
	 */
	Comparator comparator = new Comparator<City>() {
		@Override
		public int compare(City lhs, City rhs) {
			String a = lhs.getPinyi().substring(0, 1);
			String b = rhs.getPinyi().substring(0, 1);
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}

		}
	};

	private void setAdapter(List<City> list) {
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);
	}

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<City> list;
		final int VIEW_TYPE = 3;

		public ListAdapter(Context context, List<City> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// ��ǰ����ƴ������ĸ
				String currentStr = getAlpha(list.get(i).getPinyi());
				// ��һ������ƴ������ĸ�����������Ϊ�� ��
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getPinyi()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getPinyi());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			int type = 0;
			if (position == 0) {
				type = 2;
			} else if (position == 1) {
				type = 1;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {// ������Ҫ������Ҫ���в������ͣ��ܴ�СΪ���͵��������±�
			return VIEW_TYPE;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int viewType = getItemViewType(position);
			if (viewType == 1) {
				if (convertView == null) {
					topViewHolder = new TopViewHolder();
					convertView = inflater.inflate(R.layout.frist_list_item,
							null);
					topViewHolder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					topViewHolder.name = (TextView) convertView
							.findViewById(R.id.lng_city);
					convertView.setTag(topViewHolder);
				} else {
					topViewHolder = (TopViewHolder) convertView.getTag();
				}

				topViewHolder.name.setText(lngCityName);
				topViewHolder.alpha.setVisibility(View.VISIBLE);
				topViewHolder.alpha.setText("��λ����");

			} else if (viewType == 2) {
				final ShViewHolder shViewHolder;
				if (convertView == null) {
					shViewHolder = new ShViewHolder();
					convertView = inflater.inflate(R.layout.search_item, null);
					shViewHolder.editText = (EditText) convertView
							.findViewById(R.id.sh);
					convertView.setTag(shViewHolder);
				} else {
					shViewHolder = (ShViewHolder) convertView.getTag();
				}
			} else {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.list_item, null);
					holder = new ViewHolder();
					holder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (position >= 1) {
					holder.name.setText(list.get(position).getName());
					String currentStr = getAlpha(list.get(position).getPinyi());
					String previewStr = (position - 1) >= 0 ? getAlpha(list
							.get(position - 1).getPinyi()) : " ";
					if (!previewStr.equals(currentStr)) {
						holder.alpha.setVisibility(View.VISIBLE);
						if (currentStr.equals("#")) {
							currentStr = "���ų���";
						}
						holder.alpha.setText(currentStr);
					} else {
						holder.alpha.setVisibility(View.GONE);
					}
				}
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha; // ����ĸ����
			TextView name; // ��������
		}

		private class TopViewHolder {
			TextView alpha; // ����ĸ����
			TextView name; // ��������
		}

		private class ShViewHolder {
			EditText editText;

		}
	}

	// ��ʼ������ƴ������ĸ������ʾ��
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// �ӳ�һ���ִ�У���overlayΪ���ɼ�
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// ����overlay���ɼ�
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	// ��ú���ƴ������ĸ
	private String getAlpha(String str) {

		if (str.equals("-")) {
			return "&";
		}
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	private class GetCityName implements LocateIn {
		@Override
		public void getCityName(String name) {
			System.out.println(name);
			if (topViewHolder.name != null) {
				lngCityName = name;
				adapter.notifyDataSetChanged();
			}
		}
	}

}