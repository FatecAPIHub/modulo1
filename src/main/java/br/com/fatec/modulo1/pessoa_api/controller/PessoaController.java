package br.com.fatec.modulo1.pessoa_api.controller;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.services.PessoaService;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class PessoaController {
    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<Pessoa>> listar(
            @RequestParam(defaultValue = "0") int pagina) {
        return ResponseEntity.ok(service.listar(pagina));
    }

    @PostMapping
    public Pessoa salvar(@RequestBody Pessoa pessoa) { return service.salvar(pessoa); }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        if (service.deletarPorId(id)) {
            return ResponseEntity.ok("Pessoa deletada com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pessoa com ID " + id + " não encontrada.");
        }
    }

    @PutMapping
    public ResponseEntity<String> atualizar(@RequestBody Pessoa pessoa) {
        if (service.atualizar(pessoa))
            return ResponseEntity.ok("Pessoa Atualizada com sucesso");
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pessoa não encontrada.");
        }
    }
}
