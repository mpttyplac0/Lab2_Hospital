package com.example.hospital.model.dao.jdbc;

import com.example.hospital.model.dao.*;

public class JdbcDaoFactory extends DaoFactory {

    @Override
    public DoctorDao createDoctorDao() {
        return new JdbcDoctorDaoImpl();
    }

    @Override
    public PatientDao createPatientDao() {
        return new JdbcPatientDaoImpl();
    }

    @Override
    public AppointmentDao createAppointmentDao() {
        // JdbcAppointmentDaoImpl може потребувати інші DAO для завантаження пов'язаних сутностей
        return new JdbcAppointmentDaoImpl(createDoctorDao(), createPatientDao());
    }
}