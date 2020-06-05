import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main
{
    public static void main(String[] args) throws Exception
    {
	    SPMS mine = new SPMS();
	    mine.start();
    }
}

class SPMS implements Serializable, Encryptable
{
    PrintStream out = System.out;
    Scanner sc = new Scanner(System.in);
    File pwTxt = new File("password.txt");
    ObjectOutputStream writer;
    ObjectInputStream reader;
    String thisPw;      // hashing 값 저장
    Map<String, String> map = new HashMap<>();

    public void start() throws Exception
    {
        login();
        out.println("========================");
        out.println("Welcome to SPSS (~^~^~)");
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

    public void login() throws IOException, ClassNotFoundException, NoSuchAlgorithmException
    {
        if(!pwTxt.exists()){
            out.print("Please create password to use: ");
            thisPw = sc.next();
            out.print("Reenter your password to confirm: ");
            while(!thisPw.equals(sc.next())){
                out.println("sorry, that's wrong");
                out.print("Reenter your password to confirm: ");
            }
            thisPw = bytesToHex(sha256(thisPw));    // encrypt with SHA-256
            map.put("this", thisPw);
        }else{
            out.print("password: ");
            thisPw = bytesToHex(sha256(sc.next()));
            reader = new ObjectInputStream(new FileInputStream(pwTxt));
            map = (HashMap)reader.readObject();     // 파일 읽어서 hashMap에 저장!
            String truePW = map.get("this");
            while(!thisPw.equals(truePW)){      // hash 값으로 비교
                out.println("sorry, it's incorrect");
                out.print("password: ");
                thisPw = bytesToHex(sha256(sc.next()));
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

    public void add() throws Exception
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
            map.put(key, Encrypt(pw, thisPw));  // AES로 암호화해서 저장. key는 비밀번호 hashing값
            out.println("Add successfully");
        }
    }

    public void search() throws Exception
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
        pw = Decrypt(map.get(key), thisPw);     // 복호화
        out.println("Password of " + key + " is " + pw);
    }

    public void showKeyList()
    {
        out.println("<List>");
        Set keySet = map.keySet();
        for(Object s : keySet) {
            if(!s.equals("this"))           // hide this own password
                out.println("- " + s);
        }
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

interface Encryptable
{
    // SHA-256
    default byte[] sha256(String msg) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());
        return md.digest();
    }

    default String bytesToHex(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder();
        for(byte b : bytes)
            builder.append(String.format("%02x", b));
        return builder.toString();
    }

    // AES
    default String Encrypt(String text, String key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);

        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(results);
    }

    default String Decrypt(String text, String key) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] keyBytes= new byte[16];
        byte[] b= key.getBytes("UTF-8");
        int len= b.length;
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);

        BASE64Decoder decoder = new BASE64Decoder();
        byte [] results = cipher.doFinal(decoder.decodeBuffer(text));
        return new String(results,"UTF-8");
    }
}