package com.nomelestar.lauracanceleatiempo.chef.model;

import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name="chefs")
@Data
public class Chef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 120)
    private String description;
    @Column(nullable = false, length = 120)
    private String speciality;
    @OneToMany(mappedBy = "chef",cascade = CascadeType.ALL)
    private List<Dish> dishes;
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chef chef = (Chef) o;
        return Objects.equals(id, chef.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
