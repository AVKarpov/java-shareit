package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        //уникальный идентификатор комментария

    @Column(nullable = false, length = 512)
    private String text;    //содержимое комментария

    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;              //вещь, к которой относится комментарий

    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;            //автор комментария

    LocalDateTime created;  //дата создания комментария

}
