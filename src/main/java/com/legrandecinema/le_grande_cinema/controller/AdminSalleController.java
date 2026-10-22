package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.SalleDto;
import com.legrandecinema.le_grande_cinema.mapper.SalleMapper;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/admin/salles")
public class AdminSalleController {

    private final SalleService salleService;

    public AdminSalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    public ResponseEntity<List<SalleDto>> getAllSalles(){

        List<SalleDto> salles = salleService
                .getAllSalles()
                .stream()
                .map(SalleMapper::toDto)
                .toList();

        return ResponseEntity.ok(salles);
    }
}
