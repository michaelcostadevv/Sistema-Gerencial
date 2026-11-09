package com.erp.sistemagerencial.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Getter/@Setter em vez de @Data: o @Data gera equals/hashCode/toString com todos os campos,
// o que em entidades JPA pode causar loops infinitos e carregar relações LAZY sem querer.
@Entity
@Table(name = "tb_clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 14)
    private String cpfCnpj;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;
}
