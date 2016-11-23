package alexandervbarkov.android.practice.imagedownloader;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {
	private Cursor cursor;

	public ImagesAdapter(Cursor cursor) {
		this.cursor = cursor;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MyViewHolder((CardView)LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_image, parent, false));
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		if(!cursor.moveToPosition(position))
			return;
		byte[] image = cursor.getBlob(1);
		holder.setImage(image);
		String title = cursor.getString(2);
		holder.setTitle(title);
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}

	public void changeCursor(Cursor cursor) {
		this.cursor.close();
		this.cursor = cursor;
		notifyDataSetChanged();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		private CardView cardView;

		public MyViewHolder(CardView cardView) {
			super(cardView);
			this.cardView = cardView;
		}

		public void setImage(byte[] image) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
			((ImageView)cardView.findViewById(R.id.iv_image)).setImageBitmap(bitmap);
		}

		public void setTitle(String title) {
			((TextView)cardView.findViewById(R.id.tv_title)).setText(title);
		}
	}
}
