package edu.udelar.ayudemos.repartidor.application;

import edu.udelar.ayudemos.common.exception.EmailAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.NumeroLicenciaAlreadyExistsException;
import edu.udelar.ayudemos.repartidor.application.exception.RepartidorNotFoundException;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import edu.udelar.ayudemos.repartidor.infrastructure.RepartidorRepository;
import edu.udelar.ayudemos.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Repartidor crearRepartidor(final Repartidor repartidor) {
        validarCorreoUnico(repartidor.getCorreo(), null);
        validarNumeroLicenciaUnico(repartidor.getNumeroLicencia(), null);
        return repartidorRepository.save(repartidor);
    }

    @Transactional(readOnly = true)
    public List<Repartidor> listarRepartidores() {
        return repartidorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Repartidor obtenerPorId(final Long id) {
        return repartidorRepository.findById(id)
                .orElseThrow(() -> new RepartidorNotFoundException(id));
    }

    @Transactional
    public Repartidor actualizarRepartidor(final Long id, final Repartidor repartidorActualizado) {
        final Repartidor repartidor = obtenerPorId(id);

        validarCorreoUnico(repartidorActualizado.getCorreo(), repartidor.getId());
        validarNumeroLicenciaUnico(repartidorActualizado.getNumeroLicencia(), repartidor.getId());

        repartidor.setNombre(repartidorActualizado.getNombre());
        repartidor.setCorreo(repartidorActualizado.getCorreo());
        repartidor.setNumeroLicencia(repartidorActualizado.getNumeroLicencia());

        return repartidorRepository.save(repartidor);
    }

    private void validarCorreoUnico(final String correo, final Long usuarioPermitidoId) {
        usuarioRepository.findByCorreo(correo)
                .filter(usuario -> usuarioPermitidoId == null || !usuario.getId().equals(usuarioPermitidoId))
                .ifPresent(usuario -> {
                    throw new EmailAlreadyExistsException(correo);
                });
    }

    private void validarNumeroLicenciaUnico(final String numeroLicencia, final Long repartidorPermitidoId) {
        repartidorRepository.findByNumeroLicencia(numeroLicencia)
                .filter(repartidor -> repartidorPermitidoId == null || !repartidor.getId().equals(repartidorPermitidoId))
                .ifPresent(repartidor -> {
                    throw new NumeroLicenciaAlreadyExistsException(numeroLicencia);
                });
    }
}
