
package com.huapu.huafen.banner;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * @ClassName: ABaseTransformer
 * @Description: banner翻转效果
 * @author chen_hao
 * @date 2016-05-26
 */
public abstract class ABaseTransformer implements PageTransformer {

	
	protected abstract void onTransform(View page, float position);

	/**
	 * @param page 应用转换到这个页面
	 */
	@Override
	public void transformPage(View page, float position) {
		onPreTransform(page, position);
		onTransform(page, position);
		onPostTransform(page, position);
	}

	/**
	 * 如果该位置的片段的偏移小于负一个或多于一个，返回true
	 */
	protected boolean hideOffscreenPages() {
		return true;
	}

	/**
	 * 是否使用viewpager的默认的动画
	 */
	protected boolean isPagingEnabled() {
		return false;
	}

	
	protected void onPreTransform(View page, float position) {
		final float width = page.getWidth();

		page.setRotationX(0);
		page.setRotationY(0);
		page.setRotation(0);
		page.setScaleX(1);
		page.setScaleY(1);
		page.setPivotX(0);
		page.setPivotY(0);
		page.setTranslationY(0);
		page.setTranslationX(isPagingEnabled() ? 0f : -width * position);

		if (hideOffscreenPages()) {
			page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
			page.setEnabled(false);
		} else {
			page.setEnabled(true);
			page.setAlpha(1f);
		}
	}

	
	protected void onPostTransform(View page, float position) {
	}

	/**
	 * Same as {@link Math#min(double, double)} without double casting, zero closest to infinity handling, or NaN support.
	 * 
	 * @param val
	 * @param min
	 * @return
	 */
	protected static final float min(float val, float min) {
		return val < min ? min : val;
	}

}
