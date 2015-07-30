package us.eiyou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Au on 16-Jul-15.
 */
//input and output class
public class IO {
    /**
     * put something with keyboard
     * @return
     * @throws IOException
     */
    public static <T>T i() throws IOException {
        String s=new String(new BufferedReader(new InputStreamReader(System.in)).readLine());
        try{
            Integer i=Integer.parseInt(s);
            return (T) i;
        }catch (Exception e){
        }
        try{
            Double d=Double.parseDouble(s);
            return (T) d;
        }catch (Exception e) {
            return (T) s;
        }
    }

    /**
     * output
     * @param s
     */
    public static void o(String s){
        System.out.println(s);
    }
}
