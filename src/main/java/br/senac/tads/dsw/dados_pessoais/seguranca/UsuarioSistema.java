package br.senac.tads.dsw.dados_pessoais.seguranca;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioSistema implements UserDetails {

    private final String username;
    private final String senha;
    private final List<GrantedAuthority> autoridades;

    public UsuarioSistema(String username, String senha, List<GrantedAuthority> autoridades) {
        this.username = username;
        this.senha = senha;
        this.autoridades = autoridades;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autoridades;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}