package com.example.photocapsule;

import android.graphics.Bitmap;

public class MergeImage {
	public Bitmap combineImages(Bitmap c, Bitmap s) {
		Bitmap cs = null;
		int width, height = 0;
		
		width = c.getWidth() + s.getWidth();
		height = c.getHeight();
		cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		return cs;
	}
}
