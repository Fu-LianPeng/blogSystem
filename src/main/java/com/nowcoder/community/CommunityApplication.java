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
        SpringApplication.run(CommunityApplication.class, args);
    }
}
