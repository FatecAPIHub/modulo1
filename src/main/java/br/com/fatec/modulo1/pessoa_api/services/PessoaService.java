package br.com.fatec.modulo1.pessoa_api.services;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PessoaService {
    public final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public List<Pessoa> listar() { return pessoaRepository.findAll(); }
}
