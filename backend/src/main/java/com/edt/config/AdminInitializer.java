
package com.edt.config;

import com.edt.entity.Administrateur;
import com.edt.entity.enums.Role;
import com.edt.repository.AdministrateurRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(
            AdministrateurRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (repository.findByEmail("admin@edplanning.com").isEmpty()) {

                Administrateur admin = new Administrateur();

                admin.setNom("Admin");
                admin.setPrenom("Principal");
                admin.setEmail("admin@edplanning.com");
                admin.setMotDePasse(
                        passwordEncoder.encode("Admin@12345")
                );
                admin.setRole(Role.ADMINISTRATEUR);

                repository.save(admin);

                System.out.println("Administrateur créé !");
            }
        };
    }
}
