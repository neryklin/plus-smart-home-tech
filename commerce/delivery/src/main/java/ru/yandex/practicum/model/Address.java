package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID addressId;
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}