package alexandervbarkov.android.bnr.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

public abstract class FragmentVisible extends Fragment {
	private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Toast.makeText(getActivity(), "Canceling notification", Toast.LENGTH_LONG).show();
			setResultCode(Activity.RESULT_CANCELED);
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
		IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
		getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		getActivity().unregisterReceiver(mOnShowNotification);
	}
}