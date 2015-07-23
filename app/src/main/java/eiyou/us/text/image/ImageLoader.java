package eiyou.us.text.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Au on 23-Jul-15.
 */
public class ImageLoader {
    private String url;
    //lru算法的缓存机制
    private LruCache<String,Bitmap> cache;
    public ImageLoader(){
        int maxMemory=(int)Runtime.getRuntime().maxMemory();
        int cacheSize=maxMemory/4;
        cache=new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public void addBitmapToCache(String url,Bitmap bitmap){
        if(getBitmapFromCache(url)==null){
            cache.put(url,bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String url){
        return cache.get(url);
    }

    public void showImageByAsyncTask(ImageView imageView,String url){
        this.url=url;
        Bitmap bitmap=getBitmapFromCache(url);
        if(bitmap==null){
            new NewsAsyncTask(imageView).execute(url);
        }else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public class NewsAsyncTask extends AsyncTask<String,Void,Bitmap>{
        private ImageView imageView;
        public NewsAsyncTask(ImageView imageView){
            this.imageView=imageView;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=getBitmapFromURL(params[0]);
            if (bitmap!=null){
                addBitmapToCache(params[0],bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView.getTag().equals(url)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    public Bitmap getBitmapFromURL(String urlString){
        Bitmap bitmap;
        InputStream is=null;
        try{
            URL url=new URL(urlString);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            is=new BufferedInputStream(connection.getInputStream());
            bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
