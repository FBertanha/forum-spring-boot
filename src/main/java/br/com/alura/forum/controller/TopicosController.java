package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/topicos")
public class TopicosController {

    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<TopicoDto> lista(String nomeCurso) {
        if (nomeCurso == null) {
            List<Topico> topicos = topicoRepository.findAll();
            return TopicoDto.converter(topicos);

        } else {
            List<Topico> cursos = topicoRepository.findByCursoNome(nomeCurso);
            return TopicoDto.converter(cursos);
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm topicoForm, UriComponentsBuilder uriBuilder) {
        Topico topico = topicoForm.converter(cursoRepository);
        topicoRepository.save(topico);

        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
        return topicoRepository.findById(id)
                .map(t -> ResponseEntity.ok().body(new DetalhesTopicoDto(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm topicoForm) {
        return topicoRepository.findById(id)
                .map(t -> {
                            Topico topicoAtualizado = topicoForm.atualizar(t);
                            return ResponseEntity.ok(new TopicoDto(topicoAtualizado));
                        }
                ).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    @Transactional
    public ResponseEntity<?> remover(@PathVariable Long id) {
        return topicoRepository.findById(id)
                .map(t -> {
                            topicoRepository.deleteById(id);
                            return ResponseEntity.ok(new TopicoDto(t));
                        }
                ).orElse(ResponseEntity.notFound().build());

    }
}
