package br.com.fatec.modulo1.pessoa_api.services;

import br.com.fatec.modulo1.pessoa_api.exceptions.ValidationException;
import br.com.fatec.modulo1.pessoa_api.exceptions.ResourceNotFoundException;
import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.repository.PessoaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {
    public final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(PessoaService.class);

    private static final int PAGE_SIZE = 10;

    public Page<Pessoa> listar(int pagina) {
        MDC.put("operation", "listarPessoas");
        MDC.put("pagina", String.valueOf(pagina));

        try {
            logger.info("Listando pessoas ativas - página {}", pagina);

            // Validação de entrada
            if (pagina < 0) {
                logger.warn("Número de página inválido: {}", pagina);
                pagina = 0;
            }
            PageRequest paginacao = PageRequest.of(
                    pagina,
                    PAGE_SIZE,
                    Sort.by("nome").ascending()
            );

            long startTime = System.currentTimeMillis();
            Page<Pessoa> resultado = pessoaRepository.findByAtivoTrue(paginacao);
            long duration = System.currentTimeMillis() - startTime;

            MDC.put("totalElements", String.valueOf(resultado.getTotalElements()));
            MDC.put("totalPages", String.valueOf(resultado.getTotalPages()));
            MDC.put("queryDuration", String.valueOf(duration));

            logger.info("Listagem concluída: {} pessoas encontradas em {} páginas ({}ms)",
                    resultado.getTotalElements(),
                    resultado.getTotalPages(),
                    duration);

            if (duration > 500) {
                logger.warn("Consulta lenta detectada ao listar pessoas");
            }

            return resultado;

        } catch (Exception e) {
            logger.error("Erro ao listar pessoas", e);
            throw e;
        } finally {
            MDC.remove("operation");
            MDC.remove("pagina");
            MDC.remove("totalElements");
            MDC.remove("totalPages");
            MDC.remove("queryDuration");
        }
    }

    @Transactional
    public Pessoa salvar(Pessoa pessoa) {
        MDC.put("operation", "salvarPessoa");

        try {
            if (pessoa == null) {
                logger.error("Tentativa de salvar pessoa nula");
                throw new ValidationException("Pessoa não pode ser nula");
            }

            if (pessoa.getNome().isEmpty()) {
                logger.error("Tentativa de salver pessoa sem nome");
                throw new ValidationException("O campo nome não estar vazio");
            }

            if (pessoa.getId() != null) {
                logger.warn("Tentativa de salvar pessoa com ID já definido: {}", pessoa.getId());
                throw new ValidationException("Nova pessoa não deve ter ID");
            }

            logger.info("Salvando nova pessoa: {}", pessoa.getNome());

            long startTime = System.currentTimeMillis();
            Pessoa pessoaSalva = pessoaRepository.save(pessoa);
            long duration = System.currentTimeMillis() - startTime;

            MDC.put("pessoaId", String.valueOf(pessoaSalva.getId()));
            MDC.put("saveDuration", String.valueOf(duration));

            logger.info("Pessoa salva com sucesso: ID={}, nome={} ({}ms)",
                    pessoaSalva.getId(),
                    pessoaSalva.getNome(),
                    duration);

            return pessoaSalva;
        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("saveDuration");
        }
    }

    @Transactional
    public void deletarPorId(Long id) {
        MDC.put("operation", "deletarPessoa");
        MDC.put("pessoaId", String.valueOf(id));

        try {
            logger.info("Tentando deletar pessoa: ID={}", id);

            if (id == null || id <= 0) {
                logger.warn("ID inválido fornecido para deleção: {}", id);
                throw new ValidationException("id", id, "ID deve ser um número positivo");
            }

            Pessoa pessoa = pessoaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pessoa", id));

            MDC.put("pessoaNome", pessoa.getNome());

            pessoaRepository.deleteById(pessoa.getId());

            logger.info("Pessoa deletada com sucesso: ID={}, nome={}", id, pessoa.getNome());

        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("pessoaNome");
        }
    }

    @Transactional
    public Pessoa atualizar(Pessoa pessoa) {
        MDC.put("operation", "atualizarPessoa");

        try {
            if (pessoa == null) {
                logger.error("Tentativa de atualizar pessoa nula");
                throw new ValidationException("Pessoa não pode ser nula");
            }

            if (pessoa.getId() == null) {
                logger.error("Tentativa de atualizar pessoa sem ID");
                throw new ValidationException("ID da pessoa é obrigatório para atualização");
            }

            MDC.put("pessoaId", String.valueOf(pessoa.getId()));

            logger.info("Tentando atualizar pessoa: ID={}", pessoa.getId());

            Pessoa pessoaExistente = pessoaRepository.findById(pessoa.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pessoa", pessoa.getId()));

            long startTime = System.currentTimeMillis();
            pessoaRepository.save(pessoa);
            long duration = System.currentTimeMillis() - startTime;

            Pessoa pessoaAtualizada = pessoaRepository.save(pessoa);

            logger.info("Pessoa atualizada com sucesso: ID={}, nome='{}' -> '{}' ({}ms)",
                    pessoa.getId(),
                    pessoaExistente.getNome(),
                    pessoa.getNome(),
                    duration);

            return pessoaAtualizada;
        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("pessoaNomeAnterior");
            MDC.remove("pessoaNomeNovo");
        }
    }
}
