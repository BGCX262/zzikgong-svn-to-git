package com.example.photocapsule;

import android.graphics.Bitmap;

public class DivideImage {
	public static Bitmap croppedBmpLeft;
	public static Bitmap croppedBmpright;
	
	public void segmentImage(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int halfWidth = width / 2;
		
		croppedBmpLeft = Bitmap.createBitmap(bitmap, 0, 0, halfWidth, height);
		croppedBmpright = Bitmap.createBitmap(bitmap, halfWidth, 0, halfWidth, height);
	}
}
