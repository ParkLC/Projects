import java.util.*;
import java.io.*;

/** Parker Carlson
 *  CSCI 405
 *  Description: This program solves the problem of the Weighted Activity Selection Problem. 
 *  It achieves this in O(n^2), with the core of the algorithm placed in printMaxTime(). 
 *  The program takes ina  text file, with activities formatted in this way: A:[58,91)	B:[99,100)	C:[12,47)...
 *  and must have a text file formatted in this way otherwise it will not work properly.
 */
public class ActivitySelection
{
	public static void main(String[] args) throws FileNotFoundException
	{
        if(args.length != 1){
            System.out.println("Proper input:\njava ActivitySelection Input_File.txt");
            System.exit(0);
        }
        
        /**
         * This portion is getting the info from the txt file and putting it into the correct arrays.
         * ******************************************************************************************
         */
        File input = new File(args[0] + "");
        Scanner sc = new Scanner(input);
        
        ArrayList<Integer> s = new ArrayList<>();
        ArrayList<Integer> f = new ArrayList<>();
        ArrayList<String> a = new ArrayList<>();

        while(sc.hasNextLine()){

            char[] line = sc.nextLine().toCharArray();

            String fill = "";

            for(char c: line){
                if(c == ':'){
                    a.add(fill);
                    fill = "";
                    continue;
                }else if(c == ','){
                    s.add(Integer.parseInt(fill));
                    fill = "";
                    continue;
                }else if(c == ')'){
                    f.add(Integer.parseInt(fill));
                    fill = "";
                    continue;
                }else if(c == '\t' || c == '[' || c == ' '){
                    continue;
                }
                fill = fill + c;
            }

            
        }
        sc.close();
        int n = s.size();
        
        int[] start = new int[n];
        int[] finish = new int[n];
        int[] weight = new int[n];
        String[] activity = new String[n];

        for(int i = 0; i < n; i++){
            
        start[i] = s.get(i);
        finish[i] = f.get(i);
        weight[i] = finish[i] - start[i];
        activity[i] = a.get(i);
        }

        int tempf = 0;
        int temps = 0;
        int tempw = 0;
        String tempa = "";
        for(int i = 0; i < n; i++){
            for(int j = i+1; j < n; j++){
                if(finish[i] > finish[j]){
                    tempf = finish[i];
                    temps = start[i];
                    tempw = weight[i];
                    tempa = activity[i];

                    finish[i] = finish[j];
                    start[i] = start[j];
                    weight[i] = weight[j];
                    activity[i] = activity[j];

                    finish[j] = tempf;
                    start[j] = temps;
                    weight[j] = tempw;
                    activity[j] = tempa;
                }
            }
        }
        /**
         * *************************************************************************************
         */
        int j = printMaxTime(start, finish, weight, activity, n);
	}
	
    public static int printMaxTime(int[] s, int[] f, int[] w, String[] a, int n)
    {
        int l = w.length;
        int[] acc_w = new int[w.length];// This is to hold the max accumulated time of the ride at that index. It will be calculated using all previous rides that matchup with its time.
        String[] acc_path = new String[w.length];// This will hold the path of accumulated rides up until the current ride at said index.
        for(int i = 0; i < l; i++){// We make a copy of w and a in acc_w, and acc_a. We use these as the starting values.
            acc_w[i] = w[i];
            acc_path[i] = "" + a[i] + " ";
        }

        for(int i = 1; i < n; i++){
            for(int j = 0; j < i; j++){
                if(f[j] <= s[i]){// If finish time of Activity j is <= start time of activity i
                    if((acc_w[j] + w[i]) > acc_w[i]){// acc_w[j] + w[i] represents the accumulated value of doing these 2 activities together. We compare it against the best current accumulated weight currently for that activity stored in acc_w[i].
                        acc_w[i] = (acc_w[j] + w[i]);// If the check passes we then update acc_w[i] with its new current best accumulated weight of: acc_w[j] + w[i]
                        acc_path[i] = acc_path[j] + a[i] + " ";// We also update the accumulated path for the best accumulated weight at position i. This will hold which other activities we have done prior to the current one at position i.
                    }
                }
            }
        }

        // We then find the max accumulated weight out of all activities. And also pick out its accumulated path.
        int max = acc_w[0];
        String path = acc_path[0];
        l = acc_w.length;
        for(int i = 0; i < l; i++){
            if(acc_w[i] > max){
                max = acc_w[i];
                path = acc_path[i];
            }
        }

        System.out.println("maximum time spent on rides is: " + max + "\nThe order of rides is: " + path);
        return max;
    }
}
