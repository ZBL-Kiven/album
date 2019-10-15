/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.zj.album.graphy.views.gestures_view.frescoUtils.libs.zoomable;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Interface for implementing a controller that works with { ZoomableDraweeView}
 * to control the zoom.
 */
public interface ZoomableController {

  /**
   * Listener interface.
   */
  public interface Listener {

    void onTransformChanged(Matrix transform);
  }


  void setEnabled(boolean enabled);

  boolean isEnabled();

  void setListener(Listener listener);

  float getScaleFactor();

  Matrix getTransform();

  void setImageBounds(RectF imageBounds);

  void setViewBounds(RectF viewBounds);

  boolean onTouchEvent(MotionEvent event);
}
