package br.senac.tads.dsw.dados_pessoais;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public List<Pessoa> obterPessoas() {
        return pessoaService.obterPessoas();
    }

    @GetMapping("/{username}")
    public Pessoa obterPessoa(@PathVariable("username") String username) {
        Optional<Pessoa> optPessoa = pessoaService.obterPessoa(username);
        if (optPessoa.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optPessoa.get();
    }

    @PostMapping
    public ResponseEntity<Pessoa> incluirNovaPessoa(@RequestBody Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaService.incluirNovaPessoa(pessoa);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(pessoaSalva.getUsername())
                .toUri();
        return ResponseEntity.created(location).body(pessoaSalva);
    }
}