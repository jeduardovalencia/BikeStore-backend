package com.bike.shop.service;

import com.bike.shop.dto.request.ConfiguracionRequestDTO;
import com.bike.shop.dto.response.ConfiguracionResponseDTO;
import com.bike.shop.entity.Configuracion;
import com.bike.shop.repository.ConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    /**
     * Obtiene la configuración global. Si no existe, crea una con valores por defecto.
     */
    public ConfiguracionResponseDTO obtener() {
        return toDTO(obtenerEntidad());
    }

    public Configuracion obtenerEntidad() {
        List<Configuracion> lista = configuracionRepository.findAll();
        if (lista.isEmpty()) {
            Configuracion defecto = new Configuracion();
            defecto.setNombreTienda("BikeStore");
            defecto.setUmbralAprobacionVenta(new BigDecimal("2000000.00"));
            defecto.setUmbralAprobacionPedido(new BigDecimal("5000000.00"));
            defecto.setHorarioInicio(LocalTime.of(8, 0));
            defecto.setHorarioFin(LocalTime.of(18, 0));
            return configuracionRepository.save(defecto);
        }
        return lista.get(0);
    }

    @Transactional
    public ConfiguracionResponseDTO actualizar(ConfiguracionRequestDTO dto) {
        Configuracion config = obtenerEntidad();
        config.setNombreTienda(dto.getNombreTienda());
        config.setUmbralAprobacionVenta(dto.getUmbralAprobacionVenta());
        config.setUmbralAprobacionPedido(dto.getUmbralAprobacionPedido());
        config.setHorarioInicio(dto.getHorarioInicio());
        config.setHorarioFin(dto.getHorarioFin());
        return toDTO(configuracionRepository.save(config));
    }

    private ConfiguracionResponseDTO toDTO(Configuracion c) {
        return new ConfiguracionResponseDTO(
                c.getId(),
                c.getNombreTienda(),
                c.getUmbralAprobacionVenta(),
                c.getUmbralAprobacionPedido(),
                c.getHorarioInicio(),
                c.getHorarioFin()
        );
    }
}
