package br.com.fatec.modulo1.pessoa_api.repository;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    Page<Pessoa> findByAtivoTrue(Pageable pageable);
}
