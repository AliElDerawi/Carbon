package carbon.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import carbon.Carbon;
import carbon.CarbonContextWrapper;
import carbon.R;
import carbon.animation.AnimatedColorStateList;
import carbon.animation.AnimatedView;
import carbon.animation.StateAnimator;
import carbon.drawable.VectorDrawable;
import carbon.drawable.ripple.RippleDrawable;
import carbon.drawable.ripple.RippleView;
import carbon.internal.RevealAnimator;
import carbon.shadow.Shadow;
import carbon.shadow.ShadowGenerator;
import carbon.shadow.ShadowShape;
import carbon.shadow.ShadowView;
import carbon.view.MaxSizeView;
import carbon.view.RevealView;
import carbon.view.RoundedCornersView;
import carbon.view.StateAnimatorView;
import carbon.view.StrokeView;
import carbon.view.TintedView;
import carbon.view.TouchMarginView;
import carbon.view.TransformationView;
import carbon.view.VisibleView;

@SuppressLint("AppCompatCustomView")
public class ImageView extends android.widget.ImageView
        implements
        ShadowView,
        RippleView,
        TouchMarginView,
        StateAnimatorView,
        AnimatedView,
        RoundedCornersView,
        TintedView,
        StrokeView,
        MaxSizeView,
        RevealView,
        VisibleView,
        TransformationView {

    public ImageView(Context context) {
        super(CarbonContextWrapper.wrap(context), null, R.attr.carbon_imageViewStyle);
        initImageView(null, R.attr.carbon_imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(Carbon.getThemedContext(context, attrs, R.styleable.ImageView, R.attr.carbon_imageViewStyle, R.styleable.ImageView_carbon_theme), attrs, R.attr.carbon_imageViewStyle);
        initImageView(attrs, R.attr.carbon_imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(Carbon.getThemedContext(context, attrs, R.styleable.ImageView, defStyleAttr, R.styleable.ImageView_carbon_theme), attrs, defStyleAttr);
        initImageView(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(Carbon.getThemedContext(context, attrs, R.styleable.ImageView, defStyleAttr, R.styleable.ImageView_carbon_theme), attrs, defStyleAttr, defStyleRes);
        initImageView(attrs, defStyleAttr);
    }

    private static int[] rippleIds = new int[]{
            R.styleable.ImageView_carbon_rippleColor,
            R.styleable.ImageView_carbon_rippleStyle,
            R.styleable.ImageView_carbon_rippleHotspot,
            R.styleable.ImageView_carbon_rippleRadius
    };
    private static int[] animationIds = new int[]{
            R.styleable.ImageView_carbon_inAnimation,
            R.styleable.ImageView_carbon_outAnimation
    };
    private static int[] touchMarginIds = new int[]{
            R.styleable.ImageView_carbon_touchMargin,
            R.styleable.ImageView_carbon_touchMarginLeft,
            R.styleable.ImageView_carbon_touchMarginTop,
            R.styleable.ImageView_carbon_touchMarginRight,
            R.styleable.ImageView_carbon_touchMarginBottom
    };
    private static int[] tintIds = new int[]{
            R.styleable.ImageView_carbon_tint,
            R.styleable.ImageView_carbon_tintMode,
            R.styleable.ImageView_carbon_backgroundTint,
            R.styleable.ImageView_carbon_backgroundTintMode,
            R.styleable.ImageView_carbon_animateColorChanges
    };
    private static int[] strokeIds = new int[]{
            R.styleable.ImageView_carbon_stroke,
            R.styleable.ImageView_carbon_strokeWidth
    };
    private static int[] maxSizeIds = new int[]{
            R.styleable.ImageView_carbon_maxWidth,
            R.styleable.ImageView_carbon_maxHeight,
    };
    private static int[] elevationIds = new int[]{
            R.styleable.ImageView_carbon_elevation,
            R.styleable.ImageView_carbon_elevationShadowColor,
            R.styleable.ImageView_carbon_elevationAmbientShadowColor,
            R.styleable.ImageView_carbon_elevationSpotShadowColor
    };

    private void initImageView(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageView, defStyleAttr, R.style.carbon_ImageView);

        for (int i = 0; i < a.getIndexCount(); i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ImageView_android_src) {
                int resId = a.getResourceId(attr, 0);
                if (resId != 0 && getContext().getResources().getResourceTypeName(resId).equals("raw")) {
                    if (!isInEditMode()) {
                        setImageDrawable(new VectorDrawable(getResources(), resId));
                    } else {
                        setImageResource(R.drawable.carbon_iconplaceholder);
                    }
                }
            } else if (attr == R.styleable.ImageView_android_enabled) {
                setEnabled(a.getBoolean(attr, true));
            } else if (attr == R.styleable.ImageView_carbon_cornerRadius) {
                setCornerRadius(a.getDimension(attr, 0));
            }
        }

        new AppCompatImageHelper(this).loadFromAttributes(attrs, defStyleAttr);

        Carbon.initDefaultBackground(this, a, R.styleable.ImageView_android_background);

        Carbon.initElevation(this, a, elevationIds);
        Carbon.initRippleDrawable(this, a, rippleIds);
        Carbon.initAnimations(this, a, animationIds);
        Carbon.initTouchMargin(this, a, touchMarginIds);
        Carbon.initMaxSize(this, a, maxSizeIds);
        Carbon.initTint(this, a, tintIds);
        Carbon.initStroke(this, a, strokeIds);

        a.recycle();
    }

    @Override
    public void setImageResource(int resId) {
        if (resId != 0 && getContext().getResources().getResourceTypeName(resId).equals("raw")) {
            setImageDrawable(new VectorDrawable(getResources(), resId));
        } else {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        updateTint();
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    RevealAnimator revealAnimator;

    public Point getLocationOnScreen() {
        int[] outLocation = new int[2];
        super.getLocationOnScreen(outLocation);
        return new Point(outLocation[0], outLocation[1]);
    }

    public Point getLocationInWindow() {
        int[] outLocation = new int[2];
        super.getLocationInWindow(outLocation);
        return new Point(outLocation[0], outLocation[1]);
    }

    public Animator createCircularReveal(android.view.View hotspot, float startRadius, float finishRadius) {
        int[] location = new int[2];
        hotspot.getLocationOnScreen(location);
        int[] myLocation = new int[2];
        getLocationOnScreen(myLocation);
        return createCircularReveal(location[0] - myLocation[0] + hotspot.getWidth() / 2, location[1] - myLocation[1] + hotspot.getHeight() / 2, startRadius, finishRadius);
    }

    @Override
    public Animator createCircularReveal(int x, int y, float startRadius, float finishRadius) {
        startRadius = Carbon.getRevealRadius(this, x, y, startRadius);
        finishRadius = Carbon.getRevealRadius(this, x, y, finishRadius);
        if (Carbon.IS_LOLLIPOP_OR_HIGHER && renderingMode == RenderingMode.Auto) {
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(this, x, y, startRadius, finishRadius);
            circularReveal.setDuration(Carbon.getDefaultRevealDuration());
            return circularReveal;
        } else {
            revealAnimator = new RevealAnimator(x, y, startRadius, finishRadius);
            revealAnimator.setDuration(Carbon.getDefaultRevealDuration());
            revealAnimator.addUpdateListener(animation -> {
                RevealAnimator reveal = ((RevealAnimator) animation);
                reveal.radius = (float) reveal.getAnimatedValue();
                reveal.mask.reset();
                reveal.mask.addCircle(reveal.x, reveal.y, Math.max((Float) reveal.getAnimatedValue(), 1), Path.Direction.CW);
                postInvalidate();
            });
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    revealAnimator = null;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    revealAnimator = null;
                }
            });
            return revealAnimator;
        }
    }


    // -------------------------------
    // corners
    // -------------------------------

    private float cornerRadius;
    private Path cornersMask;

    /**
     * Gets the corner radius. If corner radius is equal to 0, rounded corners are turned off.
     *
     * @return corner radius, equal to or greater than 0.
     */
    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the corner radius. If corner radius is equal to 0, rounded corners are turned off.
     *
     * @param cornerRadius
     */
    public void setCornerRadius(float cornerRadius) {
        if (!Carbon.IS_LOLLIPOP_OR_HIGHER && cornerRadius != this.cornerRadius)
            postInvalidate();
        this.cornerRadius = cornerRadius;
        if (getWidth() > 0 && getHeight() > 0)
            updateCorners();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed)
            return;

        if (getWidth() == 0 || getHeight() == 0)
            return;

        updateCorners();

        if (rippleDrawable != null)
            rippleDrawable.setBounds(0, 0, getWidth(), getHeight());
    }

    private void updateCorners() {
        if (cornerRadius > 0) {
            cornerRadius = Math.min(cornerRadius, Math.min(getWidth(), getHeight()) / 2.0f);
            if (cornerRadius < 1)
                cornerRadius = 0;
            if (Carbon.IS_LOLLIPOP_OR_HIGHER && renderingMode == RenderingMode.Auto) {
                setClipToOutline(true);
                setOutlineProvider(ShadowShape.viewOutlineProvider);
            } else {
                cornersMask = new Path();
                cornersMask.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);
                cornersMask.setFillType(Path.FillType.INVERSE_WINDING);
            }
        }
    }

    public void drawInternal(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (stroke != null)
            drawStroke(canvas);
        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Over)
            rippleDrawable.draw(canvas);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(@NonNull Canvas canvas) {
        boolean r = revealAnimator != null;
        boolean c = cornerRadius > 0;
        if (isInEditMode() && (r || c) && getWidth() > 0 && getHeight() > 0) {
            Bitmap layer = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas layerCanvas = new Canvas(layer);
            drawInternal(layerCanvas);

            Bitmap mask = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mask);
            Paint maskPaint = new Paint(0xffffffff);
            maskCanvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), cornerRadius, cornerRadius, maskPaint);

            for (int x = 0; x < getWidth(); x++) {
                for (int y = 0; y < getHeight(); y++) {
                    int maskPixel = mask.getPixel(x, y);
                    layer.setPixel(x, y, Color.alpha(maskPixel) > 0 ? layer.getPixel(x, y) : 0);
                }
            }
            canvas.drawBitmap(layer, 0, 0, paint);
        } else if ((r || c) && getWidth() > 0 && getHeight() > 0 && (!Carbon.IS_LOLLIPOP_OR_HIGHER || renderingMode == RenderingMode.Software)) {
            int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

            if (r) {
                int saveCount2 = canvas.save();
                canvas.clipRect(revealAnimator.x - revealAnimator.radius, revealAnimator.y - revealAnimator.radius, revealAnimator.x + revealAnimator.radius, revealAnimator.y + revealAnimator.radius);
                drawInternal(canvas);
                canvas.restoreToCount(saveCount2);
            } else {
                drawInternal(canvas);
            }

            paint.setXfermode(Carbon.CLEAR_MODE);
            if (c)
                canvas.drawPath(cornersMask, paint);
            if (r)
                canvas.drawPath(revealAnimator.mask, paint);
            paint.setXfermode(null);

            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        } else {
            drawInternal(canvas);
        }
    }


    // -------------------------------
    // ripple
    // -------------------------------

    private RippleDrawable rippleDrawable;

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (rippleDrawable != null && event.getAction() == MotionEvent.ACTION_DOWN)
            rippleDrawable.setHotspot(event.getX(), event.getY());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public RippleDrawable getRippleDrawable() {
        return rippleDrawable;
    }

    @Override
    public void setRippleDrawable(RippleDrawable newRipple) {
        if (rippleDrawable != null) {
            rippleDrawable.setCallback(null);
            if (rippleDrawable.getStyle() == RippleDrawable.Style.Background)
                super.setBackgroundDrawable(rippleDrawable.getBackground());
        }

        if (newRipple != null) {
            newRipple.setCallback(this);
            newRipple.setBounds(0, 0, getWidth(), getHeight());
            newRipple.setState(getDrawableState());
            ((Drawable) newRipple).setVisible(getVisibility() == VISIBLE, false);
            if (newRipple.getStyle() == RippleDrawable.Style.Background)
                super.setBackgroundDrawable((Drawable) newRipple);
        }

        rippleDrawable = newRipple;
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || rippleDrawable == who;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate(@NonNull Rect dirty) {
        super.invalidate(dirty);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        super.invalidate(l, t, r, b);
        invalidateParentIfNeeded();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        invalidateParentIfNeeded();
    }

    private void invalidateParentIfNeeded() {
        if (getParent() == null || !(getParent() instanceof View))
            return;

        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Borderless)
            ((View) getParent()).invalidate();

        if (elevation > 0 || cornerRadius > 0)
            ((View) getParent()).invalidate();
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds) {
        super.postInvalidateDelayed(delayMilliseconds);
        postInvalidateParentIfNeededDelayed(delayMilliseconds);
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        super.postInvalidateDelayed(delayMilliseconds, left, top, right, bottom);
        postInvalidateParentIfNeededDelayed(delayMilliseconds);
    }

    private void postInvalidateParentIfNeededDelayed(long delayMilliseconds) {
        if (getParent() == null || !(getParent() instanceof View))
            return;

        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Borderless)
            ((View) getParent()).postInvalidateDelayed(delayMilliseconds);

        if (elevation > 0 || cornerRadius > 0)
            ((View) getParent()).postInvalidateDelayed(delayMilliseconds);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (background instanceof RippleDrawable) {
            setRippleDrawable((RippleDrawable) background);
            return;
        }

        if (rippleDrawable != null && rippleDrawable.getStyle() == RippleDrawable.Style.Background) {
            rippleDrawable.setCallback(null);
            rippleDrawable = null;
        }
        super.setBackgroundDrawable(background);
        updateBackgroundTint();
    }


    // -------------------------------
    // elevation
    // -------------------------------

    private float elevation = 0;
    private float translationZ = 0;
    private Shadow ambientShadow, spotShadow;
    private ColorStateList ambientShadowColor, spotShadowColor;
    private PorterDuffColorFilter ambientShadowColorFilter, spotShadowColorFilter;
    private RectF shadowMaskRect = new RectF();

    @Override
    public float getElevation() {
        return elevation;
    }

    @Override
    public void setElevation(float elevation) {
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setElevation(elevation);
            super.setTranslationZ(translationZ);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            if ((ambientShadowColor == null || spotShadowColor == null) && renderingMode == RenderingMode.Auto) {
                super.setElevation(elevation);
                super.setTranslationZ(translationZ);
            } else {
                super.setElevation(0);
                super.setTranslationZ(0);
            }
        } else if (elevation != this.elevation && getParent() != null) {
            ((View) getParent()).postInvalidate();
        }
        this.elevation = elevation;
    }

    @Override
    public float getTranslationZ() {
        return translationZ;
    }

    public void setTranslationZ(float translationZ) {
        if (translationZ == this.translationZ)
            return;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setTranslationZ(translationZ);
        } else if (Carbon.IS_LOLLIPOP_OR_HIGHER) {
            if ((ambientShadowColor == null || spotShadowColor == null) && renderingMode == RenderingMode.Auto) {
                super.setTranslationZ(translationZ);
            } else {
                super.setTranslationZ(0);
            }
        } else if (translationZ != this.translationZ && getParent() != null) {
            ((View) getParent()).postInvalidate();
        }
        this.translationZ = translationZ;
    }

    @Override
    public ShadowShape getShadowShape() {
        if (cornerRadius == getWidth() / 2 && getWidth() == getHeight())
            return ShadowShape.CIRCLE;
        if (cornerRadius > 0)
            return ShadowShape.ROUND_RECT;
        return ShadowShape.RECT;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public boolean hasShadow() {
        return getElevation() + getTranslationZ() >= 0.01f && getWidth() > 0 && getHeight() > 0;
    }

    @Override
    public void drawShadow(Canvas canvas) {
        float alpha = getAlpha() * Carbon.getDrawableAlpha(getBackground()) / 255.0f * Carbon.getBackgroundTintAlpha(this) / 255.0f;
        if (alpha == 0)
            return;

        if (!hasShadow())
            return;

        float z = getElevation() + getTranslationZ();
        if (ambientShadow == null || ambientShadow.elevation != z || ambientShadow.cornerRadius != cornerRadius) {
            ambientShadow = ShadowGenerator.generateShadow(this, z / getResources().getDisplayMetrics().density / 4);
            spotShadow = ShadowGenerator.generateShadow(this, z / getResources().getDisplayMetrics().density);
        }

        int saveCount = 0;
        boolean maskShadow = getBackground() != null && alpha != 1;
        boolean r = revealAnimator != null && revealAnimator.isRunning();
        if (maskShadow) {
            saveCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        } else if (r) {
            saveCount = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
            canvas.clipRect(
                    getLeft() + revealAnimator.x - revealAnimator.radius, getTop() + revealAnimator.y - revealAnimator.radius,
                    getLeft() + revealAnimator.x + revealAnimator.radius, getTop() + revealAnimator.y + revealAnimator.radius);
        }

        paint.setAlpha((int) (Shadow.ALPHA * alpha));

        Matrix matrix = getMatrix();

        canvas.save();
        canvas.translate(this.getLeft(), this.getTop());
        canvas.concat(matrix);
        ambientShadow.draw(canvas, this, paint, ambientShadowColorFilter);
        canvas.restore();

        canvas.save();
        canvas.translate(this.getLeft(), this.getTop() + z / 2);
        canvas.concat(matrix);
        spotShadow.draw(canvas, this, paint, spotShadowColorFilter);
        canvas.restore();

        if (saveCount != 0) {
            canvas.translate(this.getLeft(), this.getTop());
            canvas.concat(matrix);
            paint.setXfermode(Carbon.CLEAR_MODE);
        }
        if (maskShadow) {
            shadowMaskRect.set(0, 0, getWidth(), getHeight());
            canvas.drawRoundRect(shadowMaskRect, cornerRadius, cornerRadius, paint);
        }
        if (r) {
            canvas.drawPath(revealAnimator.mask, paint);
        }
        if (saveCount != 0) {
            canvas.restoreToCount(saveCount);
            paint.setXfermode(null);
        }
    }

    @Override
    public void setElevationShadowColor(ColorStateList shadowColor) {
        ambientShadowColor = spotShadowColor = shadowColor;
        ambientShadowColorFilter = spotShadowColorFilter = shadowColor != null ? new PorterDuffColorFilter(shadowColor.getColorForState(getDrawableState(), shadowColor.getDefaultColor()), PorterDuff.Mode.MULTIPLY) : Shadow.DEFAULT_FILTER;
        setElevation(elevation);
        setTranslationZ(translationZ);
    }

    @Override
    public void setElevationShadowColor(int color) {
        ambientShadowColor = spotShadowColor = ColorStateList.valueOf(color);
        ambientShadowColorFilter = spotShadowColorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        setElevation(elevation);
        setTranslationZ(translationZ);
    }

    @Override
    public ColorStateList getElevationShadowColor() {
        return ambientShadowColor;
    }

    @Override
    public void setOutlineAmbientShadowColor(int color) {
        setOutlineAmbientShadowColor(ColorStateList.valueOf(color));
    }

    @Override
    public void setOutlineAmbientShadowColor(ColorStateList color) {
        ambientShadowColor = color;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setOutlineAmbientShadowColor(color.getColorForState(getDrawableState(), color.getDefaultColor()));
        } else {
            ambientShadowColorFilter = new PorterDuffColorFilter(color.getColorForState(getDrawableState(), color.getDefaultColor()), PorterDuff.Mode.MULTIPLY);
            setElevation(elevation);
            setTranslationZ(translationZ);
        }
    }

    @Override
    public int getOutlineAmbientShadowColor() {
        return ambientShadowColor.getDefaultColor();
    }

    @Override
    public void setOutlineSpotShadowColor(int color) {
        setOutlineSpotShadowColor(ColorStateList.valueOf(color));
    }

    @Override
    public void setOutlineSpotShadowColor(ColorStateList color) {
        spotShadowColor = color;
        if (Carbon.IS_PIE_OR_HIGHER) {
            super.setOutlineSpotShadowColor(color.getColorForState(getDrawableState(), color.getDefaultColor()));
        } else {
            spotShadowColorFilter = new PorterDuffColorFilter(color.getColorForState(getDrawableState(), color.getDefaultColor()), PorterDuff.Mode.MULTIPLY);
            setElevation(elevation);
            setTranslationZ(translationZ);
        }
    }

    @Override
    public int getOutlineSpotShadowColor() {
        return spotShadowColor.getDefaultColor();
    }


    // -------------------------------
    // touch margin
    // -------------------------------

    private Rect touchMargin = new Rect();

    @Override
    public void setTouchMargin(int left, int top, int right, int bottom) {
        touchMargin.set(left, top, right, bottom);
    }

    @Override
    public void setTouchMarginLeft(int margin) {
        touchMargin.left = margin;
    }

    @Override
    public void setTouchMarginTop(int margin) {
        touchMargin.top = margin;
    }

    @Override
    public void setTouchMarginRight(int margin) {
        touchMargin.right = margin;
    }

    @Override
    public void setTouchMarginBottom(int margin) {
        touchMargin.bottom = margin;
    }

    @Override
    public Rect getTouchMargin() {
        return touchMargin;
    }

    final RectF tmpHitRect = new RectF();

    public void getHitRect(@NonNull Rect outRect) {
        Matrix matrix = getMatrix();
        if (matrix.isIdentity()) {
            outRect.set(getLeft(), getTop(), getRight(), getBottom());
        } else {
            tmpHitRect.set(0, 0, getWidth(), getHeight());
            matrix.mapRect(tmpHitRect);
            outRect.set((int) tmpHitRect.left + getLeft(), (int) tmpHitRect.top + getTop(),
                    (int) tmpHitRect.right + getLeft(), (int) tmpHitRect.bottom + getTop());
        }
        outRect.left -= touchMargin.left;
        outRect.top -= touchMargin.top;
        outRect.right += touchMargin.right;
        outRect.bottom += touchMargin.bottom;
    }


    // -------------------------------
    // state animators
    // -------------------------------

    private StateAnimator stateAnimator = new StateAnimator(this);

    @Override
    public StateAnimator getStateAnimator() {
        return stateAnimator;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (rippleDrawable != null && rippleDrawable.getStyle() != RippleDrawable.Style.Background)
            rippleDrawable.setState(getDrawableState());
        if (stateAnimator != null)
            stateAnimator.setState(getDrawableState());
        if (tint != null && tint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) tint).setState(getDrawableState());
        if (backgroundTint != null && backgroundTint instanceof AnimatedColorStateList)
            ((AnimatedColorStateList) backgroundTint).setState(getDrawableState());
        if (ambientShadow != null && ambientShadowColor != null)
            ambientShadowColorFilter = new PorterDuffColorFilter(ambientShadowColor.getColorForState(getDrawableState(), ambientShadowColor.getDefaultColor()), PorterDuff.Mode.MULTIPLY);
        if (spotShadow != null && spotShadowColor != null)
            spotShadowColorFilter = new PorterDuffColorFilter(spotShadowColor.getColorForState(getDrawableState(), spotShadowColor.getDefaultColor()), PorterDuff.Mode.MULTIPLY);
    }


    // -------------------------------
    // animations
    // -------------------------------

    private Animator inAnim = null, outAnim = null;
    private Animator animator;

    public Animator animateVisibility(final int visibility) {
        if (visibility == View.VISIBLE && (getVisibility() != View.VISIBLE || animator != null)) {
            if (animator != null)
                animator.cancel();
            if (inAnim != null) {
                animator = inAnim;
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator a) {
                        a.removeListener(this);
                        animator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator a) {
                        a.removeListener(this);
                        animator = null;
                    }
                });
                animator.start();
            }
            setVisibility(visibility);
        } else if (visibility != View.VISIBLE && (getVisibility() == View.VISIBLE || animator != null)) {
            if (animator != null)
                animator.cancel();
            if (outAnim == null) {
                setVisibility(visibility);
                return null;
            }
            animator = outAnim;
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    if (((ValueAnimator) a).getAnimatedFraction() == 1)
                        setVisibility(visibility);
                    a.removeListener(this);
                    animator = null;
                }

                @Override
                public void onAnimationCancel(Animator a) {
                    a.removeListener(this);
                    animator = null;
                }
            });
            animator.start();
        } else {
            setVisibility(visibility);
        }
        return animator;
    }

    public Animator getAnimator() {
        return animator;
    }

    public Animator getOutAnimator() {
        return outAnim;
    }

    public void setOutAnimator(Animator outAnim) {
        if (this.outAnim != null)
            this.outAnim.setTarget(null);
        this.outAnim = outAnim;
        if (outAnim != null)
            outAnim.setTarget(this);
    }

    public Animator getInAnimator() {
        return inAnim;
    }

    public void setInAnimator(Animator inAnim) {
        if (this.inAnim != null)
            this.inAnim.setTarget(null);
        this.inAnim = inAnim;
        if (inAnim != null)
            inAnim.setTarget(this);
    }


    // -------------------------------
    // tint
    // -------------------------------

    ColorStateList tint;
    PorterDuff.Mode tintMode;
    ColorStateList backgroundTint;
    PorterDuff.Mode backgroundTintMode;
    boolean animateColorChanges;
    ValueAnimator.AnimatorUpdateListener tintAnimatorListener = animation -> {
        updateTint();
        ViewCompat.postInvalidateOnAnimation(this);
    };
    ValueAnimator.AnimatorUpdateListener backgroundTintAnimatorListener = animation -> {
        updateBackgroundTint();
        ViewCompat.postInvalidateOnAnimation(this);
    };

    @Deprecated
    public void setTint(ColorStateList list) {
        setTintList(list);
    }

    @Override
    public void setTintList(ColorStateList list) {
        this.tint = list == null ? null : animateColorChanges && !(list instanceof AnimatedColorStateList) ? AnimatedColorStateList.fromList(list, tintAnimatorListener) : list;
        updateTint();
    }

    @Override
    public void setTint(int color) {
        setTint(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getTint() {
        return tint;
    }

    private void updateTint() {
        Drawable drawable = getDrawable();
        if (drawable == null)
            return;

        Carbon.setTintList(drawable, tint);
        Carbon.setTintMode(drawable, tintMode);
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode mode) {
        this.tintMode = mode;
        updateTint();
    }

    @Override
    public PorterDuff.Mode getTintMode() {
        return tintMode;
    }

    @Deprecated
    public void setBackgroundTint(ColorStateList list) {
        setBackgroundTintList(list);
    }

    @Override
    public void setBackgroundTintList(ColorStateList list) {
        this.backgroundTint = animateColorChanges && !(list instanceof AnimatedColorStateList) ? AnimatedColorStateList.fromList(list, backgroundTintAnimatorListener) : list;
        updateBackgroundTint();
    }

    @Override
    public void setBackgroundTint(int color) {
        setBackgroundTint(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getBackgroundTint() {
        return backgroundTint;
    }

    private void updateBackgroundTint() {
        Drawable background = getBackground();
        if (background instanceof RippleDrawable)
            background = ((RippleDrawable) background).getBackground();
        if (background == null)
            return;

        Carbon.setTintList(background, backgroundTint);
        Carbon.setTintMode(background, backgroundTintMode);
    }

    @Override
    public void setBackgroundTintMode(@Nullable PorterDuff.Mode mode) {
        this.backgroundTintMode = mode;
        updateBackgroundTint();
    }

    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        return backgroundTintMode;
    }

    public boolean isAnimateColorChangesEnabled() {
        return animateColorChanges;
    }

    public void setAnimateColorChangesEnabled(boolean animateColorChanges) {
        this.animateColorChanges = animateColorChanges;
        if (tint != null && !(tint instanceof AnimatedColorStateList))
            setTint(AnimatedColorStateList.fromList(tint, tintAnimatorListener));
        if (backgroundTint != null && !(backgroundTint instanceof AnimatedColorStateList))
            setBackgroundTint(AnimatedColorStateList.fromList(backgroundTint, backgroundTintAnimatorListener));
    }


    // -------------------------------
    // stroke
    // -------------------------------

    private ColorStateList stroke;
    private float strokeWidth;
    private Paint strokePaint;
    private RectF strokeRect;

    private void drawStroke(Canvas canvas) {
        strokePaint.setStrokeWidth(strokeWidth * 2);
        strokePaint.setColor(stroke.getColorForState(getDrawableState(), stroke.getDefaultColor()));
        strokeRect.set(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(strokeRect, cornerRadius, cornerRadius, strokePaint);
    }

    @Override
    public void setStroke(ColorStateList colorStateList) {
        stroke = colorStateList;

        if (stroke == null)
            return;

        if (strokePaint == null) {
            strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokeRect = new RectF();
        }
    }

    @Override
    public void setStroke(int color) {
        setStroke(ColorStateList.valueOf(color));
    }

    @Override
    public ColorStateList getStroke() {
        return stroke;
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public float getStrokeWidth() {
        return strokeWidth;
    }


    // -------------------------------
    // maximum width & height
    // -------------------------------

    int maxWidth = Integer.MAX_VALUE, maxHeight = Integer.MAX_VALUE;

    @Override
    public int getMaximumWidth() {
        return maxWidth;
    }

    @Override
    public void setMaximumWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        requestLayout();
    }

    @Override
    public int getMaximumHeight() {
        return maxHeight;
    }

    @Override
    public void setMaximumHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() > maxWidth || getMeasuredHeight() > maxHeight) {
            if (getMeasuredWidth() > maxWidth)
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
            if (getMeasuredHeight() > maxHeight)
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    // -------------------------------
    // rendering mode
    // -------------------------------

    private RenderingMode renderingMode = RenderingMode.Auto;

    @Override
    public void setRenderingMode(RenderingMode mode) {
        this.renderingMode = mode;
        setElevation(elevation);
        setTranslationZ(translationZ);
        if (getWidth() > 0 && getHeight() > 0)
            updateCorners();
    }

    @Override
    public RenderingMode getRenderingMode() {
        return renderingMode;
    }


    // -------------------------------
    // transformations
    // -------------------------------

    List<OnTransformationChangedListener> transformationChangedListeners = new ArrayList<>();

    public void addOnTransformationChangedListener(OnTransformationChangedListener listener) {
        transformationChangedListeners.add(listener);
    }

    public void removeOnTransformationChangedListener(OnTransformationChangedListener listener) {
        transformationChangedListeners.remove(listener);
    }

    public void clearOnTransformationChangedListeners() {
        transformationChangedListeners.clear();
    }

    private void fireOnTransformationChangedListener() {
        if (transformationChangedListeners == null)
            return;
        for (OnTransformationChangedListener listener : transformationChangedListeners)
            listener.onTransformationChanged();
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setRotationY(float rotationY) {
        super.setRotationY(rotationY);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setRotationX(float rotationX) {
        super.setRotationX(rotationX);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setPivotX(float pivotX) {
        super.setPivotX(pivotX);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setPivotY(float pivotY) {
        super.setPivotY(pivotY);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        super.setAlpha(alpha);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setTranslationX(float translationX) {
        super.setTranslationX(translationX);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        invalidateParentIfNeeded();
        fireOnTransformationChangedListener();
    }

    public void setWidth(int width) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            setLayoutParams(new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            layoutParams.width = width;
            setLayoutParams(layoutParams);
        }
    }

    public void setHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height));
        } else {
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            setLayoutParams(new ViewGroup.LayoutParams(width, height));
        } else {
            layoutParams.width = width;
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
    }

    public void setBounds(int x, int y, int width, int height) {
        setSize(width, height);
        setTranslationX(x);
        setTranslationY(y);
    }
}
