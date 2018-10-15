import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Excercise {

    // Complete the rotLeft function below.
    static int[] rotLeft(int[] a, int d) {
        if(a.length == 2*d){
            for (int j = 0; j < a.length/2; j++) {
                System.out.println(((a.length - d) + j)% a.length+","+ j);
                int temp = a[j];
                a[j] = a[((a.length - d) + j)% a.length];
                a[((a.length - d) + j)% a.length] = temp;
            }
        }else{
            int i = 0;
            int indx = 0;
            int val = a[0];
            while(d % a.length > 0 && i < a.length){
                int tarInd = ( (a.length - d) + indx) % a.length;
                System.out.println(tarInd+", "+ indx);
                int temp = a[tarInd];
                a[tarInd] = val;
                val = temp;

                i++;
            }
        }

        return a;

    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int[] a = {41, 73, 89, 7, 10, 1, 59, 58, 84, 77, 77, 97, 58, 1, 86, 58, 26, 10, 86, 51};
        rotLeft(a, 10);
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]+",");

        }

    }
}
