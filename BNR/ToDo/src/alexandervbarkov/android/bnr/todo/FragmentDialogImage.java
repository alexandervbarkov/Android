package alexandervbarkov.android.bnr.todo;

import alexandervbarkov.android.bnr.todo.R;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FragmentDialogImage extends DialogFragment {
	private ImageView mIvImage;
	private ImageButton mIbntDelete;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_image, parent, false);
		
		mIvImage = (ImageView)v.findViewById(R.id.iv_image);
		String path = (String)getArguments().getSerializable(FragmentTask.EXTRA_IMAGE_PATH);
		BitmapDrawable image;
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		image = UtilsImage.getScaledImage(getActivity(), path, dm.widthPixels, dm.heightPixels);
		mIvImage.setImageDrawable(image);	
		
		mIbntDelete = (ImageButton)v.findViewById(R.id.ibtn_image_delete);
		mIbntDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
				dismiss();
			}
		});
		
		return v;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		UtilsImage.cleanImageView(mIvImage);
	}
	
	public static FragmentDialogImage newInstance(String path) {
		Bundle args = new Bundle();
		args.putSerializable(FragmentTask.EXTRA_IMAGE_PATH, path);
		FragmentDialogImage f = new FragmentDialogImage();
		f.setArguments(args);
		f.setStyle(STYLE_NO_TITLE, 0);
		return f;
	}
}