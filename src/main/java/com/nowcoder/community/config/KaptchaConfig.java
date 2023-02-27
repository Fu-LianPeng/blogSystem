package com.nowcoder.community.config;


import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.config-2022-08-03 21:15
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha kaptchaProducer(){
        Properties properties = new Properties();
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT,"40");
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH,"100");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE,"32");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR,"0,0,0");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH,"4");
        properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
