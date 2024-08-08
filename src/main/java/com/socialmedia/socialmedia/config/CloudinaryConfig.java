package com.socialmedia.socialmedia.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", "dfyw7op99",
                        "api_key", "623896555626394",
                        "api_secret", "CxwfkRCei1tx1Rwh43ARkeU1KNA",
                        "secure", true));
    }
}



