package com.nomelestar.lauracanceleatiempo.chef.repository;

import com.nomelestar.lauracanceleatiempo.chef.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChefRepository
        extends JpaRepository<Chef, Long> {
    //Insertar
    //Eliminar
    //
    public Chef findByName(String name);
    //@Query("Consula")
}
