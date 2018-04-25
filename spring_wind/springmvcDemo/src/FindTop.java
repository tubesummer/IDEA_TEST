import sun.awt.datatransfer.DataTransferer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class FindTop {
    private static final String filePath = "D:\\userdata\\xianyu\\My Documents\\MyJabberFiles\\xiangwei.yu.ext@nokia.com\\String.txt";
    public Map findTop(String param){
        char[] chars = param.toCharArray();
        System.out.println(chars);
        Long start = System.nanoTime();
        //byte[] cs = param.getBytes();
        //byte[] b = cs;
        Map<String, Integer> map=new HashMap();

        //System.out.println(Arrays.toString(b));
        //System.out.println(b.length);
        for (int i = 0; i < chars.length; i++) {
            int count = 0;
            for (int j = 0; j <chars.length ; j++) {
                if(chars[i]==chars[j]){
                   //map.put(String.valueOf(chars[i]),count++);
                    count++;
                }
            }
            map.put(String.valueOf(chars[i]),count);
        }
        /*System.out.println("通过Map.entrySet使用iterator遍历key和value：");
        Iterator<Map.Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());

        }*/
        Long end = System.nanoTime();
        System.out.println("用时"+(Long)(end-start));
        return map;

    }
    public void topTen(Map<String, Integer> map){
        Iterator<Map.Entry<String,Integer>> it = map.entrySet().iterator();
        List<Content> list = new ArrayList();

        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Content co = new Content((String)entry.getKey(),(Integer)entry.getValue());

            list.add(co);
            Collections.sort(list, new Comparator<Content>() {

                @Override
                public int compare(Content o1, Content o2) {
                    return o2.getCount()-o1.getCount();
                }
            });
        }
        for (int i = 0; i <10 ; i++) {
            /*System.out.print("字符为：");
            System.out.print(list.get(i).getKey());
            System.out.print("次数为：");
            System.out.print(list.get(i).getCount());
            System.out.println("    ");*/

        }
    }

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

        FindTop a=new FindTop();
        Map map=a.findTop(data);
        a.topTen(map);
    }
}


class Content{
    String key;
    Integer count;

    public Content(String key, Integer count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}