public class RemoveDuplicate {
    public static void removeMethod(String s) {
        StringBuffer sb=new StringBuffer();
        int len=s.length();
        int i=0;
        boolean flag=false;
        for(i=0; i<len;i++) {
            char c = s.charAt(i);
            /*if(s.indexOf(c)!=s.lastIndexOf(c)){
                flag=false;
            }else{
                flag=true;
            }*/
            if (i == s.indexOf(c)){

            System.out.println("位置：");
            System.out.println(s.indexOf(c));
            System.out.println("i:");
            System.out.println(i);
                sb.append(c);
            }

        }
        System.out.print(sb.toString());
    }

    public static void main(String[] args) {
        RemoveDuplicate.removeMethod("abbaaccaaddaadawdawdawdawdawdsdeawdawdr");

    }
}
