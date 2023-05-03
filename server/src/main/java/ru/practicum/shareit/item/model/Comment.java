package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return id != null && Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
