package com.itzsrv.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table( name = "book_record")
public class Book {
    @NonNull
    private String name;
    private String author;
    private String summary;
    private int rating;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
}
