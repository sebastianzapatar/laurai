package com.nomelestar.lauracanceleatiempo.dishes.models;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="chef_id",nullable = false)
    private Chef chef;
    //Laura Isabel pierde en 0

}
