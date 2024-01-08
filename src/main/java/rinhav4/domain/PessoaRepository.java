package rinhav4.domain;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PessoaRepository extends CrudRepository<Pessoa, UUID> {

    Optional<Pessoa> findById(UUID id);

    Optional<Pessoa> findByApelido(String apelido);

//    @Query(value = "select * from pessoa p where " +
//            "p.nome ilike concat('%',:t,'%') or " +
//            "p.apelido ilike concat('%',:t,'%') or " +
//            "p.stack ilike concat('%',:t,'%')", nativeQuery = true)
//    List<Pessoa> buscaPorTermo(String t);
}
