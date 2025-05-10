package com.clinica.repository;

import com.clinica.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByNombreAndApellidoPaterno(String nombre, String apellidoPaterno);
}
