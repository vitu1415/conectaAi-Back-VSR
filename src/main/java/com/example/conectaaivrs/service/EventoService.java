package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.auth.dto.EventoRequest;
import com.example.conectaaivrs.domain.auth.dto.EventoResponse;
import com.example.conectaaivrs.domain.evento.Evento;
import com.example.conectaaivrs.domain.evento.EventoRepository;
import com.example.conectaaivrs.domain.evento.EventoStatus;
import com.example.conectaaivrs.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<EventoResponse> listarTodos() {
        return eventoRepository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(EventoResponse::fromEntity)
                .toList();
    }

    public List<EventoResponse> listarDestaques() {
        return eventoRepository.findByStatusOrderByCriadoEmDesc(EventoStatus.PUBLICADO)
                .stream()
                .limit(6)
                .map(EventoResponse::fromEntity)
                .toList();
    }

    public List<EventoResponse> listarProximos() {
        return eventoRepository.findByInicioAfterOrderByInicioAsc(LocalDateTime.now())
                .stream()
                .map(EventoResponse::fromEntity)
                .toList();
    }

    public EventoResponse buscarPorId(UUID id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
        return EventoResponse.fromEntity(evento);
    }

    @Transactional
    public EventoResponse criar(Usuario organizador, EventoRequest request) {
        if (request.fim().isBefore(request.inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim deve ser posterior à data de início");
        }

        Evento evento = Evento.builder()
                .organizador(organizador)
                .nome(request.nome())
                .descricao(request.descricao())
                .banner(request.banner())
                .cidade(request.cidade())
                .estado(request.estado())
                .endereco(request.endereco())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .inicio(request.inicio())
                .fim(request.fim())
                .capacidade(request.capacidade())
                .privado(request.privado() != null ? request.privado() : false)
                .status(request.status() != null ? request.status() : EventoStatus.PUBLICADO)
                .build();

        eventoRepository.save(evento);
        return EventoResponse.fromEntity(evento);
    }

    @Transactional
    public EventoResponse atualizar(Usuario usuarioLogado, UUID eventoId, EventoRequest request) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        if (!evento.getOrganizador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o organizador pode alterar o evento");
        }

        if (request.fim().isBefore(request.inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data de fim deve ser posterior à data de início");
        }

        evento.setNome(request.nome());
        evento.setDescricao(request.descricao());
        evento.setBanner(request.banner());
        evento.setCidade(request.cidade());
        evento.setEstado(request.estado());
        evento.setEndereco(request.endereco());
        evento.setLatitude(request.latitude());
        evento.setLongitude(request.longitude());
        evento.setInicio(request.inicio());
        evento.setFim(request.fim());
        evento.setCapacidade(request.capacidade());
        evento.setPrivado(request.privado() != null ? request.privado() : false);
        evento.setStatus(request.status() != null ? request.status() : EventoStatus.PUBLICADO);

        eventoRepository.save(evento);
        return EventoResponse.fromEntity(evento);
    }

    @Transactional
    public void deletar(Usuario usuarioLogado, UUID eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        if (!evento.getOrganizador().getId().equals(usuarioLogado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas o organizador pode excluir o evento");
        }

        eventoRepository.delete(evento);
    }
}
