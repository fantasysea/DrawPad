package com.drawpad.view;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;

import com.drawpad.main.R;

public class QuickAction extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private LinearLayout mScroller;
	private OnClickListener mItemClickListener;
	private OnDismissListener mDismissListener;

	private boolean mDidAction;

	private int mAnimStyle;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;
	
	public static final int ACTION_PALETTE = R.id.palette;
	public static final int ACTION_PEN = R.id.pen;
	public static final int ACTION_ERASER = R.id.eraser;
	public static final int ACTION_UNDO = R.id.undo;

	public QuickAction(Context context) {
		this(context, VERTICAL);
	}

	public QuickAction(Context context, int orientation) {
		super(context);
		
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mAnimStyle = ANIM_AUTO;
	}

	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
		mScroller = (LinearLayout) mRootView.findViewById(R.id.scroller);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}

	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	public void setOnActionItemClickListener(OnClickListener listener) {
		mItemClickListener = listener;
	}

	public void show(View anchor) {
		
		int id = anchor.getId();
		switch (id) {
		case ACTION_PALETTE:
			setRootViewId(R.layout.palette);
			break;
		case ACTION_PEN:
			setRootViewId(R.layout.pen);
			break;
		case ACTION_ERASER:
			setRootViewId(R.layout.eraser);
			break;
		case ACTION_UNDO:
			setRootViewId(R.layout.undo);
			break;
		}
		setonclicklister(anchor);
		preShow();
		int xPos, yPos, arrowPos;

		mDidAction = false;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();
		int rootWidth = mRootView.getMeasuredWidth();


		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		if ((anchorRect.centerX() + rootWidth / 2) > screenWidth) {
			xPos = anchorRect.centerX() - rootWidth / 2 - (anchorRect.centerX() + rootWidth / 2 - screenWidth);
			xPos = (xPos < 0) ? 0 : xPos;
			arrowPos = anchorRect.centerX() - xPos;

		} else {
			xPos = anchorRect.centerX() - (rootWidth / 2);
			xPos = (xPos < 0) ? 0 : xPos;
			arrowPos = anchorRect.centerX() - xPos;
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = 15;
				LayoutParams l = mScroller.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = mScroller.getLayoutParams();
				l.height = dyBottom;
			}
		}

		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void setonclicklister(View anchor) {
		int id = anchor.getId();
		switch (id) {
		case ACTION_PALETTE:
			mRootView.findViewById(R.id.color01).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color02).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color03).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color04).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color05).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color06).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color07).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color08).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color09).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color10).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color11).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color12).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color13).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color14).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.color15).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.pen01).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.pen02).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.pen03).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.pen04).setOnClickListener(mItemClickListener);
			mRootView.findViewById(R.id.pen05).setOnClickListener(mItemClickListener);
			break;
		case ACTION_PEN:
			
			break;
		case ACTION_ERASER:
			
			break;
		case ACTION_UNDO:
			
			break;
		}
		
	}

	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

		final int arrowWidth = mArrowUp.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

		hideArrow.setVisibility(View.INVISIBLE);
	}

	public void setOnDismissListener(QuickAction.OnDismissListener listener) {
		setOnDismissListener(this);
		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	public interface OnDismissListener {
		public abstract void onDismiss();
	}
	
	
}