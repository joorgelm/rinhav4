package rinhav4.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import rinhav4.domain.Pessoa;
import rinhav4.domain.PessoaService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @Post(value = "/pessoas", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Object> cadastrar(@Body Pessoa pessoa) {
        String id = pessoaService.salvar(pessoa);
        return HttpResponse.status(HttpStatus.CREATED)
                .headers(headers -> headers.location(URI.create("/pessoas/" + id)));
    }

    @Get("/pessoas/{id}")
    public HttpResponse<Pessoa> buscaPorId(@PathVariable UUID id) {

        Pessoa pessoa = pessoaService.buscaPorId(id);

        return HttpResponse.status(HttpStatus.OK).body(pessoa);
    }

    @Get("/pessoas/apelidos/{apelido}")
    public HttpResponse<Void> buscaPorApelido(@PathVariable String apelido) {

        if (pessoaService.buscaPorApelido(apelido)) {
            return HttpResponse.status(HttpStatus.OK);
        }

        return HttpResponse.status(HttpStatus.NOT_FOUND);
    }

    @Get("/pessoas")
    public HttpResponse<List<Pessoa>> buscaPorTermo(@QueryValue String t) {

        List<Pessoa> pessoas = pessoaService.buscaPorTermo(t);

        return HttpResponse.status(HttpStatus.OK).body(pessoas);
    }

    @Get("/contagem-pessoas")
    public HttpResponse<Long> contagem() {
        return HttpResponse.status(HttpStatus.OK).body(pessoaService.contagemPessoas());
    }
}
