package com.nomelestar.lauracanceleatiempo.dishes.repository;

import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByChefId(Long chefId);

}
