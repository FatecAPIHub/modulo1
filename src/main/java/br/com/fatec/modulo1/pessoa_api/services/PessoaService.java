package br.com.fatec.modulo1.pessoa_api.services;

import br.com.fatec.modulo1.pessoa_api.model.Pessoa;
import br.com.fatec.modulo1.pessoa_api.repository.PessoaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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
                throw new IllegalArgumentException("Pessoa não pode ser nula");
            }

            if (pessoa.getId() != null) {
                logger.warn("Tentativa de salvar pessoa com ID já definido: {}", pessoa.getId());
                throw new IllegalArgumentException("Nova pessoa não deve ter ID");
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

        } catch (IllegalArgumentException e) {
            logger.error("Validação falhou ao salvar pessoa: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao salvar pessoa", e);
            throw new RuntimeException("Erro ao salvar pessoa", e);
        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("saveDuration");
        }
    }

    @Transactional
    public boolean deletarPorId(Long id) {
        MDC.put("operation", "deletarPessoa");
        MDC.put("pessoaId", String.valueOf(id));

        try {
            logger.info("Tentando deletar pessoa: ID={}", id);
            if (id == null || id <= 0) {
                logger.warn("ID inválido fornecido para deleção: {}", id);
                return false;

            }
            Optional<Pessoa> pessoaOpt = pessoaRepository.findById(id);

            if (pessoaOpt.isEmpty()) {
                logger.warn("Pessoa não encontrada para deleção: ID={}", id);
                return false;
            }

            Pessoa pessoa = pessoaOpt.get();
            MDC.put("pessoaNome", pessoa.getNome());

            // Soft delete - apenas marca como inativo
            // pessoa.setAtivo(false);
            // pessoaRepository.save(pessoa);

            pessoaRepository.deleteById(pessoa.getId());

            logger.info("Pessoa deletada (soft delete) com sucesso: ID={}, nome={}",
                    id, pessoa.getNome());

            return true;

            // Se quiser hard delete, use:
            // pessoaRepository.deleteById(id);
            // logger.info("Pessoa deletada permanentemente: ID={}", id);

        } catch (Exception e) {
            logger.error("Erro ao deletar pessoa: ID={}", id, e);
            throw new RuntimeException("Erro ao deletar pessoa", e);
        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("pessoaNome");
        }
    }

    @Transactional
    public boolean atualizar(Pessoa pessoa) {
        MDC.put("operation", "atualizarPessoa");

        try {
            // Validação
            if (pessoa == null) {
                logger.error("Tentativa de atualizar pessoa nula");
                throw new IllegalArgumentException("Pessoa não pode ser nula");
            }

            if (pessoa.getId() == null) {
                logger.error("Tentativa de atualizar pessoa sem ID");
                throw new IllegalArgumentException("ID da pessoa é obrigatório para atualização");
            }

            MDC.put("pessoaId", String.valueOf(pessoa.getId()));

            logger.info("Tentando atualizar pessoa: ID={}", pessoa.getId());

            Optional<Pessoa> pessoaExistenteOpt = pessoaRepository.findById(pessoa.getId());

            if (pessoaExistenteOpt.isEmpty()) {
                logger.warn("Pessoa não encontrada para atualização: ID={}", pessoa.getId());
                return false;
            }

            Pessoa pessoaExistente = pessoaExistenteOpt.get();
            MDC.put("pessoaNomeAnterior", pessoaExistente.getNome());
            MDC.put("pessoaNomeNovo", pessoa.getNome());

            long startTime = System.currentTimeMillis();
            pessoaRepository.save(pessoa);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Pessoa atualizada com sucesso: ID={}, nome='{}' -> '{}' ({}ms)",
                    pessoa.getId(),
                    pessoaExistente.getNome(),
                    pessoa.getNome(),
                    duration);

            return true;

        } catch (IllegalArgumentException e) {
            logger.error("Validação falhou ao atualizar pessoa: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao atualizar pessoa: ID={}",
                    pessoa != null ? pessoa.getId() : "null", e);
            throw new RuntimeException("Erro ao atualizar pessoa", e);
        } finally {
            MDC.remove("operation");
            MDC.remove("pessoaId");
            MDC.remove("pessoaNomeAnterior");
            MDC.remove("pessoaNomeNovo");
        }
    }
}
