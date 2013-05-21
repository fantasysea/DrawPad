package com.drawpad.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.drawpad.interfaces.ISketchPadCallback;
import com.drawpad.view.QuickAction;
import com.drawpad.view.SketchPadView;

public class MainActivity extends Activity implements OnClickListener,ISketchPadCallback{
    private QuickAction quickAction;
	private Integer mForegroundColor;
	private int mPenSize ; 
	private SketchPadView sketchPad;
	private int[][] colorTbl = { 
			{ R.id.color01, 0xFFFF0000 }, { R.id.color02, 0xFFFF9900 }, { R.id.color03, 0xFFFFCC00 },
			{ R.id.color04, 0xFFFFFF00 }, { R.id.color05, 0xFF99FF33 }, { R.id.color06, 0xFFFFFFFF }, 
			{ R.id.color07, 0xFF0066FF }, { R.id.color08, 0xFF0099FF }, { R.id.color09, 0xFF00CCCC },
			{ R.id.color10, 0xFF00CC00 }, { R.id.color11, 0xFF66CC00 }, { R.id.color12, 0xFF999999 },
			{ R.id.color13, 0xFFFFB3B3 }, { R.id.color14, 0xFFFFCCB3 }, { R.id.color15, 0xFFFFE6B3 },
			};
	int[][] sizeTbls = {{R.id.pen01,10},
			{R.id.pen02,15},
			{R.id.pen03,20},
			{R.id.pen04,25},
			{R.id.pen05,30}};
	public static boolean canUndo = false; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        quickAction = new QuickAction(this, QuickAction.VERTICAL);
        findViewById(R.id.palette).setOnClickListener(this);
        findViewById(R.id.pen).setOnClickListener(this);
        findViewById(R.id.eraser).setOnClickListener(this);
        findViewById(R.id.undo).setOnClickListener(this);
        quickAction.setOnActionItemClickListener(pentoolCl);
		sketchPad = (SketchPadView) this.findViewById(R.id.sketchpad);
		sketchPad.setCallback(this);
    }

	@Override
	public void onClick(View v) {	
		quickAction.show(v);
	}
	
	private OnClickListener pentoolCl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.color01:
			case R.id.color02:
			case R.id.color03:
			case R.id.color04:
			case R.id.color05:
			case R.id.color06:
			case R.id.color07:
			case R.id.color08:
			case R.id.color09:
			case R.id.color10:
			case R.id.color11:
			case R.id.color12:
			case R.id.color13:
			case R.id.color14:
			case R.id.color15:
				Toast.makeText(getApplicationContext(), "Let's do some search action", Toast.LENGTH_SHORT).show();
				mForegroundColor = getColor(v.getId());
				sketchPad.setStrokeColor(mForegroundColor);
				sketchPad.setStrokeType(SketchPadView.STROKE_PEN);
				break;
			case R.id.pen01:
			case R.id.pen02:
			case R.id.pen03:
			case R.id.pen04:
			case R.id.pen05:

				mPenSize = getSize(v.getId());
				if(mForegroundColor!=null){
					sketchPad.setStrokeColor(mForegroundColor);
				}
				sketchPad.setStrokeType(SketchPadView.STROKE_PEN);
				sketchPad.setStrokeSize(mPenSize, SketchPadView.STROKE_PEN);
				break;
			}

		}
	};
	private int getColor(int id) {
		int len = colorTbl.length;
		for (int i = 0; i < len; i++) {
			if (colorTbl[i][0] == id) {
				return colorTbl[i][1];
			}
		}
		return 0x00000000;
	}
	private int getSize(int id) {
		int len = sizeTbls.length;
		for (int i = 0; i < len; i++) {
			if (sizeTbls[i][0] == id) {
				return sizeTbls[i][1];
			}
		}

		return -1;
	}

	@Override
	public void onTouchDown(SketchPadView obj, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchUp(SketchPadView obj, MotionEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy(SketchPadView obj) {
		// TODO Auto-generated method stub
		
	}
}