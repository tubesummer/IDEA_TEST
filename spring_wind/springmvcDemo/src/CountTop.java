import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class CountTop {
    private static final String filePath = "D:\\userdata\\xianyu\\My Documents\\MyJabberFiles\\xiangwei.yu.ext@nokia.com\\String.txt";

    public static void main(String[] args) {

        //读取文件
        BufferedReader bReader = null;
        StringBuffer sBuffer = null;

        try {

            bReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));

            sBuffer = new StringBuffer();

            String line = null;
            while ((line = bReader.readLine())!=null) {

                sBuffer.append(line);

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }finally {

            if (bReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        String data = new String(sBuffer);

        data.trim();

        System.out.println("文件的内容===》"+data);

        System.out.println(data.length());

        //解析文本内容不统计所有文集或者字符出现的次数

        Set<Character> set = new HashSet<>();

        List<Character> list = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {

//			System.out.println(data.charAt(i));

            set.add(data.charAt(i));

            list.add(data.charAt(i));

        }

        for (Character character : set) {

            System.out.println(character);

            System.out.println(character+"  :  "+Collections.frequency(list,character));

        }

        System.out.println(set.size());

        System.out.println(list.size());



    }
}
