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
        Double i=IO.i();
        IO.o(i+"");
    }
}
