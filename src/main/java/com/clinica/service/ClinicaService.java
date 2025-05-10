package com.clinica.service;

import com.clinica.model.Cita;
import com.clinica.model.Consultorio;
import com.clinica.model.Doctor;
import com.clinica.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClinicaService {

    @Autowired
    private CitaRepository citaRepository;

    // Agendar una cita
    public void agendarCita(Cita cita) {
        citaRepository.save(cita);
    }

    public boolean validarCita(Consultorio consultorio, Doctor doctor, LocalDateTime horario, String nombrePaciente) {

        List<Cita> citasConsultorio = citaRepository.findByConsultorioAndHorario(consultorio, horario);
        if (!citasConsultorio.isEmpty()) {
            return false;
        }

        List<Cita> citasDoctor = citaRepository.findByDoctorAndHorario(doctor, horario);
        if (!citasDoctor.isEmpty()) {
            return false;
        }


        List<Cita> citasPaciente = citaRepository.findByNombrePacienteAndHorario(nombrePaciente, horario);
        for (Cita cita : citasPaciente) {
            if (Math.abs(horario.getHour() - cita.getHorario().getHour()) < 2) {
                return false;
            }
        }


        List<Cita> citasDoctorDia = citaRepository.findByDoctorAndHorarioBetween(doctor,
                horario.toLocalDate().atStartOfDay(), horario.toLocalDate().atTime(23, 59));
        if (citasDoctorDia.size() >= 8) {
            return false;
        }

        return true;
    }

    public List<Cita> obtenerCitasPorDoctorYFecha(Doctor doctor, LocalDateTime fecha) {
        return citaRepository.findByDoctor(doctor).stream()
                .filter(cita -> cita.getHorario().toLocalDate().equals(fecha.toLocalDate()))
                .toList();
    }

    public Cita obtenerCitaPorId(Long id) {
        return citaRepository.findById(id).orElse(null);
    }

    public void cancelarCita(Long id) {
        citaRepository.deleteById(id);
    }
}
