package br.com.fatec.modulo1.pessoa_api.controller;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.services.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class PessoaController {
    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @GetMapping("/get")
    public List<Pessoa> getPessoa() { return service.listar(); }
}
