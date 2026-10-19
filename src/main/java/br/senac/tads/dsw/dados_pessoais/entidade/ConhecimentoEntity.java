package br.senac.tads.dsw.dados_pessoais.entidade;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_conhecimentos")
public class ConhecimentoEntity {

    @Transient
    private String revisao = "2026-10-19";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    public ConhecimentoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}