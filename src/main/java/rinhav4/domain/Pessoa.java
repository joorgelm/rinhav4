package rinhav4.domain;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.internal.util.StringHelper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

@Serdeable
@Entity(name = "Pessoa")
public class Pessoa {

    @Id
    private UUID id;

    @Column(unique = true)
    private String apelido;

    private String nome;

    private String nascimento;

    @Convert(converter = PessoaStackConverter.class)
    private List<String> stack;

    public Pessoa(UUID id, String apelido, String nome, String nascimento, List<String> stack) {
        this.id = id;
        this.apelido = apelido;
        this.nome = nome;
        this.nascimento = nascimento;
        this.stack = stack;
    }

    public Pessoa() {
    }

    public UUID getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

    public String getNome() {
        return nome;
    }

    public String getNascimento() {
        return nascimento;
    }

    public List<String> getStack() {
        return stack;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public void setStack(List<String> stack) {
        this.stack = stack;
    }

    public void validarDados() {

        Optional.of(this).orElseThrow(IllegalStateException::new);

        if (StringHelper.isBlank(this.nascimento)) {
            throw new IllegalStateException("Data de nascimento não pode ser vazia");
        }

        if (StringHelper.isBlank(this.nome) || this.nome.length() > 32) {
            throw new IllegalStateException("Nome não pode ser vazio ou maior que 32 caracteres");
        } else {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-'` ]?[A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
            Matcher matcher = pattern.matcher(this.nome);
            if (!matcher.find()) throw new IllegalStateException("Nome não pode conter caracteres especiais");
        }

        if (StringHelper.isBlank(this.apelido) || this.apelido.length() > 32) {
            throw new IllegalStateException("Apelido não pode ser vazio ou maior que 32 caracteres");
        } else {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-'` ]?[A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
            Matcher matcher = pattern.matcher(this.apelido);
            if (!matcher.find()) throw new IllegalStateException("Apelido não pode conter caracteres especiais");
        }
    }
}