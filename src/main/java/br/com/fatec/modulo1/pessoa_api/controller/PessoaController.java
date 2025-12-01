package br.com.fatec.modulo1.pessoa_api.controller;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.services.PessoaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping(path = "/api")
public class PessoaController {
    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    private static final Logger logger = LoggerFactory.getLogger(PessoaController.class);

    @GetMapping
    public ResponseEntity<Page<Pessoa>> listar(@RequestParam(defaultValue = "0") int pagina) {
        logger.debug("Controller: listando pessoas - página {}", pagina);
        Page<Pessoa> pessoas = service.listar(pagina);
        return ResponseEntity.ok(pessoas);
    }

    @PostMapping
    public ResponseEntity<Pessoa> salvar(@RequestBody Pessoa pessoa) {
        logger.debug("Controller: salvando pessoa {}", pessoa);
        Pessoa pessoaSalva = service.salvar(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long id, @RequestBody Pessoa pessoa) {
        logger.debug("Controller: atualizando pessoa - ID {}", id);
        pessoa.setId(id);
        Pessoa pessoaAtualizada = service.atualizar(pessoa);
        return ResponseEntity.ok(pessoaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logger.debug("Controller: deletando pessoa - ID {}", id);
        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
