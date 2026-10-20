package br.senac.tads.dsw.dados_pessoais;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	private final PessoaService pessoaService;

	public PessoaController(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}

	@GetMapping("/busca")
	public ResponseEntity<List<PessoaDto>> buscarPessoas(@RequestParam("q") String termo) {
		return ResponseEntity.ok(pessoaService.buscarPessoas(termo));
	}

	@GetMapping
	public List<PessoaDto> obterPessoas() {
		return pessoaService.obterPessoas();
	}

	@GetMapping("/{username}")
	public PessoaDto obterPessoa(@PathVariable("username") String username) {
		Optional<PessoaDto> optPessoa = pessoaService.obterPessoa(username);
		if (optPessoa.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return optPessoa.get();
	}

	@PostMapping("/sem-validacao")
	public ResponseEntity<?> incluirNovo(@RequestBody PessoaDto pessoa) {
		pessoaService.incluirNovaPessoa(pessoa);
		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/pessoas/{username}")
			.buildAndExpand(pessoa.getUsername())
			.toUri();
		return ResponseEntity.created(location).build();
	}

	@PostMapping
	public ResponseEntity<?> incluirComValidacao(@RequestBody @Valid PessoaDto pessoa) {
		pessoaService.incluirNovaPessoa(pessoa);
		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/pessoas/{username}")
			.buildAndExpand(pessoa.getUsername())
			.toUri();
		return ResponseEntity.created(location).body(pessoa);
	}

	@PutMapping("/{username}")
	public ResponseEntity<?> atualizar(@PathVariable("username") String username,
									   @RequestBody @Valid PessoaAlteracaoDto pessoa) {
		PessoaDto pessoaAlterada = pessoaService.alterarPessoa(username, pessoa);
		return ResponseEntity.ok().body(pessoaAlterada);
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<?> remover(@PathVariable("username") String username) {
		pessoaService.removerPessoa(username);
		return ResponseEntity.noContent().build(); // HTTP 204
	}

	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<ProblemDetail> trataExcecao(NaoEncontradoException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
			HttpStatusCode.valueOf(404), ex.getMessage());
		return ResponseEntity.of(pd).build();
	}
}
