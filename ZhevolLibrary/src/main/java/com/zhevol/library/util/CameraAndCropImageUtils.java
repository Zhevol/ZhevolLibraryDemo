package com.zhevol.library.util;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/2/7.
 * 调用系统内置的图片选择和裁剪图片工具类
 * 适配到8.0……部分国产机可能也许大概估计会出问题吧……手上没小米机，谁有的可以测试一下
 * <p>
 * 请确保FileProvider能获取到Context.getExternalCacheDir()目录的访问权限，如：
 * 在FileProvider的xml中添加 <external-cache-path name="name" path="." /> 条目
 * <p>
 * 裁剪：
 * CameraAndCropImageUtils.cropImage(activity,imageFile,authority).……设置剪裁参数…….start(获取回调);
 * <p>
 * 调用相机：
 * CameraAndCropImageUtils.camera(activity,authority).toCamera(回调数据);
 */

public class CameraAndCropImageUtils {

    //请保证app获取到这些权限
    public static String[] getPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return permissions;
    }


    //图片剪切
    private static void cropImage2(FragmentActivity activity, @NonNull File file, String authority, int aspectX, int aspectY, int outputX, int outputY, CropImageBack cropImageBack) {
        getSelectAndCropFragment(activity).cropImage(file, authority, aspectX, aspectY, outputX, outputY, cropImageBack);
    }

    //相机
    private static void camera2(FragmentActivity activity, CameraImageBack cameraImageBack, String authority) {
        getSelectAndCropFragment(activity).camera(authority, cameraImageBack);
    }

    /**
     * 裁剪
     *
     * @param activity
     * @param image     图片文件
     * @param authority FileProvider的authority
     * @return
     */
    public static CropImage cropImage(@NonNull Activity activity, @NonNull File image, @NonNull String authority) {
        return new CropImage(activity, authority, image);
    }

    /**
     * 调用相机
     *
     * @param activity
     * @param authority FileProvider的authority
     * @return
     */
    public static Camera camera(@NonNull Activity activity, @NonNull String authority) {
        return new Camera(activity, authority);
    }

    public static class Camera {
        FragmentActivity activity;
        private String authority = null;

        private Camera(@NonNull Activity activity, @NonNull String authority) {
            if (!(activity instanceof FragmentActivity)) {
                throw new RuntimeException("activity 需要FragmentActivity类型");
            } else {
                this.activity = (FragmentActivity) activity;
                this.authority = authority;
            }
        }

        public void toCamera(CameraImageBack cameraImageBack) {
            camera2(activity, cameraImageBack, authority);
        }
    }

    public static class CropImage {
        private FragmentActivity activity;
        private String authority;
        private File file;
        private int aspectX = 0, aspectY = 0;
        private int outputX = 0, outputY = 0;
        private CropImageBack cropImageBack;

        private CropImage(@NonNull Activity activity, @NonNull String authority, @NonNull File file) {
            if (!(activity instanceof FragmentActivity)) {
                throw new RuntimeException("activity 需要FragmentActivity类型");
            } else {
                this.activity = (FragmentActivity) activity;
                this.authority = authority;
                this.file = file;
            }
        }

        /**
         * 输出比例，不设置则以截图为准
         *
         * @param X
         * @param Y
         * @return
         */
        public CropImage setAspect(int X, int Y) {
            this.aspectX = X;
            this.aspectY = Y;
            return this;
        }

        /**
         * 输出大小，不设置则以截图为准
         *
         * @param X
         * @param Y
         * @return
         */
        public CropImage setOutputSize(int X, int Y) {
            this.outputX = X;
            this.outputY = Y;
            return this;
        }

        public void start(CropImageBack cropImageBack) {
            cropImage2(activity, file, authority, aspectX, aspectY, outputX, outputY, cropImageBack);
        }
    }

    //裁剪接口回调
    public interface CropImageBack {
        void cropImage(Bitmap bitmap);

        void erron(String err);
    }

    //相机接口回调
    public interface CameraImageBack {
        void cropImage(File file);

        void erron(String err);
    }

    private static SelectAndCropFragment getSelectAndCropFragment(FragmentActivity activity) {
        String tab = "Crop";
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = null;
        if (fragment == null) {
            fragment = new SelectAndCropFragment();
            fragmentManager.beginTransaction().add(fragment, tab).commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        return (SelectAndCropFragment) fragment;
    }


    //内置工具类
    private static class Utils {

        /**
         * 图片路径转换相关
         */
        private static class ImageConvertUtils {

            /**
             * 文件转绝对路径
             *
             * @param file
             * @return
             */
            private static String fileToPath(@NonNull File file) {
                return file.getPath();
            }

            /**
             * 绝对路径转文件
             *
             * @param path
             * @return
             */
            private static File pathToFile(@NonNull String path) {
                File file = new File(path);
                if (isFileExists(file)) {
                    return file;
                } else {
                    throw new RuntimeException("文件不存在");
                }
            }

            private static Uri pathToUri(@NonNull String path, @NonNull Context context, @NonNull String authority) {
                return fileToUri(new File(path), context, authority);
            }

            /**
             * File转Uri 兼容android 7.0,需要先定义FileProvider
             *
             * @param file      图片文件
             * @param context
             * @param authority FileProvider的authority，不懂的自行百度“FileProvider”
             * @return
             */
            private static Uri fileToUri(File file, @NonNull Context context, @NonNull String authority) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return FileProvider.getUriForFile(context, authority, file);
                } else {
                    // > 7.0
                    return Uri.fromFile(file);
                }
            }

            /**
             * uri转bitmap
             *
             * @param context
             * @param uri
             * @return
             */
            private static Bitmap uriToBitmap(Context context, Uri uri) {
                ContentResolver cr = context.getContentResolver();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            /**
             * path转bitmap(直接从数据库读取) 兼容android7.0
             *
             * @param path
             * @param context
             * @param authority FileProvider的authority，不懂的自行百度“FileProvider”
             * @return
             */
            private static Bitmap pathToBitmap(String path, @NonNull Context context, @NonNull String authority) {
                return uriToBitmap(context, fileToUri(pathToFile(path), context, authority));
            }

            /**
             * path转bitmap(直接转换)
             *
             * @param path
             * @return
             */
            private static Bitmap pathToBitmap(String path) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 先获取原大小
                Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
                options.inJustDecodeBounds = false; // 获取新的大小

                int sampleSize = 1;
                options.inSampleSize = sampleSize;
                return BitmapFactory.decodeFile(path, options);

            }


            /**
             * bitmap转file
             *
             * @param context
             * @param bitmap
             * @return 注意，建议在其他线程中执行此方法，避免阻塞主线程
             */
            private static File bitmapToFile(@NonNull Context context, @NonNull Bitmap bitmap) {
                String cacheFileNmame = System.currentTimeMillis() + (int) (Math.random() * 1000) + ".jpg";
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), cacheFileNmame);
                try {
                    FileOutputStream fileOutStream = null;
                    fileOutStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream); // 把位图输出到指定的文件中
                    fileOutStream.flush();
                    fileOutStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return file;
            }

            /**
             * 判断文件是否存在
             *
             * @param file 文件
             * @return {@code true}: 存在<br>{@code false}: 不存在
             */
            private static boolean isFileExists(final File file) {
                return file != null && file.exists();
            }


            /**
             * uri 转path
             *
             * @param context
             * @param uri
             * @return
             */
            private static String uriToPath(Context context, Uri uri) {
                if (null == uri)
                    return null;
                final String scheme = uri.getScheme();
                String data = null;
                if (scheme == null)
                    data = uri.getPath();
                else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                    data = uri.getPath();
                } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                    Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1) {
                                data = cursor.getString(index);
                            }

                        }
                        cursor.close();
                    }
                    if (data == null) {
                        data = getImageAbsolutePath(context, uri);
                    }

                }
                return data;
            }

            private static String getImageAbsolutePath(Context context, Uri imageUri) {
                if (context == null || imageUri == null)
                    return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
                    if (isExternalStorageDocument(imageUri)) {
                        String docId = DocumentsContract.getDocumentId(imageUri);
                        String[] split = docId.split(":");
                        String type = split[0];
                        if ("primary".equalsIgnoreCase(type)) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        }
                    } else if (isDownloadsDocument(imageUri)) {
                        String id = DocumentsContract.getDocumentId(imageUri);
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } else if (isMediaDocument(imageUri)) {
                        String docId = DocumentsContract.getDocumentId(imageUri);
                        String[] split = docId.split(":");
                        String type = split[0];
                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        String selection = MediaStore.Images.Media._ID + "=?";
                        String[] selectionArgs = new String[]{split[1]};
                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                } // MediaStore (and general)
                else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
                    // Return the remote address
                    if (isGooglePhotosUri(imageUri))
                        return imageUri.getLastPathSegment();
                    else {
                        String[] projection = {MediaStore.Images.Media.DATA};
                        String urlpath;
                        CursorLoader loader = new CursorLoader(context, imageUri, projection, null, null, null);
                        Cursor cursor = loader.loadInBackground();
                        try {
                            int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            urlpath = cursor.getString(column_index);
                            //如果是正常的查询到数据库。然后返回结构
                            return urlpath;
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO: handle exception
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }

//                //如果是文件。Uri.fromFile(File file)生成的uri。那么下面这个方法可以得到结果
                        urlpath = imageUri.getPath();
                        return urlpath;
                    }
                }
                // File
                else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
                    return imageUri.getPath();
                }
                return null;
            }

            private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
                Cursor cursor = null;
                String column = MediaStore.Images.Media.DATA;
                String[] projection = {column};
                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int index = cursor.getColumnIndexOrThrow(column);
                        return cursor.getString(index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                return null;
            }

            private static boolean isExternalStorageDocument(Uri uri) {
                return "com.android.externalstorage.documents".equals(uri.getAuthority());
            }

            private static boolean isDownloadsDocument(Uri uri) {
                return "com.android.providers.downloads.documents".equals(uri.getAuthority());
            }

            private static boolean isMediaDocument(Uri uri) {
                return "com.android.providers.media.documents".equals(uri.getAuthority());
            }

            private static boolean isGooglePhotosUri(Uri uri) {
                return "com.google.android.apps.photos.content".equals(uri.getAuthority());
            }
        }

        //sd卡工具类
        private static class SDCardUtils {

            private SDCardUtils() {
                throw new UnsupportedOperationException("u can't instantiate me...");
            }

            /**
             * 判断SD卡是否可用
             *
             * @return true : 可用<br>false : 不可用
             */
            public static boolean isSDCardEnable(Context context) {
                return !getSDCardPaths(context).isEmpty();
            }

            /**
             * 获取SD卡路径
             *
             * @param removable true : 外置SD卡<br>false : 内置SD卡
             * @return SD卡路径
             */
            @SuppressWarnings("TryWithIdenticalCatches")
            public static List<String> getSDCardPaths(Context context, boolean removable) {
                List<String> paths = new ArrayList<>();
                StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                try {
                    Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                    Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
                    Method getPath = storageVolumeClazz.getMethod("getPath");
                    Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
                    Object result = getVolumeList.invoke(mStorageManager);
                    final int length = Array.getLength(result);
                    for (int i = 0; i < length; i++) {
                        Object storageVolumeElement = Array.get(result, i);
                        String path = (String) getPath.invoke(storageVolumeElement);
                        boolean res = (Boolean) isRemovable.invoke(storageVolumeElement);
                        if (removable == res) {
                            paths.add(path);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return paths;
            }

            /**
             * 获取SD卡路径
             *
             * @return SD卡路径
             */
            @SuppressWarnings("TryWithIdenticalCatches")
            public static List<String> getSDCardPaths(Context context) {
                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                List<String> paths = new ArrayList<>();
                try {
                    Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
                    getVolumePathsMethod.setAccessible(true);
                    Object invoke = getVolumePathsMethod.invoke(storageManager);
                    paths = Arrays.asList((String[]) invoke);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return paths;
            }
        }

    }

    public static class SelectAndCropFragment extends Fragment {

        private CropImageBack cropImageBack;
        private Uri outUri = null;
        private static final int requestCode_crop = 10001;
        private static final int requestCode_camera = 10002;
        private String cameraImageName = null;
        private CameraImageBack cameraImageBack;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        //裁剪图片
        public void cropImage(@NonNull File file, String authority, int aspectX, int aspectY, int outputX, int outputY, CropImageBack cropImageBack) {
            this.cropImageBack = cropImageBack;
            Uri inUri = Utils.ImageConvertUtils.fileToUri(file, getContext(), authority);
            outUri = Uri.fromFile(new File(getContext().getExternalCacheDir().getAbsolutePath(), getImageName()));
            Intent intent = new Intent("com.android.camera.action.CROP");
            //sdk>=24
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getContext().grantUriPermission(getContext().getPackageName(), outUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, uri));
                intent.putExtra("noFaceDetection", true);//去除默认的人脸识别，否则和剪裁匡重叠
            }
            intent.setDataAndType(inUri, "image/*");
            intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
            if (aspectX != 0 && aspectY != 0) {
                intent.putExtra("aspectX", aspectX);// 这两项为裁剪框的比例.
                intent.putExtra("aspectY", aspectY);// x:y=1:2
            }
            if (outputX != 0 && outputY != 0) {
                intent.putExtra("outputX", outputX);
                intent.putExtra("outputY", outputY);
            }
            intent.putExtra("output", outUri);
            intent.putExtra("outputFormat", "JPEG");// 返回格式
            startActivityForResult(intent, requestCode_crop);
        }

        private void camera(String authority, CameraImageBack cameraImageBack) {
            this.cameraImageBack = cameraImageBack;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            cameraImageName = getImageName();
            //判断sd是否存在
            File file = new File(getContext().getExternalCacheDir().getAbsolutePath(), cameraImageName);
            Uri uri = null;

            // 判断存储卡是否可以用，可用进行存储
            if (Utils.SDCardUtils.isSDCardEnable(getContext())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(getContext(), authority, file);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(file);
                }
            } else {
                if (cameraImageBack != null)
                    cameraImageBack.erron("SD卡不可用");
                return;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, requestCode_camera);
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == requestCode_crop) {
                if (resultCode == -1) {
                    Bitmap bitmap = Utils.ImageConvertUtils.uriToBitmap(getContext(), outUri);
                    if (bitmap != null) {
                        if (cropImageBack != null) {
                            cropImageBack.cropImage(bitmap);
                        }
                    } else {
                        final String[] SCAN_TYPES = {"image/jpeg"};
                        MediaScannerConnection.scanFile(getContext(), new String[]{Utils.ImageConvertUtils.uriToPath(getContext(), outUri)}, SCAN_TYPES, null);
                        bitmap = Utils.ImageConvertUtils.uriToBitmap(getContext(), outUri);
                        if (cropImageBack != null) {
                            if (bitmap != null)
                                cropImageBack.cropImage(bitmap);
                            else
                                cropImageBack.erron("图片截取失败");
                        }
                    }
                } else {
                    if (cropImageBack != null)
                        cropImageBack.erron("图片截取失败");
                }
            } else if (requestCode == requestCode_camera) {
                if (resultCode == -1) {
                    if (cameraImageName == null)
                        return;

                    File file = new File(getContext().getExternalCacheDir().getAbsolutePath(),
                            cameraImageName);
                    if (cameraImageBack != null) {
                        if (file != null)
                            cameraImageBack.cropImage(file);
                        else
                            cameraImageBack.erron("获取照片失败");
                    }
                } else if (cameraImageBack != null)
                    cameraImageBack.erron("获取照片失败");
            }

            signOut();
            getFragmentManager().popBackStack();
        }

        private String getImageName() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            return "/" + formatter.format(new Date()) + (int) (Math.random() * 1000) + ".jpg";
        }

        private void signOut() {
            cropImageBack = null;
            outUri = null;
            cameraImageBack = null;
            cameraImageName = null;
        }
    }


}
