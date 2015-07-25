package com.example;

import java.io.IOException;
import us.eiyou.utils.IO;

class Point<T>{
    public T getDog() {
        return dog;
    }

    public void setDog(T dog) {
        this.dog = dog;
    }

    T dog;

}
public class MyClass {
    public static void main(String[] args) throws IOException {
        IO.o(""+0%4);
        IO.o(""+1%4);
        IO.o(""+2%4);
        IO.o(""+3%4);
        IO.o(""+4%4);
        IO.o(""+5%4);
    }
}
