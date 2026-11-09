package br.senac.tads.dsw.dados_pessoais;

import br.senac.tads.dsw.dados_pessoais.seguranca.UsuarioSistema;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "bearerJwt")
public class ExemploSecurityController {

    private final UserDetailsService userDetailsService;

    public ExemploSecurityController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/me")
    public UsuarioSistema obterDadosUsuarioLogado(Authentication auth) {
        String username = (String) auth.getPrincipal();
        return (UsuarioSistema) userDetailsService.loadUserByUsername(username);
    }

    // ==== NOVAS ROTAS (Passo 7.5) ====

    // ... código anterior ...

    public record MensagemRoleDto(String usuario, String mensagem) {
    }

    @PreAuthorize("hasRole('ADMIN')") // <--- ADICIONAR
    @GetMapping("/acesso/admin")
    public MensagemRoleDto mensagemAdmin(Authentication auth) {
        return new MensagemRoleDto((String) auth.getPrincipal(), "Mensagem para usuário com Role ADMIN");
    }

    @PreAuthorize("hasRole('GERENTE')") // <--- ADICIONAR
    @GetMapping("/acesso/gerente")
    public MensagemRoleDto mensagemGerente(Authentication auth) {
        return new MensagemRoleDto((String) auth.getPrincipal(), "Mensagem para usuário com Role GERENTE");
    }

    @PreAuthorize("hasRole('USER')") // <--- ADICIONAR
    @GetMapping("/acesso/user")
    public MensagemRoleDto mensagemUser(Authentication auth) {
        return new MensagemRoleDto((String) auth.getPrincipal(), "Mensagem para usuário com Role USER");
    }
}