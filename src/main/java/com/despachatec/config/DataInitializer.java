package com.despachatec.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.despachatec.models.Rol;
import com.despachatec.models.Usuario;
import com.despachatec.repositories.RolRepository;
import com.despachatec.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Inicializador de datos básicos del sistema.
 * Solo ejecuta la inicialización si no existen datos previos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@despachatec.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.name:Administrador}")
    private String adminName;

    @Override
    public void run(String... args) throws Exception {
        initializeBasicData();
    }

    private void initializeBasicData() {
        // Solo inicializar si no hay datos
        if (rolRepository.count() == 0) {
            initializeRoles();
        }

        if (usuarioRepository.count() == 0) {
            initializeAdminUser();
        }
    }

    private void initializeRoles() {
        // Crear roles básicos
        Rol adminRole = new Rol();
        adminRole.setNombre("ADMIN");
        rolRepository.save(adminRole);

        Rol userRole = new Rol();
        userRole.setNombre("USER");
        rolRepository.save(userRole);

        log.info("✓ Roles básicos inicializados: ADMIN, USER");
    }

    private void initializeAdminUser() {
        // Crear usuario administrador por defecto
        Rol adminRole = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

        Usuario admin = new Usuario();
        admin.setNombre(adminName);
        admin.setUsername(adminUsername);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));

        Set<Rol> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        usuarioRepository.save(admin);

        log.info("✓ Usuario administrador creado:");
        log.info("  Username: {}", adminUsername);
        log.info("  Email: {}", adminEmail);
        log.info("  Nombre: {}", adminName);
        log.info("  Rol: ADMIN");
        log.warn("⚠️  IMPORTANTE: Cambiar las credenciales por defecto en producción");
    }
}
