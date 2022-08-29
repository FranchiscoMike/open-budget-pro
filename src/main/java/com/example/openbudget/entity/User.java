package com.example.openbudget.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    private BotUser botUser;
    @ManyToOne
    private Project project;
    private boolean done;
    private boolean Paid;
    private String phoneNumber;
    private String code;
    @CreationTimestamp
    private Timestamp createdAt;
}
