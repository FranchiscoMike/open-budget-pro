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
    private boolean codeSent; // agar unga code yuborilgangan bo'lsa true bo'ladi
    private boolean verified; // agar codi to'g'ri bo'lsa verify bo'ladi
    private boolean paid;  // agar unga pay qilinsa true bo'ladi
    private String phoneNumber;
    private String code;
    @CreationTimestamp
    private Timestamp createdAt;

    // agar unga code yuborilganda to'g'ri bo'lsa codeSent = true bo'ladi
    // agar unga paid bo'lsa paid = true bo'ladi
}
