package com.jason.property;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class TicketFragment extends ListFragment {

	private TextView mTxtStartDate;

	private Button mBtnQuery;

	private DatePickerDialog mDatePickerDialog;

	private Calendar mCalendar = Calendar.getInstance();

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ticket_layout,
				container, false);
		mTxtStartDate = (TextView) view.findViewById(R.id.txt_start_date);
		mTxtStartDate.setOnClickListener(new OnStartDateClickListener());
		mBtnQuery = (Button) view.findViewById(R.id.btn_query);
		mBtnQuery.setOnClickListener(mOnBtnQueryClickListener);
		return view;
	}

	private class OnStartDateClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mDatePickerDialog.show();
		}
	}

	private OnClickListener mOnBtnQueryClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			getListView().setAdapter(new TicketAdapter());
		}
	};

	private DatePickerDialog.OnDateSetListener mDateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCalendar.set(year, monthOfYear, dayOfMonth);
			mTxtStartDate.setText(mDateFormat.format(mCalendar.getTime()));
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDatePickerDialog = new DatePickerDialog(getActivity(), mDateListener,
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
	}

	private class TicketAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
