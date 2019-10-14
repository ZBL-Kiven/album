package com.zj.album.cache;

import android.os.AsyncTask;

import com.zj.album.services.Constancs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojie on 2017/10/10.
 */

public class PhotoCacheUtils {
    /**
     * 对静态清单进行配置，项目缓存主目录
     */
    public static final String CACHEHOME = Constancs.INSTANCE.getPhotoCachePath();

    private static PhotoCacheUtils mCacheUtils;
    private File homeFile;
    private WeakReference<OnSaveCacheActionListener> saveListener;
    private WeakReference<OnGetCacheActionListener> getListener;
    private WeakReference<getAllCacheLitener> allCacheLitener;
    private WeakReference<removeFolderListener> removeFolderListener;

    private PhotoCacheUtils() {
        homeFile = new File(CACHEHOME);
        if (!homeFile.exists()) homeFile.mkdirs();
    }

    public static PhotoCacheUtils getInstance() {
        if (mCacheUtils == null) mCacheUtils = new PhotoCacheUtils();
        return mCacheUtils;
    }

    //调用，存
    public boolean saveCache(String cacheName, String obj) {
        return save(cacheName + ".txt", getEncode(obj));
    }

    //调用，存，异步
    public void saveCache(String cacheName, String obj, OnSaveCacheActionListener listener) {
        this.saveListener = new WeakReference(listener);
        new saveCacheTask().execute(cacheName + ".txt", getEncode(obj));
    }

    //调用，取
    public String getCache(String cacheName) {
        return takeOut(cacheName + ".txt");
    }

    //调用，取,异步
    public void getCache(String cacheName, OnGetCacheActionListener listener) {
        this.getListener = new WeakReference(listener);
        new getCacheTask().execute(cacheName + ".txt");
    }

    //调用，取全部，异步
    public void getAllCaches(List<String> keys, getAllCacheLitener litener) {
        allCacheLitener = new WeakReference(litener);
        new getAllTask().execute(keys);
    }

    private boolean save(String... params) {
        try {
            File file = new File(homeFile, params[0]);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(params[1].getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String takeOut(String... params) {
        File file = new File(homeFile, params[0]);
        String s = "";
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(isr);
            String mimeTypeLine = null;
            StringBuffer sb = new StringBuffer();
            while ((mimeTypeLine = br.readLine()) != null) {
                sb.append(mimeTypeLine);
            }
            s = sb.toString();
        } catch (Exception ignore) {
        }
        return getDecode(s);
    }

    private List<String> getAll(List<String> keys) {
        List<String> datas = new ArrayList<>();
        for (String key : keys) {
            datas.add(takeOut(key));
        }
        return datas;
    }

    //存本地JSON
    private class saveCacheTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return save(params);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (saveListener != null) {
                saveListener.get().onResult(b);
                saveListener.clear();
                saveListener = null;
            }
        }
    }

    //取本地JSON
    private class getCacheTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return takeOut(params);
        }

        @Override
        protected void onPostExecute(String s) {
            if (getListener != null) {
                getListener.get().onResult(s);
                getListener.clear();
                getListener = null;
            }
        }
    }

    //取本地JSON组
    private class getAllTask extends AsyncTask<List<String>, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(List<String>... lists) {
            return getAll(lists[0]);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (allCacheLitener != null) {
                allCacheLitener.get().onGet(strings);
                allCacheLitener.clear();
                allCacheLitener = null;
            }
        }
    }

    //异步移除一个或多个文件的异步线程
    private class RemoveFolderTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean b = true;
            for (String path : strings) {
                b = clearFolder(path);
                if (!b) return false;
            }
            return b;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (removeFolderListener != null) {
                removeFolderListener.get().onRemoved(aBoolean);
                removeFolderListener.clear();
                removeFolderListener = null;
            }
        }
    }


    public interface OnSaveCacheActionListener {

        void onResult(boolean isSuccess);
    }

    public interface OnGetCacheActionListener {

        void onResult(String s);
    }

    public interface getAllCacheLitener {
        void onGet(List<String> strs);
    }

    public interface removeFolderListener {
        void onRemoved(boolean success);
    }

    /**
     * 异步移除一个或多个文件
     */
    public void removeFolder(removeFolderListener listener, String... fileName) {
        this.removeFolderListener = new WeakReference<>(listener);
        new RemoveFolderTask().execute(fileName);
    }

    /**
     * 异步移除多个文件
     */
    public void removeFolder(removeFolderListener listener, List<String> fileNames) {
        this.removeFolderListener = new WeakReference<>(listener);
        if (fileNames != null && fileNames.size() > 0) {
            String[] files = new String[fileNames.size()];
            for (int i = 0; i < fileNames.size(); i++) {
                files[i] = fileNames.get(i);
            }
            new RemoveFolderTask().execute(files);
        }
    }

    public void clearAll() {
        clearFolder(CACHEHOME);
    }

    public void removeFile(String filePath) {
        clearFolder(CACHEHOME + filePath + ".txt");
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    private boolean clearFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                return deleteCache(filePath);
            } else {
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private boolean deleteDirectory(String filePath) {
        boolean flag;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (File file : files) {
            if (file.isFile()) {
                //删除子文件
                flag = deleteCache(file.getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    private boolean deleteCache(String key) {
        File file = new File(key);
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }

    private String getEncode(String s) {
        String result = "";
        try {
            result = URLEncoder.encode(s, "UTF-8");
        } catch (Exception ignore) {
        }
        return result;
    }

    private String getDecode(String s) {
        String result = "";
        try {
            result = URLDecoder.decode(s, "UTF-8");
        } catch (Exception ignore) {
        }
        return result;
    }
}
