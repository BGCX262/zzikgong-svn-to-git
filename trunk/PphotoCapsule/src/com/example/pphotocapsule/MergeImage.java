package com.example.pphotocapsule;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MergeImage {

	public Bitmap mergedImage(Bitmap c, Bitmap s) {
		Bitmap cs = null; 
		int width, height = 0; 
		
		width = c.getWidth(); 
		height = c.getHeight() + s.getHeight();	 
 
		cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	
		Canvas comboImage = new Canvas(cs); 
		comboImage.drawBitmap(c, 0f, 0f, null); 
		comboImage.drawBitmap(s, 0f, c.getHeight(), null); 
		 
		return cs;
	}
}
