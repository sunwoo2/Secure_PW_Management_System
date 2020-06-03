import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        while(act!=3){
            out.println("Select one you want: ");
            out.println("1. Add id/pw");
            out.println("2. Search pw");
            out.println("3. Quit");
            act = sc.nextInt();
            switch (act){
                case 1: add(); break;
                case 2: search(); break;
                case 3: exit(); break;
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
            writer = new ObjectOutputStream(new FileOutputStream(pwTxt, true));   // hashmap을 그대로 직렬화로 저장 하니깐, 덮어쓰기 불가능하게하고 맵에 데이터 추가한다음 그걸 그대로 다시 씀
            writer.writeObject(map);
            reader = new ObjectInputStream(new FileInputStream(pwTxt));
        }else{
            writer = new ObjectOutputStream(new FileOutputStream(pwTxt, true));
            out.print("password: ");
            pw = sc.next();
            reader = new ObjectInputStream(new FileInputStream(pwTxt));
            map = (HashMap)reader.readObject();
            String truePW = map.get("this");
            while(!pw.equals(truePW)){
                out.println("sorry, it's incorrect");
                out.print("password: ");
                pw = sc.next();
            }
        }
    }

    public void exit() throws IOException
    {
        out.println("Thank you to use. Good bye (~^~^~)");
        writer.close();
    }

    public void add() throws IOException
    {
        map.put("tesaat", "03003");
        map.put("ubuntu", "emergentproperties33");
        writer.writeObject(map);
    }

    public void search() throws IOException, ClassNotFoundException
    {
        out.println(map.values());
    }
}