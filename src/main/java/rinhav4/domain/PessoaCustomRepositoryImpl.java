package rinhav4.domain;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.List;

@Singleton
@Transactional
public class PessoaCustomRepositoryImpl {

    private final EntityManager entityManager;

    private final PessoaStackConverter pessoaStackConverter;


    public PessoaCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.pessoaStackConverter = new PessoaStackConverter();
    }


    public List<Pessoa> search(String termo) {

        Query query = entityManager.createNativeQuery("select * from pessoa p where " +
                "p.nome ilike concat('%',:termo,'%') or " +
                "p.apelido ilike concat('%',:termo,'%') or " +
                "p.stack ilike concat('%',:termo,'%')", Pessoa.class);

        query.setParameter("termo", termo);

        return query.getResultList();
    }

    public String customSave(Pessoa pessoa) {

        Query query = entityManager.createNativeQuery(
                "insert into pessoa (id, apelido, nome, busca, nascimento, stack) " +
                        "values (:id, :apelido, :nome, to_tsvector(:busca), :nascimento, :stack)"
        );
        String stack = pessoaStackConverter.convertToDatabaseColumn(pessoa.getStack());

        query.setParameter("id", pessoa.getId());
        query.setParameter("apelido", pessoa.getApelido());
        query.setParameter("nome", pessoa.getNome());
        query.setParameter("busca", pessoa.getApelido() + ' ' + pessoa.getNome() + ' ' + stack);
        query.setParameter("nascimento", pessoa.getNascimento());
        query.setParameter("stack", stack);

        try {
            query.executeUpdate();
            return pessoa.getId().toString();
        } catch (Exception e) {
            throw new IllegalStateException("Apelido já cadastrado");
        }
    }

    public List<Pessoa> findAllByTermoTsQuery(String termo) {

        Query query = entityManager.createNativeQuery("select * from pessoa p where " +
                "p.busca @@ to_tsquery( :termo )", Pessoa.class);

        query.setParameter("termo", termo.replaceAll("(\\s)+", " | "));

        return query.getResultList();
    }
}
