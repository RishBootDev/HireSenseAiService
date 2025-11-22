package org.rishbootdev.hiresenseaiservice.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public ChatClient getChatClient(ChatClient.Builder builder){
        return builder.build();
    }


    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
}
