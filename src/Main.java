import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
	    SWSS mine = new SWSS();
	    mine.start();
    }
}

class SWSS implements Serializable
{
    PrintStream out = System.out;
    Scanner sc = new Scanner(System.in);
    File pwTxt = new File("password.txt");
    ObjectOutputStream writer;
    ObjectInputStream reader;
    String pw;
    Map<String, String> map = new HashMap<>();

    public void start() throws IOException, ClassNotFoundException
    {
        login();
        out.println("========================");
        out.println("Welcome to SWSS (~^~^~)");
        out.println("========================\n");
        int act = 0;
        while(act!=4){
            out.println("Select one you want: ");
            out.println("1. Add");
            out.println("2. Search");
            out.println("3. Delete");
            out.println("4. Quit");
            act = sc.nextInt();
            switch (act){
                case 1: add(); break;
                case 2: search(); break;
                case 3: delete(); break;
                case 4: exit(); break;
                default: break;
            }
            out.println();
        }
    }

    public void login() throws IOException, ClassNotFoundException
    {
        if(!pwTxt.exists()){
            out.print("Please create password to use: ");
            pw = sc.next();
            out.print("Reenter your password to confirm: ");
            while(!pw.equals(sc.next())){
                out.println("sorry, that's wrong");
                out.print("Reenter your password to confirm: ");
            }
            map.put("this", pw);
        }else{
            out.print("password: ");
            pw = sc.next();
            reader = new ObjectInputStream(new FileInputStream(pwTxt));
            map = (HashMap)reader.readObject();     // 파일 읽어서 hashMap에 저장!
            String truePW = map.get("this");
            while(!pw.equals(truePW)){
                out.println("sorry, it's incorrect");
                out.print("password: ");
                pw = sc.next();
            }
            reader.close();
        }
    }

    public void exit() throws IOException
    {
        out.println("Thank you to use. Good bye (~^~^~)");
        // 종료 할때 이때까지 작업한 hashMap 저장. 그 전 파일을 백업하는 의미.
        writer = new ObjectOutputStream(new FileOutputStream(pwTxt));  // hashMap serialization 때문에 그대로 저장하기 위해 "W" 모드로 파일 열기
        writer.writeObject(map);    // 종료전에 hashMap 쓰기
        writer.close();
    }

    public void add()
    {
        String key, pw;
        out.print("where? ");
        key = sc.next();
        while(map.containsKey(key)){
            out.println("sorry, it is already exist");
            out.print("where? ");
            key = sc.next();
        }
        out.print("input password: ");
        pw = sc.next();
        out.println(pw);
        out.println("Add? (y/n)");
        if(sc.next().equalsIgnoreCase("y")){
            map.put(key, pw);
            out.println("Add successfully");
        }
    }

    public void search()
    {
        String key, pw;
        showKeyList();
        out.print("Which one do you want to show?(quit) ");
        key = sc.next();
        if(key.equals("quit"))
            return ;
        while(!map.containsKey(key)){
            out.println("sorry, there is no " + key);
            out.print("Which one do you want to show?(quit) ");
            key = sc.next();
            if(key.equals("quit"))
                return ;
        }
        pw = map.get(key);
        out.println("Password of " + key + " is " + pw);
    }

    public void showKeyList()
    {
        out.println("<List>");
        Set keySet = map.keySet();
        for(Object s : keySet)
            out.println("- " + s);
    }

    public void delete()
    {
        String key;
        showKeyList();
        out.print("Which one do you want to delete?(quit) ");
        key = sc.next();
        if(key.equals("quit"))
            return ;
        while(!map.containsKey(key)){
            out.println("sorry, there is no " + key);
            out.print("Which one do you want to delete?(quit) ");
            key = sc.next();
            if(key.equals("quit"))
                return ;
        }
        out.println("Do you want to really delete it? (y/n)");
        if(sc.next().equalsIgnoreCase("y")){
            map.remove(key);
            out.println("Delete successfully");
        }
    }
}