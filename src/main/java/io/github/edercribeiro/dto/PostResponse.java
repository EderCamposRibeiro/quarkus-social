package io.github.edercribeiro.dto;

import io.github.edercribeiro.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        //Também podemos utilizar a variável implícita do Java 11;
        var response = new PostResponse();
        //PostResponse response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }
}
