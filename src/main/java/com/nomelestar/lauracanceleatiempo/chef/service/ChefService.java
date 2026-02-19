package com.nomelestar.lauracanceleatiempo.chef.service;

import com.nomelestar.lauracanceleatiempo.chef.dto.ChefCreateDTO;
import com.nomelestar.lauracanceleatiempo.chef.dto.ChefResponseDTO;
import com.nomelestar.lauracanceleatiempo.chef.mapper.ChefMapper;
import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import com.nomelestar.lauracanceleatiempo.chef.repository.ChefRepository;
import com.nomelestar.lauracanceleatiempo.excepciones.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChefService {

    private final ChefRepository chefRepository;
    /*@Autowired
    private ChefRepository cRepo;
    */

    public ChefService(ChefRepository chefRepository) {
        this.chefRepository = chefRepository;
    }
    public ChefResponseDTO create(ChefCreateDTO dto){
        Chef chef = ChefMapper.toChef(dto);
        Chef saved=chefRepository.save(chef);
        return ChefMapper.toChefResponseDTO(saved);
    }
    public List<ChefResponseDTO> findAll(){
        return chefRepository.findAll().stream()
                .map(ChefMapper::toChefResponseDTO)
                .toList();
    }
    public ChefResponseDTO findById(Long id){
        Chef chef=chefRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Chef not found"));
        return ChefMapper.toChefResponseDTO(chef);
    }

}
