package com.zhevol.library.zxing.custom.camera;

import com.zhevol.library.zxing.custom.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
