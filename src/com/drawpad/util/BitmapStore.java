package com.drawpad.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.graphics.Bitmap;

public class BitmapStore {
	private static LinkedHashMap<Integer, Bitmap> bmps = new LinkedHashMap<Integer, Bitmap>();
	private static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private void addBitmap(Bitmap btm) {
		if(bitmaps.size()==10){
			bitmaps.remove(0);
		}
		bitmaps.add(btm);
		 Set<Entry<Integer, Bitmap>> entryseSet=bmps.entrySet();
		  for (Entry<Integer, Bitmap> entry:entryseSet) {
			  bmps.remove(entry.getKey());
		  }
	}
	
}
