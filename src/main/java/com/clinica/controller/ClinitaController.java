package com.clinica.controller;

import com.clinica.model.Cita;
import com.clinica.model.Consultorio;
import com.clinica.model.Doctor;
import com.clinica.repository.ConsultorioRepository;
import com.clinica.repository.DoctorRepository;
import com.clinica.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/citas")
public class ClinitaController {

    @Autowired
    private ClinicaService citaService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @GetMapping("/agendar")
    public String mostrarFormularioCita(Model model) {
        model.addAttribute("doctores", doctorRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "formulario_agendar_cita";
    }


    @PostMapping("/agendar")
    public String agendarCita(
            @RequestParam Long doctorId,
            @RequestParam Long consultorioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horario,
            @RequestParam String nombrePaciente,
            Model model) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Consultorio consultorio = consultorioRepository.findById(consultorioId).orElse(null);

        if (doctor == null || consultorio == null) {
            model.addAttribute("mensaje", "Doctor o Consultorio no encontrados.");
            return "resultado";
        }

        Cita cita = new Cita();
        cita.setDoctor(doctor);
        cita.setConsultorio(consultorio);
        cita.setHorario(horario);
        cita.setNombrePaciente(nombrePaciente);

        if (citaService.validarCita(consultorio, doctor, horario, nombrePaciente)) {
            citaService.agendarCita(cita);
            model.addAttribute("mensaje", "Cita agendada exitosamente.");
        } else {
            model.addAttribute("mensaje", "No se puede agendar la cita debido a las reglas de validación.");
        }
        return "resultado";
    }


    @GetMapping("/consultarPorDoctor")
    public String mostrarFormularioConsultaDoctor(Model model) {
        model.addAttribute("doctores", doctorRepository.findAll());
        return "formulario_consulta_doctor";
    }

    @PostMapping("/consultarPorDoctor")
    public String consultarCitasPorDoctor(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            model.addAttribute("mensaje", "Doctor no encontrado.");
            return "resultado";
        }

        List<Cita> citas = citaService.obtenerCitasPorDoctorYFecha(doctor, fecha.atStartOfDay());
        model.addAttribute("citas", citas);
        return "consulta_citas_doctor"; // Vista para mostrar las citas del doctor
    }


    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Cita cita = citaService.obtenerCitaPorId(id);
        if (cita == null) {
            model.addAttribute("mensaje", "Cita no encontrada.");
            return "resultado";
        }
        model.addAttribute("cita", cita);
        model.addAttribute("doctores", doctorRepository.findAll());
        model.addAttribute("consultorios", consultorioRepository.findAll());
        return "formulario_editar_cita"; // Vista para editar una cita
    }

    @PostMapping("/editar/{id}")
    public String editarCita(
            @PathVariable Long id,
            @RequestParam Long doctorId,
            @RequestParam Long consultorioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horario,
            @RequestParam String nombrePaciente,
            Model model) {

        Cita cita = citaService.obtenerCitaPorId(id);
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Consultorio consultorio = consultorioRepository.findById(consultorioId).orElse(null);

        if (cita == null || doctor == null || consultorio == null) {
            model.addAttribute("mensaje", "Datos incorrectos.");
            return "resultado";
        }

        cita.setDoctor(doctor);
        cita.setConsultorio(consultorio);
        cita.setHorario(horario);
        cita.setNombrePaciente(nombrePaciente);

        if (citaService.validarCita(consultorio, doctor, horario, nombrePaciente)) {
            citaService.agendarCita(cita); // Usamos agendar para guardar los cambios
            model.addAttribute("mensaje", "Cita editada exitosamente.");
        } else {
            model.addAttribute("mensaje", "No se puede editar la cita debido a las reglas de validación.");
        }
        return "resultado"; // Vista para mostrar el resultado
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, Model model) {
        Cita cita = citaService.obtenerCitaPorId(id);
        if (cita != null && cita.getHorario().isAfter(LocalDateTime.now())) {
            citaService.cancelarCita(id);
            model.addAttribute("mensaje", "Cita cancelada exitosamente.");
        } else {
            model.addAttribute("mensaje", "No se puede cancelar la cita. Ya pasó el horario.");
        }
        return "resultado"; // Vista para mostrar el resultado
    }

}
