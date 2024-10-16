package net.pladema.medicalconsultation.doctorsoffice.service.impl;

import net.pladema.emergencycare.repository.EmergencyCareEpisodeRepository;
import net.pladema.medicalconsultation.doctorsoffice.repository.DoctorsOfficeRepository;
import net.pladema.medicalconsultation.doctorsoffice.repository.domain.DoctorsOfficeVo;
import net.pladema.medicalconsultation.doctorsoffice.service.DoctorsOfficeService;
import net.pladema.medicalconsultation.doctorsoffice.service.domain.DoctorsOfficeBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DoctorsOfficeServiceImpl implements DoctorsOfficeService {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorsOfficeServiceImpl.class);

    private static final String LOGGING_OUTPUT = "Output -> {}";

    private final DoctorsOfficeRepository doctorsOfficeRepository;

	private final EmergencyCareEpisodeRepository emergencyCareEpisodeRepository;

    public DoctorsOfficeServiceImpl(DoctorsOfficeRepository doctorsOfficeRepository, EmergencyCareEpisodeRepository emergencyCareEpisodeRepository){
        super();
        this.doctorsOfficeRepository = doctorsOfficeRepository;
		this.emergencyCareEpisodeRepository = emergencyCareEpisodeRepository;
	}

    @Override
    public List<DoctorsOfficeBo> getAllDoctorsOffice(Integer institutionId, Integer sectorId) {
        LOG.debug("Input parameters -> institutionId {}, sectorId {}",
                institutionId, sectorId);
        List<DoctorsOfficeVo> resultQuery = doctorsOfficeRepository.findAllBy(institutionId, sectorId);
        List<DoctorsOfficeBo> result = new ArrayList<>();
        resultQuery.forEach(doctorsOfficeVo -> result.add(new DoctorsOfficeBo(doctorsOfficeVo)));
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public List<DoctorsOfficeBo> getDoctorsOfficeBySectorType(Integer institutionId, Short sectorTypeId) {
        LOG.debug("Input parameters -> institutionId {}, sectorTypeId {}", institutionId, sectorTypeId);
        List<DoctorsOfficeVo> resultQuery = doctorsOfficeRepository.findAllBySectorType(institutionId, sectorTypeId);
        List<DoctorsOfficeBo> result = new ArrayList<>();
        resultQuery.forEach(doctorsOfficeVo -> {
			boolean isAvailable = emergencyCareEpisodeRepository.existsEpisodeInOffice(doctorsOfficeVo.getId(), null) == 0;
			DoctorsOfficeBo doctorsOfficeBo = new DoctorsOfficeBo(doctorsOfficeVo);
			doctorsOfficeBo.setAvailable(isAvailable);
			result.add(doctorsOfficeBo);
		});
        LOG.debug(LOGGING_OUTPUT, result);
        return result;
    }

    @Override
    public DoctorsOfficeBo getById(Integer doctorsOfficeId) {
        LOG.debug("Input parameter -> doctorsOfficeId {}", doctorsOfficeId);
        return doctorsOfficeRepository.findById(doctorsOfficeId).map(doctorsOffice -> {
			DoctorsOfficeBo result = new DoctorsOfficeBo(doctorsOffice);
			LOG.debug(LOGGING_OUTPUT, result);
			return result;
		}).get();
    }

	@Override
	public String getDescription(Integer doctorsOfficeId) {
		LOG.debug("Input parameter -> doctorsOfficeId {}", doctorsOfficeId);
		String result = doctorsOfficeRepository.getDescription(doctorsOfficeId);
		LOG.debug(LOGGING_OUTPUT, result);
		return result;
	}

	@Override
	public Integer getSectorId(Integer doctorsOfficeId) {
		LOG.debug("Input parameter -> doctorsOfficeId {}", doctorsOfficeId);
		Integer result = doctorsOfficeRepository.getSectorId(doctorsOfficeId);
		LOG.debug(LOGGING_OUTPUT, result);
		return result;
	}
}
