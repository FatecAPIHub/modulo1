package br.com.fatec.modulo1.pessoa_api.services;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {
    public final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Page<Pessoa> listar(int pagina) {
        PageRequest paginacao = PageRequest.of(pagina, 10); // página, tamanho
        return pessoaRepository.findByAtivoTrue(paginacao);
    }

//    public Optional<Pessoa> listarPorId(long id) { return pessoaRepository.findById(id); }

    public Pessoa salvar(Pessoa p) { return pessoaRepository.save(p);}

    public boolean deletarPorId(Long id)
    {
        if (pessoaRepository.existsById(id))
        {
            pessoaRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public boolean atualizar(Pessoa p) {
        if (!pessoaRepository.existsById(p.getId())) {
            return false;
        }
        pessoaRepository.save(p);
        return true;
    }
}
