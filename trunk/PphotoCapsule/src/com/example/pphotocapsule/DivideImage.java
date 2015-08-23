package com.example.pphotocapsule;

import android.graphics.Bitmap;

public class DivideImage {
	public static Bitmap croppedBmpLeft;
	public static Bitmap croppedBmpright;
	
	public void segmentImage(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int halfheight = height / 2;
		
		croppedBmpLeft = Bitmap.createBitmap(bitmap, 0, 0, width, halfheight);
		croppedBmpright = Bitmap.createBitmap(bitmap, 0, halfheight, width, halfheight);
	}
}
