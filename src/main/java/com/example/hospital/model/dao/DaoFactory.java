package com.example.hospital.model.dao;

public abstract class DaoFactory {

    private static DaoFactory daoFactoryInstance;

    public abstract DoctorDao createDoctorDao();
    public abstract PatientDao createPatientDao();
    public abstract AppointmentDao createAppointmentDao();

    public static DaoFactory getInstance() {
        if (daoFactoryInstance == null) {
            synchronized (DaoFactory.class) {
                if (daoFactoryInstance == null) {
                    try {
                        String factoryClassName = "com.example.hospital.model.dao.jdbc.JdbcDaoFactory";
                        daoFactoryInstance = (DaoFactory) Class.forName(factoryClassName)
                                .getDeclaredConstructor()
                                .newInstance();
                    } catch (Exception e) {
                        System.err.println("Error creating DaoFactory instance: " + e.getMessage());
                        throw new RuntimeException("Failed to create DaoFactory instance", e);
                    }
                }
            }
        }
        return daoFactoryInstance;
    }
}