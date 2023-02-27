package com.nowcoder.community;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.*;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.misc.Launcher;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {
    private static volatile int num = 1;

    @PostConstruct
    public void init() {
        System.setProperty("os.set.netty.runtime.available.processors", "false");
    }


    public static void main(String[] args) throws Exception {
        //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
        //Singleton1 instance = Singleton1.getInstance();
        //SpringApplication.run(CommunityApplication.class, args);
        //MyClassLoader classLoader = new MyClassLoader("C:\\test\\");
        System.out.println(ServiceLoader.class.getClassLoader());
        //System.out.println(aClass.getName());

    }
    static class MyClassLoader extends ClassLoader{
        private String path="";
        public MyClassLoader(String path){
            this.path=path;
        }
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            System.out.println(name);
            if("java.lang.String".equals(name))
                return findClass(name);
            return super.loadClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            String fullName=path+name.replace('.','\\')+".class";
            FileInputStream fileInputStream =null;
            ByteArrayOutputStream outputStream =null;
            try {
                fileInputStream = new FileInputStream(fullName);
                outputStream = new ByteArrayOutputStream();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int next;
            while(true){
                try {
                    if ((next=fileInputStream.read())==-1) break;
                    outputStream.write(next);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byte[] bytes = outputStream.toByteArray();
            return defineClass(name,bytes,0,bytes.length);
        }
    }

    public static int kmp(String s, String pattern) {
        int j = 0;
        int[] next = new int[pattern.length()];
        next(pattern, next);
        for (int i = 0; i < s.length(); i++) {
            while (j > 0 && s.charAt(i) != pattern.charAt(j)) j = next[j - 1];
            if (s.charAt(i) == pattern.charAt(j)) j++;
            if (j == pattern.length()) return i - j + 1;
        }
        return -1;
    }

    public static void next(String pattern, int[] next) {
        next[0] = 0;
        int j = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (j > 0 && pattern.charAt(j) != pattern.charAt(i)) j = next[j - 1];
            if (pattern.charAt(i) == pattern.charAt(j)) j++;
            next[i] = j;
        }
    }

}

class Main {
    public static void main(String[] arg) throws InterruptedException {
        ListNode head = new ListNode(1);
        ListNode node = head;
        int len = 20;
        int m = 10;
        int n = 20;
        int k = 1;//调用函数的参数
        while (k++ < len) {
            node.next = new ListNode(k);
            node = node.next;
        }
        head = reservenode(head, m, n);
        node = head;
        while (node != null) {
            Thread.sleep(100);
            System.out.print(node.val);
            System.out.print(",");
            node = node.next;
        }
        System.out.println();
    }

    public static ListNode reservenode(ListNode head, int m, int n) {
        if (n == m) return head;//不需要反转
        ListNode prev = null;
        ListNode cur = head;
        ListNode prefix = null;
        ListNode suffix = null;
        ListNode start = null;
        ListNode end = null;
        while (cur != null && n != 0) {
            if (m == 1) {
                end = cur;
                prefix = prev;
            } else if (n == 1) {
                start = cur;
                suffix = cur.next;
            }
            ListNode next = cur.next;
            if (m <= 1) cur.next = prev;
            prev = cur;
            cur = next;
            m--;
            n--;
        }
        end.next = suffix;
        if (prefix == null) return start;
        else {
            prefix.next = start;
            return head;
        }
        // write code here
    }

    public static class ListNode {
        int val;
        ListNode next = null;

        public ListNode(int val) {
            this.val = val;
        }
    }

}


//线程安全，实例在类初始化后一直存在内存，浪费资源
//反射攻击，反序列化攻击，克隆
class HunSingleton {

    private static HunSingleton instance = new HunSingleton();

    public static HunSingleton getInstance() {
        return instance;
    }
}

//延迟实例化，线程安全
//反射攻击，反序列化攻击，克隆
class Singleton {
    private static volatile Singleton Instance;

    private Singleton() {
    }

    public static Singleton getInstance() {
        if (Instance == null) {
            synchronized (Singleton.class) {
                if (Instance == null) Instance = new Singleton();
            }
        }
        return Instance;
    }
}

//延迟实例化，线程安全
//反射攻击，反序列化攻击，克隆
class Singleton1 {
    static {
        System.out.println("初始化");
    }

    private static class InstanceHolder {
        static Singleton1 instance = new Singleton1();//其实private也可以，因为外部类拥有对内部类的访问权限
    }

    public static Singleton1 getInstance() {
        return InstanceHolder.instance;//此时该内部类才会初始化，类初始化是线程安全的
    }
}

//线程安全，懒汉式
//无法反射攻击，无法序列化攻击
enum EnumSingleton {
    INSTANCE;
}

class LRUCache<k, v> extends LinkedHashMap<k, v> {
    private int maxLimit;

    public LRUCache(int limit) {
        super(limit, 0.75f, true);
        maxLimit = limit;
    }

    public v getOne(k key) {
        return get(key);
    }

    public void save(k key, v value) {
        put(key, value);
    }

    protected boolean removeEldestEntry(Map.Entry<k, v> eldest) {
        return super.size() > maxLimit;
    }

    public static void main(String[] args) {
        LRUCache<Integer, Character> cache = new LRUCache<Integer, Character>(3);
        cache.save(1, 'a');
        cache.save(2, 'b');
        cache.save(3, 'c');
        cache.getOne(1);
        cache.save(4, 'd');
        for (Map.Entry<Integer, Character> entry : cache.entrySet()) {
            System.out.println(entry);
        }
    }
}
