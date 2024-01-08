package rinhav4.domain;

import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final Set<String> apelidos;
    private final ConcurrentHashMap<String, Pessoa> pessoaCache;
    private final PessoaCustomRepositoryImpl pessoaCustomRepositoryImpl;
    private final StatefulRedisConnection<String, String> redisConnection;

    public PessoaService(PessoaRepository pessoaRepository, PessoaCustomRepositoryImpl pessoaCustomRepositoryImpl, StatefulRedisConnection<String, String> redisConnection) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaCustomRepositoryImpl = pessoaCustomRepositoryImpl;
        this.redisConnection = redisConnection;
        this.apelidos = new HashSet<>(22000);
        this.pessoaCache = new ConcurrentHashMap<>(22000);
    }

    @Transactional
    public String salvar(Pessoa pessoa) {
        UUID pessoaUUID = UUID.randomUUID();
        pessoa.setId(pessoaUUID);
        pessoa.validarDados();

        if(this.apelidos.contains(pessoa.getApelido())) {
            throw new IllegalStateException("Apelido já cadastrado");
        } else {
            this.apelidos.add(pessoa.getApelido());
        }

        if (Optional.ofNullable(pessoa.getStack()).isEmpty()) pessoa.setStack(Collections.emptyList());

        this.pessoaCache.put(pessoa.getId().toString(), pessoa);



        pessoaCustomRepositoryImpl.customSave(pessoa);

        return pessoa.getId().toString();
    }

    public Pessoa buscaPorId(UUID id) {
        if (this.pessoaCache.containsKey(id.toString())) {
            return this.pessoaCache.get(id.toString());
        }

        Optional<Pessoa> optionalPessoa = pessoaRepository.findById(id);

        if (optionalPessoa.isPresent()) {
            return optionalPessoa.get();
        }

        throw new IllegalStateException("Pessoa não encontrada");
    }

    public boolean buscaPorApelido(String apelido) {
        if (this.apelidos.contains(apelido)) {
            return true;
        }

        return pessoaRepository.findByApelido(apelido).isPresent();
    }

    public synchronized List<Pessoa> buscaPorTermo(String t) {
        List<Pessoa> pessoaList;

        synchronized (this.pessoaCache) {
            pessoaList = this.pessoaCache.values()
                    .parallelStream()
                    .filter(pessoa -> pessoa.getApelido().contains(t)
                            || pessoa.getNome().contains(t)
                            || String.join(" ,", pessoa.getStack()).contains(t)
                    )
                    .limit(50L)
                    .toList();
        }

        if (pessoaList.isEmpty()) {
            return pessoaCustomRepositoryImpl.findAllByTermoTsQuery(t);
        }

        return pessoaList;
    }

    public Long contagemPessoas() {
        return pessoaRepository.count();
    }
}
