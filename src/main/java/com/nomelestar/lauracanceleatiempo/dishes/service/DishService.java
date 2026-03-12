package com.nomelestar.lauracanceleatiempo.dishes.service;

import com.nomelestar.lauracanceleatiempo.chef.mapper.ChefMapper;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.chef.service.ChefService;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishCreateDTO;
import com.nomelestar.lauracanceleatiempo.dishes.dto.DishResponseDTO;
import com.nomelestar.lauracanceleatiempo.dishes.mapper.DishMapper;
import com.nomelestar.lauracanceleatiempo.dishes.models.Dish;
import com.nomelestar.lauracanceleatiempo.dishes.repository.DishRepository;
import com.nomelestar.lauracanceleatiempo.excepciones.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final ChefService chefService;

    public DishResponseDTO create(DishCreateDTO dto){
        Chef chef= ChefMapper.toChefResponse(chefService.findById(dto.chefId()));
        Dish dish= DishMapper.toEntity(dto,chef);
        dishRepository.save(dish);
        return DishMapper.toDto(dish);

    }
    public DishResponseDTO findById(Long id){
        Dish dish=dishRepository.findById(id).orElseThrow(()->
                new NotFoundException("Parido"));
        return DishMapper.toDto(dish);
    }
    public List<DishResponseDTO> findAll(){
        List<Dish> dishes=dishRepository.findAll();
        return dishes.stream().map(DishMapper::toDto).toList();
    }
    public List<DishResponseDTO> findByChef(Long chefId){
        List<Dish> dishes=dishRepository.findByChefId(chefId);
        return dishes.stream().map(DishMapper::toDto).toList();
    }

}
