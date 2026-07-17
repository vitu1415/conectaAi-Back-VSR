package com.example.conectaaivrs.service;

import com.example.conectaaivrs.domain.inscricao.dto.MeuEventoResponse;
import com.example.conectaaivrs.domain.inscricao.dto.ParticipanteResponse;
import com.example.conectaaivrs.domain.evento.*;
import com.example.conectaaivrs.domain.inscricao.ParticipanteEvento;
import com.example.conectaaivrs.domain.inscricao.ParticipanteEventoRepository;
import com.example.conectaaivrs.domain.inscricao.ParticipantePapel;
import com.example.conectaaivrs.domain.inscricao.ParticipanteStatus;
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
public class InscricaoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ParticipanteEventoRepository participanteRepository;

    @Transactional
    public ParticipanteResponse participar(Usuario usuario, UUID eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));

        if (evento.getStatus() != EventoStatus.PUBLICADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento não está aberto para inscrições");
        }

        if (evento.getFim().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento já encerrou");
        }

        if (participanteRepository.existsByEventoIdAndUsuarioId(eventoId, usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já está inscrito neste evento");
        }

        if (evento.getCapacidade() != null) {
            long count = participanteRepository.countByEventoId(eventoId);
            if (count >= evento.getCapacidade()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento lotado");
            }
        }

        ParticipanteEvento participante = ParticipanteEvento.builder()
                .evento(evento)
                .usuario(usuario)
                .papel(ParticipantePapel.PARTICIPANTE)
                .status(ParticipanteStatus.INSCRITO)
                .build();

        participanteRepository.save(participante);
        return ParticipanteResponse.fromEntity(participante);
    }

    public void cancelarParticipacao(Usuario usuario, UUID eventoId) {
        ParticipanteEvento participante = participanteRepository
                .findByEventoIdAndUsuarioId(eventoId, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Você não está inscrito neste evento"));

        participanteRepository.delete(participante);
    }

    public List<ParticipanteResponse> listarParticipantes(UUID eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }

        return participanteRepository.findAllByEventoIdOrderByDataInscricaoAsc(eventoId)
                .stream()
                .map(ParticipanteResponse::fromEntity)
                .toList();
    }

    public List<MeuEventoResponse> listarMeusEventos(Usuario usuario) {
        return participanteRepository.findAllByUsuarioIdOrderByDataInscricaoDesc(usuario.getId())
                .stream()
                .map(MeuEventoResponse::fromEntity)
                .toList();
    }
}
