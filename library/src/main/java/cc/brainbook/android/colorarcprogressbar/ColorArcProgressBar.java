package cc.brainbook.android.colorarcprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import cc.brainbook.android.colorarcprogressbar.util.Util;

public class ColorArcProgressBar extends View {
    private static final int MIN_WIDTH = 200; //dp
    private static final int MIN_HEIGHT = 200; //dp


    public ColorArcProgressBar(@NonNull Context context) {
        super(context, null);

        initConfig(context, null);
        initView();
    }

    public ColorArcProgressBar(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        initConfig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initConfig(context, attrs);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = getDefaultSize(Math.max(getSuggestedMinimumWidth(), dipToPx(MIN_WIDTH)), widthMeasureSpec);
        final int height = getDefaultSize(Math.max(getSuggestedMinimumHeight(), dipToPx(MIN_WIDTH)), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (width != mWidth || height != mHeight) {
            mWidth = width;
            mHeight = height;

            setView();
        }
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        final float startAngle = 270F - mProgressMaxAngle / 2;  ///180:180; 270:135; 360:90

        //Baseline
        if (isNeedBaseline) {
            canvas.drawArc(mRect, startAngle, mProgressMaxAngle, false, mBaseline);
        }

        //Progressbar
        mRotateMatrix.setRotate(startAngle, mCenterX, mCenterY);
        mSweepGradient.setLocalMatrix(mRotateMatrix);
        mProgressbarPaint.setShader(mSweepGradient);
        //////??????[BUG#drawArc+SweepGradient#起始位置有一个结尾位置的颜色]临时解决：多设置一个颜色、且等于首位的颜色
        canvas.drawArc(mRect, startAngle, mProgressAngle, false, mProgressbarPaint);

        //Scale
        if (isNeedScale) {
            for (int i = 0; i < 40; i++) {
                final float rangeNoScale = (360F - mProgressMaxAngle) / 18F;
                if (i > 20 - rangeNoScale && i < 20 + rangeNoScale) {
                    canvas.rotate(9, mCenterX, mCenterY);
                    continue;
                }

                final float stopY = mCenterY - Math.min(mWidth, mHeight) / 2.0F;
                if (i % 5 == 0) {
                    mScalePaint.setStrokeWidth(dipToPx(2));
                    mScalePaint.setColor(mScaleLongColor);
                    canvas.drawLine(mCenterX, stopY + mScaleLongHeight, mCenterX, stopY, mScalePaint);
                } else {
                    mScalePaint.setStrokeWidth(dipToPx(1.4F));
                    mScalePaint.setColor(mScaleShortColor);
                    canvas.drawLine(mCenterX, stopY + (mScaleLongHeight + mScaleShortHeight) / 2,
                            mCenterX, stopY + (mScaleLongHeight - mScaleShortHeight) / 2, mScalePaint);
                }

                canvas.rotate(9, mCenterX, mCenterY);
            }
        }

        //Title
        if (isNeedTitle) {
            canvas.drawText(mTitle, mCenterX, mCenterY - 2 * mProgressTextSize / 3, mTitlePaint);
        }

        //Subtitle
        if (isNeedSubtitle) {
            canvas.drawText(mSubtitle, mCenterX, mCenterY + 2 * mProgressTextSize / 3, mSubtitlePaint);
        }

        //ProgressText
        if (isNeedProgressText) {
            ///https://stackoverflow.com/questions/7779621/how-to-get-programmatically-a-list-of-colors-from-a-gradient-on-android/7779834
            if (isProgressTextColorFromGradient) {
                mProgressTextPaint.setColor(Util.getColorFromGradient(mProgressbarColors, mGradientPositions, mProgressAngle / 360F));
            }

            canvas.drawText(String.format("%.0f", mProgressValue), mCenterX, mCenterY + mProgressTextSize / 3, mProgressTextPaint);
        }

        invalidate();
    }


    /* -------------- ///[initConfig] -------------- */
    private boolean isNeedBaseline = true;
    public boolean isNeedBaseline() {
        return isNeedBaseline;
    }
    public void setIsNeedBaseline(boolean isNeedBaseline) {
        this.isNeedBaseline = isNeedBaseline;
    }

    private boolean isNeedScale = true;
    public boolean isNeedScale() {
        return isNeedScale;
    }
    public void setIsNeedScale(boolean isNeedScale) {
        this.isNeedScale = isNeedScale;
    }

    private boolean isNeedProgressText = true;
    public boolean isNeedProgressText() {
        return isNeedProgressText;
    }
    public void setIsNeedProgressText(boolean isNeedProgressText) {
        this.isNeedProgressText = isNeedProgressText;
    }

    private boolean isNeedTitle = true;
    public boolean isNeedTitle() {
        return isNeedTitle;
    }
    public void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    private boolean isNeedSubtitle = true;
    public boolean isNeedSubtitle() {
        return isNeedSubtitle;
    }
    public void setIsNeedSubtitle(boolean isNeedSubtitle) {
        this.isNeedSubtitle = isNeedSubtitle;
    }


    private float mProgressMaxAngle = 270F;
    public float getProgressMaxAngle() {
        return mProgressMaxAngle;
    }
    public void setProgressMaxAngle(float progressMaxAngle) {
        mProgressMaxAngle = progressMaxAngle;
        updateGradientPositions();
    }

    private float mProgressMaxValue = 100F;  ///必须大于0！
    public float getProgressMaxValue() {
        return mProgressMaxValue;
    }
    public void setProgressMaxValue(float progressMaxValue) {
        mProgressMaxValue = progressMaxValue <= 0 ? 1F : progressMaxValue;
    }


    private float mProgressbarWidth = dipToPx(10);
    public float getProgressbarWidth() {
        return mProgressbarWidth;
    }
    public void setProgressbarWidth(float progressbarWidth) {
        mProgressbarWidth = progressbarWidth;
    }

    private int[] mProgressbarColors = new int[]{Color.GREEN, Color.YELLOW, Color.RED};
    public int[] getProgressbarColors() {
        return mProgressbarColors;
    }
    public void setProgressbarColors(int[] progressbarColors) {
        mProgressbarColors = progressbarColors;
    }

    private float[] mProgressbarPositions = new float[]{0, 0.5F, 1F};
    public float[] getProgressbarPositions() {
        return mProgressbarPositions;
    }
    public void setProgressbarPositions(float[] progressbarPositions) {
        mProgressbarPositions = progressbarPositions;
        updateGradientPositions();
    }


    private float mBaselineWidth = dipToPx(2);
    public float getBaselineWidth() {
        return mBaselineWidth;
    }
    public void setBaselineWidth(float baselineWidth) {
        mBaselineWidth = baselineWidth;
    }

    private int mBaselineColor = Color.parseColor("#666666");
    public int getBaselineColor() {
        return mBaselineColor;
    }
    public void setBaselineColor(int baselineColor) {
        mBaselineColor = baselineColor;
    }


    private float mScaleLongHeight = dipToPx(13);
    public float getScaleLongHeight() {
        return mScaleLongHeight;
    }
    public void setScaleLongHeight(float scaleLongHeight) {
        mScaleLongHeight = scaleLongHeight;
    }

    private int mScaleLongColor = Color.parseColor("#0000ff");
    public int getScaleLongColor() {
        return mScaleLongColor;
    }
    public void setScaleLongColor(int scaleLongColor) {
        mScaleLongColor = scaleLongColor;
    }

    private float mScaleShortHeight = dipToPx(5);
    public float getScaleShortHeight() {
        return mScaleShortHeight;
    }
    public void setScaleShortHeight(float scaleShortHeight) {
        mScaleShortHeight = scaleShortHeight;
    }

    private int mScaleShortColor = Color.parseColor("#ff0000");
    public int getScaleShortColor() {
        return mScaleShortColor;
    }
    public void setScaleShortColor(int scaleShortColor) {
        mScaleShortColor = scaleShortColor;
    }

    private float mScaleGap = dipToPx(8);
    public float getScaleGap() {
        return mScaleGap;
    }
    public void setScaleGap(float scaleGap) {
        mScaleGap = scaleGap;
    }


    private float mProgressTextSize = dipToPx(60);
    public float getProgressTextSize() {
        return mProgressTextSize;
    }
    public void setProgressTextSize(float progressTextSize) {
        mProgressTextSize = progressTextSize;
    }

    private int mProgressTextColor = Color.BLACK;
    public int getProgressTextColor() {
        return mProgressTextColor;
    }
    public void setProgressTextColor(int progressTextColor) {
        mProgressTextColor = progressTextColor;
    }

    private boolean isProgressTextColorFromGradient = true;
    public boolean isProgressTextColorFromGradient() {
        return isProgressTextColorFromGradient;
    }
    public void setIsProgressTextColorFromGradient(boolean isProgressTextColorFromGradient) {
        this.isProgressTextColorFromGradient = isProgressTextColorFromGradient;
    }

    private String mTitle;
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    private float mTitleTextSize = dipToPx(14);
    public float getTitleTextSize() {
        return mTitleTextSize;
    }
    public void setTitleTextSize(float titleTextSize) {
        mTitleTextSize = titleTextSize;
    }

    private int mTitleColor = Color.parseColor("#676767");
    public int getTitleColor() {
        return mTitleColor;
    }
    public void setTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }

    private String mSubtitle;
    public String getSubtitle() {
        return mSubtitle;
    }
    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    private float mSubtitleTextSize = dipToPx(12);
    public float getSubtitleTextSize() {
        return mSubtitleTextSize;
    }
    public void setSubtitleTextSize(float subtitleTextSize) {
        mSubtitleTextSize = subtitleTextSize;
    }

    private int mSubtitleColor = Color.parseColor("#676767");
    public int getSubtitleColor() {
        return mSubtitleColor;
    }
    public void setSubtitleColor(int subtitleColor) {
        mSubtitleColor = subtitleColor;
    }


    private long mAnimatorDuration = 1000L;
    public long getAnimatorDuration() {
        return mAnimatorDuration;
    }
    public void setAnimatorDuration(long animatorDuration) {
        mAnimatorDuration = animatorDuration;
    }


    private void initConfig(@NonNull Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);

            isNeedBaseline = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_baseline, true);
            isNeedScale = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_scale, true);
            isNeedProgressText = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_progress_text, true);
            isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, true);
            isNeedSubtitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_subtitle, true);

//            mProgressMaxAngle = a.getFloat(R.styleable.ColorArcProgressBar_progress_max_angle, 270F);
            setProgressMaxAngle(a.getFloat(R.styleable.ColorArcProgressBar_progress_max_angle, 270F));

            mProgressMaxValue = a.getFloat(R.styleable.ColorArcProgressBar_progress_max_value, 100F);

            mProgressbarWidth = a.getDimension(R.styleable.ColorArcProgressBar_progressbar_width, dipToPx(10));

            int color1 = a.getColor(R.styleable.ColorArcProgressBar_progressbar_color1, Color.GREEN);
            int color2 = a.getColor(R.styleable.ColorArcProgressBar_progressbar_color2, color1);
            int color3 = a.getColor(R.styleable.ColorArcProgressBar_progressbar_color3, color1);
            mProgressbarColors = new int[]{color1, color2, color3};

            float position1 = a.getFloat(R.styleable.ColorArcProgressBar_progressbar_position1, 0F);
            float position2 = a.getFloat(R.styleable.ColorArcProgressBar_progressbar_position2, 0.5F);
            float position3 = a.getFloat(R.styleable.ColorArcProgressBar_progressbar_position3, 1F);
//            mProgressbarPositions = new float[]{position1, position2, position3};
            setProgressbarPositions(new float[]{position1, position2, position3});

            mBaselineWidth = a.getDimension(R.styleable.ColorArcProgressBar_baseline_width, dipToPx(2));
            mBaselineColor = a.getColor(R.styleable.ColorArcProgressBar_baseline_color, Color.parseColor("#666666"));

            mScaleLongHeight = a.getDimension(R.styleable.ColorArcProgressBar_scale_long_height, dipToPx(13));
            mScaleLongColor = a.getColor(R.styleable.ColorArcProgressBar_scale_long_color, Color.parseColor("#0000ff"));

            mScaleShortHeight = a.getDimension(R.styleable.ColorArcProgressBar_scale_short_height, dipToPx(5));
            mScaleShortColor = a.getColor(R.styleable.ColorArcProgressBar_scale_short_color, Color.parseColor("#ff0000"));

            mScaleGap = a.getDimension(R.styleable.ColorArcProgressBar_scale_gap, dipToPx(8));

            mProgressTextSize = a.getDimension(R.styleable.ColorArcProgressBar_progress_text_size, dipToPx(60));
            mProgressTextColor = a.getColor(R.styleable.ColorArcProgressBar_progress_text_color, Color.BLACK);
            isProgressTextColorFromGradient = a.getBoolean(R.styleable.ColorArcProgressBar_progress_text_color_from_gradient, true);

            mTitle = a.getString(R.styleable.ColorArcProgressBar_title);
            mTitleTextSize = a.getDimension(R.styleable.ColorArcProgressBar_title_text_size, dipToPx(14));
            mTitleColor = a.getColor(R.styleable.ColorArcProgressBar_title_color, Color.parseColor("#676767"));

            mSubtitle = a.getString(R.styleable.ColorArcProgressBar_subtitle);
            mSubtitleTextSize = a.getDimension(R.styleable.ColorArcProgressBar_subtitle_text_size, dipToPx(12));
            mSubtitleColor = a.getColor(R.styleable.ColorArcProgressBar_subtitle_color, Color.parseColor("#676767"));

            mAnimatorDuration = (long) a.getInteger(R.styleable.ColorArcProgressBar_animator_duration, 1000);

            mProgressValue = a.getFloat(R.styleable.ColorArcProgressBar_progress_value, 0F);

            a.recycle();
        }

        if (mProgressValue > 0) {
            setProgressValue(mProgressValue);
        }
    }


    /* -------------- ///[initView] -------------- */
    private final Paint mProgressbarPaint = new Paint();
    private final Paint mBaseline = new Paint();
    private final Paint mScalePaint = new Paint();
    private final Paint mProgressTextPaint = new Paint();
    private final Paint mTitlePaint = new Paint();
    private final Paint mSubtitlePaint = new Paint();

    private final PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0,
            Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private final RectF mRect = new RectF();
    private final Matrix mRotateMatrix = new Matrix();
    private SweepGradient mSweepGradient;

    private float[] mGradientPositions;
    public void updateGradientPositions() {
        mGradientPositions = new float[]{0, mProgressMaxAngle / 360F * mProgressbarPositions[1], mProgressMaxAngle / 360F / mProgressbarPositions[2]};
    }


    private void initView() {
        mProgressbarPaint.setAntiAlias(true);
        mProgressbarPaint.setDither(true);
        mProgressbarPaint.setStyle(Paint.Style.STROKE);
        mProgressbarPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressbarPaint.setStrokeWidth(mProgressbarWidth);
//        mProgressbarPaint.setColor(Color.YELLOW);    //////??????无效！

        if (isNeedBaseline) {
            mBaseline.setAntiAlias(true);
            mBaseline.setDither(true);
            mBaseline.setStyle(Paint.Style.STROKE);
            mBaseline.setStrokeCap(Paint.Cap.ROUND);
            mBaseline.setStrokeWidth(mBaselineWidth);
            mBaseline.setColor(mBaselineColor);
        }

        if (isNeedProgressText) {
            mProgressTextPaint.setTextSize(mProgressTextSize);
            mProgressTextPaint.setColor(mProgressTextColor);
            mProgressTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (isNeedTitle) {
            mTitlePaint.setTextSize(mTitleTextSize);
            mTitlePaint.setColor(mTitleColor);
            mTitlePaint.setTextAlign(Paint.Align.CENTER);
        }

        if (isNeedSubtitle) {
            mSubtitlePaint.setTextSize(mSubtitleTextSize);
            mSubtitlePaint.setColor(mSubtitleColor);
            mSubtitlePaint.setTextAlign(Paint.Align.CENTER);
        }
    }


    /* -------------- ///[setView] -------------- */
    private int mWidth = MIN_WIDTH;
    private int mHeight = MIN_HEIGHT;

    private float mCenterX;
    private float mCenterY;

    private void setView() {
        float radius = (Math.min(mWidth, mHeight) - mProgressbarWidth) / 2 - mScaleLongHeight - mScaleGap;
        mCenterX = (float) mWidth / 2;
        mCenterY = (float) mHeight / 2;

        mRect.top = mCenterY - radius;
        mRect.left = mCenterX - radius;
        mRect.right = mCenterX + radius;
        mRect.bottom = mCenterY + radius;

        ///颜色分布比例 https://blog.csdn.net/u010126792/article/details/85238050
        mSweepGradient = new SweepGradient(mCenterX, mCenterY, mProgressbarColors, mGradientPositions);
//        mSweepGradient = new SweepGradient(mCenterX, mCenterY, mProgressBarColors, null);
    }


    /* -------------- ///[setProgressValue/setAnimation] -------------- */
    private float mProgressValue = 0F;
    public void setProgressValue(float progressValue) {
        if (progressValue > mProgressMaxValue) {
            progressValue = mProgressMaxValue;
        }

        if (progressValue < 0) {
            progressValue = 0;
        }

        mProgressValue = progressValue;
        setAnimation(mProgressAngle, progressValue * mProgressMaxAngle / mProgressMaxValue, mAnimatorDuration);
    }
    public float getProgressValue() {
        return mProgressValue;
    }

    private float mProgressAngle = 0F;
    private void setAnimation(float last, float current, long duration) {
        final ValueAnimator progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(duration);
        progressAnimator.setTarget(mProgressAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgressAngle = (float) animation.getAnimatedValue();
                mProgressValue = mProgressAngle * mProgressMaxValue / mProgressMaxAngle;
            }
        });
        progressAnimator.start();
    }


    /* -------------- ///[Others] -------------- */
    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5F * (dip >= 0 ? 1 : -1));
    }

}

