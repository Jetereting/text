package us.eiyou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Au on 16-Jul-15.
 */
//输入输出类
public class IO {
    /**
     * 从键盘输入一个东西
     * @return 返回输入内容
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
     * 输出
     * @param s
     */
    public static void o(String s){
        System.out.println(s);
    }
}
