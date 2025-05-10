package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Consultorio;
import com.clinica.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByConsultorioAndHorario(Consultorio consultorio, LocalDateTime horario);
    List<Cita> findByDoctorAndHorario(Doctor doctor, LocalDateTime horario);
    List<Cita> findByNombrePacienteAndHorario(String nombrePaciente, LocalDateTime horario);
    List<Cita> findByDoctorAndHorarioBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    List<Cita> findByDoctor(Doctor doctor);
}
