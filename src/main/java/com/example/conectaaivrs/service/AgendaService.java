package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.agenda.Agenda;
import com.example.conectaaivrs.domain.agenda.AgendaRepository;
import com.example.conectaaivrs.domain.agenda.dto.AgendaRequest;
import com.example.conectaaivrs.domain.agenda.dto.AgendaResponse;
import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.evento.EventoRepository;
import com.example.conectaaivrs.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public List<AgendaResponse> listar(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        return agendaRepository.findAllByEventoIdOrderByInicioAsc(eventoId)
                .stream()
                .map(AgendaResponse::fromEntity)
                .toList();
    }

    public AgendaResponse criar(Usuario usuarioLogado, AgendaRequest request) {
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        if (!evento.getOrganizador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o organizador pode criar itens na agenda");
        }

        if (request.fim().isBefore(request.inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário de fim deve ser posterior ao de início");
        }

        Agenda agenda = Agenda.builder()
                .evento(evento)
                .titulo(request.titulo())
                .descricao(request.descricao())
                .categoria(request.categoria())
                .local(request.local())
                .inicio(request.inicio())
                .fim(request.fim())
                .ativo(request.ativo() != null ? request.ativo() : true)
                .build();

        agendaRepository.save(agenda);
        return AgendaResponse.fromEntity(agenda);
    }

    public AgendaResponse atualizar(Usuario usuarioLogado, UUID agendaId, AgendaRequest request) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de agenda não encontrado"));

        if (!agenda.getEvento().getOrganizador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o organizador pode alterar itens da agenda");
        }

        if (request.fim().isBefore(request.inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário de fim deve ser posterior ao de início");
        }

        agenda.setTitulo(request.titulo());
        agenda.setDescricao(request.descricao());
        agenda.setCategoria(request.categoria());
        agenda.setLocal(request.local());
        agenda.setInicio(request.inicio());
        agenda.setFim(request.fim());
        agenda.setAtivo(request.ativo() != null ? request.ativo() : agenda.getAtivo());

        agendaRepository.save(agenda);
        return AgendaResponse.fromEntity(agenda);
    }

    public void deletar(Usuario usuarioLogado, UUID agendaId) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de agenda não encontrado"));

        if (!agenda.getEvento().getOrganizador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o organizador pode excluir itens da agenda");
        }

        agendaRepository.delete(agenda);
    }
}
