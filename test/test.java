import java.io.*;

class test {
    public static void main(String[] args){
        int block = 2;
        for(int i = 0; i < 10; i += block){
            for(int j = 0; j < block; j++){
                System.out.println("Oi");
            }
            System.out.println(i);
        }
    }
}