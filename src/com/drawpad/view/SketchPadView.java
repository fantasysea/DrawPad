package com.drawpad.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.drawpad.interfaces.ISketchPadCallback;
import com.drawpad.interfaces.IUndoCommand;
import com.drawpad.main.MainActivity;
import com.drawpad.main.R;
import com.drawpad.main.R.color;
import com.drawpad.util.ISketchPadTool;
import com.drawpad.util.SketchPadDeco;
import com.drawpad.util.SketchPadPen;

public class SketchPadView extends View implements IUndoCommand {

	public static final int STROKE_NONE = 0;
	public static final int STROKE_PEN = 1;
	public static final int STROKE_DECO = 2;

	public static final int EDIT = 101;
	public static final int DRAWING = 102;
	public static final int IDEL = 103;
	public static final int BEGIN_TOUCH = 105;
	public static int state = IDEL;

	private final static int ANGLE = 201;
	private final static int SIZE = 202;
	private final static int TRANSLATE = 203;
	private boolean m_isDirty = false;
	private boolean m_isSetForeBmp = false;
	private int m_bkColor = Color.argb(255, 0, 255, 255);

	private int m_strokeType = STROKE_PEN;
	private int m_strokeColor = Color.BLACK;
	private int m_penSize = 4;
	private int m_canvasWidth = 100;
	private int m_canvasHeight = 100;
	public boolean m_canClear = true;

	private Paint m_bitmapPaint = null;
	private Bitmap m_foreBitmap = null;
	private Bitmap m_tempBitmap = null;
	private Bitmap m_bkBitmap = null;
	private Bitmap btn_angel;
	private SketchPadUndoStack m_undoStack = null;
	private Canvas m_canvas = null;
	private ISketchPadTool m_curTool = null;
	private ISketchPadCallback m_callback = null;
	private String picPath;
	public float x1;
	private float y1;
	private float x2;
	private float y2;
	private Float angle_Scale = null;
	private Float size_Scale = null;
	private float startx;
	private float starty;
	private Float dx;
	private Float dy;
	private float currentDx;
	private float currentDy;
	private boolean fromPic = false;
	private Matrix matrix = null;
	private String m_bkBitmapPath = null;

	private float newx1;
	private float newx2;
	private float newx3;
	private float newx4;
	private float newy1;
	private float newy2;
	private float newy3;
	private float newy4;

	private float leftX;
	private float leftY;
	private float rightX;
	private float rightY;
	private float topX;
	private float topY;
	private float bottomX;
	private float bottomY;
	private boolean haveSaved = true;
	private float[] fs = new float[] { newx1, newy1, newx2, newy2, newx3,newy3, newx4, newy4 };
	public static AssetManager am;
	public ArrayList<ISketchPadTool> currentObjs = new ArrayList<ISketchPadTool>();
	public static int a = 1;

	public SketchPadView(Context context) {
		this(context, null);
	}

	public SketchPadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (am == null) {
			am = context.getAssets();
		}
		initialize();
	}

	public Bitmap getForeBitmap() {
		return m_foreBitmap;
	}

	public void setBkBitmap(String path) {
		if (m_bkBitmapPath != path) {
			if (m_bkBitmap != null) {
				m_bkBitmap.recycle();
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
			if (path.contains("rakugaki")) {
				InputStream stream = null;
				try {
					stream = am.open(path);
					m_bkBitmap = BitmapFactory.decodeStream(stream, null, opts);

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					stream = null;
				}
			} else {
				FileInputStream stream = null;
				try {
					stream = new FileInputStream(new File(path));
					m_bkBitmap = BitmapFactory.decodeStream(stream, null, opts);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					stream = null;
				}
			}
			invalidate();
		}
	}

	public void setDeco(String path) {
		this.picPath = path;
	}

	public Bitmap getBkBitmap() {
		return m_bkBitmap;
	}

	public void setStrokeType(int type) {
		switch (type) {
		case STROKE_PEN:
			m_curTool = new SketchPadPen(m_penSize, m_strokeColor);
			break;

		case STROKE_DECO:
			m_curTool = new SketchPadDeco(picPath);
			break;
		}
		m_strokeType = type;
	}

	public void setStrokeSize(int size, int type) {
		switch (type) {
		case STROKE_PEN:
			m_penSize = size;
			break;
		}
	}

	public void setStrokeColor(int color) {
		m_strokeColor = color;
	}

	public int getStrokeSize() {
		return m_penSize;
	}

	public int getStrokeColor() {
		return m_strokeColor;
	}

	public void clearAllStrokes() {
		if (m_canClear) {
			m_undoStack.clearAll();
			createStrokeBitmap(m_canvasWidth, m_canvasHeight);
			invalidate();
			m_isDirty = true;
			m_canClear = false;
		}
	}

	public void setCallback(ISketchPadCallback callback) {
		m_callback = callback;
	}

	public ISketchPadCallback getCallback() {
		return m_callback;
	}

	public void onDeleteFromRedoStack() {
	}

	public void onDeleteFromUndoStack() {
	}

	public void undo() {
		if (null != m_undoStack) {
			m_undoStack.undo();
		}
	}

	public boolean canUndo() {
		if (null != m_undoStack) {
			return m_undoStack.canUndo();
		}

		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			if (state == IDEL) {
				angle_Scale = null;
				size_Scale = null;
				dx = null;
				dy = null;
				matrix = null;
				currentDx = 0;
				currentDy = 0;
				
				state = BEGIN_TOUCH;
				m_curTool = null;
				setStrokeType(m_strokeType);
				m_curTool.touchDown(event.getX(), event.getY());
				m_callback.onTouchDown(this, event);
				invalidate();
			} else if (state == DRAWING) {
				setStrokeType(m_strokeType);
				m_curTool.touchDown(event.getX(), event.getY());
				m_callback.onTouchDown(this, event);
				invalidate();
			} else if (state == EDIT) {
				if (contains(x, y)) {
					startx = event.getX();
					starty = event.getY();
				} else {
					state = IDEL;
					setStrokeType(m_strokeType);
					m_callback.onTouchDown(this, event);
					invalidate();
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			if (state == IDEL) {
				
			}else if (state == BEGIN_TOUCH) {
				m_curTool.touchMove(event.getX(), event.getY());
				invalidate();
			}else if (state == DRAWING) {
				m_curTool.touchMove(event.getX(), event.getY());
				invalidate();
			} else if (state == EDIT) {
				dx = null;
				dy = null;
				dx = event.getX() - startx;
				dy = event.getY() - starty;
				currentDx += dx;
				currentDy += dy;
				startx = event.getX();
				starty = event.getY();
				size_or_angle(TRANSLATE);
			}
			break;

		case MotionEvent.ACTION_UP:
			if (state == IDEL) {
				
			}else if(state == BEGIN_TOUCH){
				if (m_curTool.hasDraw()) {
					if (m_tempBitmap != null) {
						m_tempBitmap.recycle();
						m_tempBitmap = null;
					}
					m_tempBitmap = m_foreBitmap.copy(Config.ARGB_4444, true);
					currentObjs.clear();
					currentObjs.add(m_curTool);
					MainActivity.canUndo = true;
					haveSaved = false;
					m_curTool.touchUp(event.getX(), event.getY());
					m_curTool.draw(m_canvas);
					m_isDirty = true;
					m_canClear = true;
					state = DRAWING;
				}else{
					state = IDEL;
				}
				m_callback.onTouchUp(this, event);
				invalidate();
			}else if (state == DRAWING) {
				if (m_curTool.hasDraw()) {
					currentObjs.add(m_curTool);
					m_curTool.touchUp(event.getX(), event.getY());
					m_curTool.draw(m_canvas);
				}
				m_callback.onTouchUp(this, event);
				invalidate();
			} else if (state == EDIT) {

			}
			break;
		}

		return true;
	}

	private boolean contains(float x, float y) {
		if (currentDx == 0 && angle_Scale == null && size_Scale == null) {
			return x > x1 && x < x2 && y > y1 && y < y2 ? true : false;
		} else {
			if (angle_Scale == null) {
				return x > fs[0] && x < fs[4] && y > fs[1] && y < fs[5] ? true
						: false;
			} else if (angle_Scale == 180 || angle_Scale == -180) {
				return x > fs[4] && x < fs[0] && y > fs[5] && y < fs[1] ? true
						: false;
			} else if (angle_Scale == 90) {
				return x > fs[6] && x < fs[2] && y > fs[7] && y < fs[3] ? true
						: false;
			} else if (angle_Scale == -90) {
				return x > fs[2] && x < fs[6] && y > fs[3] && y < fs[7] ? true
						: false;
			} else {
				ArrayList<Float> listx = new ArrayList<Float>();
				listx.add(fs[0]);
				listx.add(fs[2]);
				listx.add(fs[4]);
				listx.add(fs[6]);
				ArrayList<Float> listy = new ArrayList<Float>();
				listy.add(fs[1]);
				listy.add(fs[3]);
				listy.add(fs[5]);
				listy.add(fs[7]);

				leftX = listx.get(0);
				for (int i = 1; i < listx.size(); i++) {
					if (leftX > listx.get(i)) {
						leftX = listx.get(i);
					}
				}
				leftY = listy.get(listx.indexOf(leftX));

				rightX = listx.get(0);
				for (int i = 1; i < listx.size(); i++) {
					if (rightX < listx.get(i)) {
						rightX = listx.get(i);
					}
				}
				rightY = listy.get(listx.indexOf(rightX));

				topY = listy.get(0);
				for (int i = 1; i < listy.size(); i++) {
					if (topY > listy.get(i)) {
						topY = listy.get(i);
					}
				}
				topX = listx.get(listy.indexOf(topY));

				bottomY = listy.get(0);
				for (int i = 1; i < listy.size(); i++) {
					if (bottomY < listy.get(i)) {
						bottomY = listy.get(i);
					}
				}
				bottomX = listx.get(listy.indexOf(bottomY));

				float slope1 = (topX - leftX) / (leftY - topY);
				float slope2 = (rightX - topX) / (rightY - topY);
				float slope3 = (rightX - bottomX) / (bottomY - rightY);
				float slope4 = (bottomX - leftX) / (bottomY - leftY);
				if (x < leftX || x > rightX || y < topY || y > bottomY) {
					return false;
				} else {
					if ((x - leftX) / slope1 - leftY + y >= 0
							&& (rightX - x) / slope2 - rightY + y >= 0
							&& (x - bottomX) / slope3 - bottomY + y <= 0
							&& (bottomX - x) / slope4 - bottomY + y <= 0) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}

	protected void setCanvasSize(int width, int height) {
		if (width > 0 && height > 0) {
			if (m_canvasWidth != width || m_canvasHeight != height) {
				m_canvasWidth = width;
				m_canvasHeight = height;

				createStrokeBitmap(m_canvasWidth, m_canvasHeight);
			}
		}
	}

	protected void onDraw(Canvas canvas) {

		if (null != m_bkBitmap) {
			RectF dst = new RectF(getLeft(), getTop(), getRight(), getBottom());
			Rect rst = new Rect(0, 0, m_bkBitmap.getWidth(),
					m_bkBitmap.getHeight());
			canvas.drawBitmap(m_bkBitmap, rst, dst, m_bitmapPaint);
		}

		if (null != m_foreBitmap) {
			canvas.drawBitmap(m_foreBitmap, 0, 0, m_bitmapPaint);
		}

		if (state == DRAWING && null != m_curTool && m_curTool.hasDraw()||state == BEGIN_TOUCH && null != m_curTool && m_curTool.hasDraw()) {
			m_curTool.draw(canvas);
		}
		if (state == EDIT) {
			canvas.save();
			if (angle_Scale != null || size_Scale != null || currentDx != 0) {

				matrix = new Matrix();
				matrix.reset();
				if (angle_Scale != null) {
					matrix.postRotate(angle_Scale, (x1 + x2) / 2, (y1 + y2) / 2);
				}
				if (size_Scale != null) {
					matrix.postScale(size_Scale, size_Scale, (x1 + x2) / 2,
							(y1 + y2) / 2);
				}
				if (dx != null) {
					matrix.postTranslate(currentDx, currentDy);
				}
			}

			Paint mPaint = new Paint();
			mPaint.setColor(color.graffiti_rect);
			mPaint.setStyle(Style.FILL);
			if (matrix != null) {
				canvas.concat(matrix);
				matrix.mapPoints(fs, new float[] { x1, y1, x2, y1, x2, y2, x1,
						y2 });
			}
			canvas.drawLine(x1, y1, x1, y2, mPaint);
			canvas.drawLine(x1, y1, x2, y1, mPaint);
			canvas.drawLine(x1, y2, x2, y2, mPaint);
			canvas.drawLine(x2, y1, x2, y2, mPaint);
			canvas.drawBitmap(btn_angel, x2 - btn_angel.getWidth() / 2, y2
					- btn_angel.getHeight() / 2, null);
			canvas.restore();
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (!m_isSetForeBmp) {
			setCanvasSize(w, h);
		}
		m_canvasWidth = w;
		m_canvasHeight = h;
		m_isSetForeBmp = false;
	}

	protected void initialize() {
		m_canvas = new Canvas();
		m_bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_undoStack = new SketchPadUndoStack(this);
		setStrokeType(STROKE_PEN);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;

		btn_angel = BitmapFactory.decodeResource(getResources(),
				R.drawable.btn_angle, options);
	}

	protected void createStrokeBitmap(int w, int h) {
		m_canvasWidth = w;
		m_canvasHeight = h;
		Bitmap bitmap = Bitmap.createBitmap(m_canvasWidth, m_canvasHeight,
				Bitmap.Config.ARGB_4444);
		if (null != bitmap) {
			if (m_foreBitmap != null) {
				m_foreBitmap.recycle();
				m_foreBitmap = null;
			}
			m_foreBitmap = bitmap;
			m_tempBitmap = m_foreBitmap.copy(Config.ARGB_4444, true);
			m_canvas.setBitmap(m_foreBitmap);
		}
	}

	public void getXY() {
		m_undoStack.getxy();
	}

	public void reDraw() {
		invalidate();
	}

	public void size_or_angle(int type) {
		if (ANGLE == type) {
			if (angle_Scale != null) {
				m_undoStack.editPic(ANGLE);
			}
		} else if (SIZE == type) {
			if (size_Scale != null) {
				m_undoStack.editPic(SIZE);
			}
		} else {
			if (dx != null) {
				m_undoStack.editPic(TRANSLATE);
			}
		}
	}

	public class SketchPadUndoStack {
		private SketchPadView m_sketchPad = null;
		public final static int HOLD_TIME = 1000;

		public SketchPadUndoStack(SketchPadView sketchPad) {
			m_sketchPad = sketchPad;
		}

		public void clearAll() {
			if (m_bkBitmap != null && !fromPic) {
				m_bkBitmap.recycle();
				m_bkBitmap = null;
			}
		}

		public void undo() {
			if (canUndo() && null != m_sketchPad) {
				if (m_tempBitmap != null) {
					if (m_foreBitmap != null) {
						m_foreBitmap.recycle();
						m_foreBitmap = null;
					}
					m_foreBitmap = m_tempBitmap.copy(Config.ARGB_4444, true);
					m_canvas.setBitmap(m_foreBitmap);
				}
				m_sketchPad.invalidate();
			}
		}

		public void getxy() {
			if (canUndo() && null != m_sketchPad) {
				ISketchPadTool effectiveObj = currentObjs.get(currentObjs.size() - 1);
				x1 = effectiveObj.getX1();
				x2 = effectiveObj.getX2();
				y1 = effectiveObj.getY1();
				y2 = effectiveObj.getY2();
				if (effectiveObj.getStartTime() != effectiveObj.getEndTime()) {
					for (int i = currentObjs.size() - 2; i > -1; i--) {
						effectiveObj = currentObjs.get(i);
						x1 = x1 > effectiveObj.getX1() ? effectiveObj.getX1(): x1;
						x2 = x2 < effectiveObj.getX2() ? effectiveObj.getX2(): x2;
						y1 = y1 > effectiveObj.getY1() ? effectiveObj.getY1(): y1;
						y2 = y2 < effectiveObj.getY2() ? effectiveObj.getY2(): y2;
					}
				}
			}
		}

		public void editPic(int type) {

			try {
				if (canUndo() && null != m_sketchPad) {
					ISketchPadTool effectiveObj = currentObjs.get(currentObjs
							.size() - 1);
					if (ANGLE == type) {
						effectiveObj.setRotation(angle_Scale);
					} else if (SIZE == type) {
						effectiveObj.setDeflation(size_Scale);
					} else {
						effectiveObj.setDx(currentDx);
						effectiveObj.setDy(currentDy);
					}
					effectiveObj.setPx((x1 + x2) / 2);
					effectiveObj.setPy((y1 + y2) / 2);
					if (effectiveObj.getStartTime() != effectiveObj
							.getEndTime()) {
						for (int i = currentObjs.size() - 2; i > -1; i--) {
							effectiveObj = currentObjs.get(i);
							if (ANGLE == type) {
								effectiveObj.setRotation(angle_Scale);
							} else if (SIZE == type) {
								effectiveObj.setDeflation(size_Scale);
							} else {
								effectiveObj.setDx(currentDx);
								effectiveObj.setDy(currentDy);
							}
							effectiveObj.setPx((x1 + x2) / 2);
							effectiveObj.setPy((y1 + y2) / 2);
						}
					}
					if (m_tempBitmap != null) {
						if (m_foreBitmap != null) {
							m_foreBitmap.recycle();
							m_foreBitmap = null;
						}
						m_foreBitmap = m_tempBitmap
								.copy(Config.ARGB_4444, true);
					}
					Canvas canvas = m_sketchPad.m_canvas;
					m_canvas.setBitmap(m_foreBitmap);
					for (ISketchPadTool sketchPadTool : currentObjs) {
						sketchPadTool.draw(canvas);
					}
					m_sketchPad.invalidate();
				}
			} catch (Exception e) {
				Log.v("other", "exception", e);
			}
		}

		public boolean canUndo() {
			return (currentObjs.size() > 0);
		}

	}

	public void setRotation_Scale(Float rotation_Scale) {
		this.angle_Scale = rotation_Scale;
	}

	public void setDeflation_Scale(Float deflation_Scale) {
		this.size_Scale = deflation_Scale;
	}

	public ISketchPadTool getM_curTool() {
		return m_curTool;

	}

	public boolean isDirty() {
		return m_isDirty;
	}

	public void setBkColor(int color) {
		if (m_bkColor != color) {
			m_bkColor = color;
			invalidate();
		}
	}

	public void setX1(Float x1) {
		this.x1 = x1;
	}

	public void setY1(Float y1) {
		this.y1 = y1;
	}

	public void setX2(Float x2) {
		this.x2 = x2;
	}

	public void setY2(Float y2) {
		this.y2 = y2;
	}

	public void setFromPic(boolean fromPic) {
		this.fromPic = fromPic;

	}

	public boolean isHaveSaved() {
		return haveSaved;
	}

	public void setHaveSaved(boolean haveSaved) {
		this.haveSaved = haveSaved;
	}

	public void recycle() {
		if (m_foreBitmap != null) {
			m_foreBitmap.recycle();
			m_foreBitmap = null;
		}
		if (m_bkBitmap != null) {
			m_bkBitmap.recycle();
			m_bkBitmap = null;
		}
		if (btn_angel != null) {
			btn_angel.recycle();
			btn_angel = null;
		}
		if (m_tempBitmap != null) {
			m_tempBitmap.recycle();
			m_tempBitmap = null;
		}
		m_canvas = null;
		m_bitmapPaint = null;
		m_undoStack = null;
		m_bkBitmap = null;
		m_canvas = null;
	}
}
