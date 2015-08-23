package com.example.photocapsule;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class AlbumActivity extends Activity {
	private Context mContext;
	GridView album_gridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		
		mContext = this;
		final ImageAdapter ia = new ImageAdapter(this);
		album_gridView = (GridView)findViewById(R.id.album_gridView);
		album_gridView.setAdapter(ia);	
		album_gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				ia.callImageViewer(position);
			}
			
		});
	}

	public class ImageAdapter extends BaseAdapter {
		private String imgData;
		private String geoData;
		private ArrayList<String> thumbsDataList;
		private ArrayList<String> thumbsIDList;
		
		ImageAdapter(Context c) {
			mContext = c;
			thumbsDataList = new ArrayList<String>();
			thumbsIDList = new ArrayList<String>();
			getThumbInfo(thumbsIDList, thumbsDataList);
		}
		
		public final void callImageViewer(int selectedIndex) {
			Intent intent_imagepopup = new Intent(mContext, ImagePopupActivity.class);
			String imgPath = getImageInfo(imgData, geoData, thumbsIDList.get(selectedIndex));
			intent_imagepopup.putExtra("filename", imgPath);
			startActivityForResult(intent_imagepopup, 1);
		}
		
		public boolean deleteSelected(int sIndex) {
			return true;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return thumbsIDList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(95, 95));
				imageView.setAdjustViewBounds(false);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(2, 2, 2, 2);
			}
			else {
				imageView = (ImageView)convertView;
			}
			
			BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inSampleSize = 8;
            Bitmap bmp = BitmapFactory.decodeFile(thumbsDataList.get(position), bo);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
            imageView.setImageBitmap(resized);

            
			return imageView;
		}
		
		private void getThumbInfo(ArrayList<String> thumbsIDs, ArrayList<String> thumbsDatas){
            String[] proj = {MediaStore.Images.Media._ID,
                             MediaStore.Images.Media.DATA,
                             MediaStore.Images.Media.DISPLAY_NAME,
                             MediaStore.Images.Media.SIZE};
             
            Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "bucket_display_name='PhotoCapsule'", null, null);
          
            if (imageCursor != null && imageCursor.moveToFirst()){
                String title;
                String thumbsID;
                String thumbsImageID;
                String thumbsData;
                String data;
                String imgSize;
                 
                int thumbsIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
                int thumbsDataCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int thumbsImageIDCol = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int thumbsSizeCol = imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE);
                int num = 0;
                
                do {
                    thumbsID = imageCursor.getString(thumbsIDCol);
                    thumbsData = imageCursor.getString(thumbsDataCol);
                    thumbsImageID = imageCursor.getString(thumbsImageIDCol);
                    imgSize = imageCursor.getString(thumbsSizeCol);
                    num++;
                    if (thumbsImageID != null){
                        thumbsIDs.add(thumbsID);
                        thumbsDatas.add(thumbsData);
                    }
                }while (imageCursor.moveToNext());
                imageCursor.close();
            }
            return;
        }
         
        private String getImageInfo(String ImageData, String Location, String thumbID){
            String imageDataPath = null;
            String[] proj = {MediaStore.Images.Media._ID,
                     MediaStore.Images.Media.DATA,
                     MediaStore.Images.Media.DISPLAY_NAME,
                     MediaStore.Images.Media.SIZE};
   
            Cursor imageCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "bucket_display_name='PhotoCapsule'", null, null);
             
            if (imageCursor != null && imageCursor.moveToFirst()){
                if (imageCursor.getCount() > 0){
                    int imgData = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    imageDataPath = imageCursor.getString(imgData);
                }
            }
            imageCursor.close();
            return imageDataPath;
        }

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album, menu);
		return true;
	}

}
