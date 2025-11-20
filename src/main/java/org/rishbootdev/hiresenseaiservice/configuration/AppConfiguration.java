package org.rishbootdev.hiresenseaiservice.configuration;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public ChatClient getChatClient(ChatClient.Builder builder){
        return builder.build();
    }
}
