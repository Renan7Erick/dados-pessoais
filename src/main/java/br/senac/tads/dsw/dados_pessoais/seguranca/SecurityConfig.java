package br.senac.tads.dsw.dados_pessoais.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter; // [cite: 24]

    public SecurityConfig(JwtFilter jwtFilter) { // [cite: 24]
        this.jwtFilter = jwtFilter; // [cite: 24]
    } // [cite: 24]

    // Define a cadeia de filtros HTTP que o Spring Security aplica em cada
    // requisição.
    // A ordem das regras em authorizeHttpRequests importa: a primeira regra que
    // casar é aplicada.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF - não é necessário em APIs REST stateless
                // (CSRF protege formulários HTML com sessão; aqui usaremos JWT por requisição)
                .csrf(csrf -> csrf.disable()) // [cite: 24]

                // Permite renderização em frames - necessário para o console H2 funcionar no
                // navegador
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())) // [cite: 24]

                // Define a política de sessão como STATELESS: o servidor não cria nem usa
                // sessão HTTP.
                // Cada requisição deve ser autenticada de forma independente (via JWT no
                // header).
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // [cite: 24]

                // ---- Regras de autorização por URL ----
                .authorizeHttpRequests(auth -> auth
                        // Endpoint de login: sempre público (é aqui que o usuário obtém o token)
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // [cite: 24]
                        // Console H2: público durante o desenvolvimento (REMOVA em produção!)
                        .requestMatchers("/h2-console/**").permitAll() // [cite: 24]
                        // Swagger UI: público para facilitar testes
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // [cite:
                                                                                                              // 24]

                        // LINHAS ADICIONADAS AQUI
                        .requestMatchers("/me").authenticated() // [cite: 25]
                        .requestMatchers("/*.html", "/*.css", "/*.js").permitAll() // [cite: 25]

                        // Demais endpoints precisam de autenticação - MANTER SEMPRE COMO ÚLTIMA
                        // CONFIGURAÇÃO
                        .anyRequest().authenticated()) // [cite: 25]

                // LINHA ADICIONADA AQUI (Registra o filtro)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // [cite: 25]

        return http.build(); // [cite: 25]
    }

    // AuthenticationManager: componente responsável por executar a autenticação.
    // DaoAuthenticationProvider é a implementação que usa um UserDetailsService
    // (nosso
    // UsuarioSistemaService) para buscar o usuário e um PasswordEncoder para
    // comparar a senha.
    // OBS: Esse código abaixo é válido para o Spring Boot 4 em diante.
    @Bean
    public AuthenticationManager authenticationManager( // [cite: 25]
            UserDetailsService userDetailsService, // [cite: 25]
            PasswordEncoder passwordEncoder) { // [cite: 25]
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService); // [cite: 25]
        provider.setPasswordEncoder(passwordEncoder); // [cite: 25]
        return new ProviderManager(provider); // [cite: 25]
    } // [cite: 25]

    // PasswordEncoder: define como as senhas são codificadas e comparadas.
    // ATENÇÃO: NoOpPasswordEncoder armazena senhas em TEXTO PURO.
    // Use APENAS para fins didáticos - será substituído por BCrypt na Etapa 7.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}