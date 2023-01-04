package projects.cxx.custom.dbconnection

import de.kairos.fhir.centraxx.metamodel.enums.GenderType
import groovy.sql.Sql

import javax.sql.DataSource
import java.lang.reflect.Method

import static de.kairos.fhir.centraxx.metamodel.RootEntities.patientMasterDataAnonymous

/**
 * Represented by a CXX PatientMasterDataAnonymous
 * @author Mike Wähnert
 * @since v.1.5.0, CXX.v.3.17.1.5
 */
patient {

  final String oid = context.source[patientMasterDataAnonymous().patientContainer().id()]

  id = "Patient/" + oid
  gender = loadGenderFromDb(oid)
}

static String loadGenderFromDb(final String oid) {
  final String cxxGender = queryFromDb(oid)
  return cxxGender == null ? null : mapToFhirGender(cxxGender as GenderType)
}

static String queryFromDb(final String OID) {

  // path to jdbc driver, e.g. for MSSQL from https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
  final def localFile = new File("C:/temp/mssql-jdbc-11.2.1.jre11.jar")
  final URLClassLoader cl = new URLClassLoader(localFile.toURI().toURL())

  // load data source class
  final Class beanClass = cl.loadClass("com.microsoft.sqlserver.jdbc.SQLServerDataSource")
  final DataSource dataSource = beanClass.getDeclaredConstructor().newInstance() as DataSource

  // TODO: set connection string and credentials
  invoke(dataSource, "setURL", "jdbc:sqlserver://localhost:1433;databaseName=KAIROS_SPRING;encrypt=false")
  invoke(dataSource, "setUser", "username")
  invoke(dataSource, "setPassword", "password")
  final def sql = new Sql(dataSource)

  try {
    return sql.firstRow("select GENDER_TYPE from CENTRAXX_PATIENTMASTERDATA_ANO  where PATIENTCONTAINER = " + OID).get("GENDER_TYPE")
  } finally {
    sql.close()
  }
}

static def mapToFhirGender(final GenderType genderType) {
  switch (genderType) {
    case GenderType.MALE: return "male"
    case GenderType.FEMALE: return "female"
    case GenderType.UNKNOWN: return "unknown"
    default: return "other"
  }
}

private static <T> void invoke(final DataSource dataSource, final String methodName, final T value) throws ReflectiveOperationException {
  Class<?> clazz = dataSource.getClass()
  while (null != clazz) {
    try {
      final Method method = clazz.getDeclaredMethod(methodName, value.getClass())
      method.invoke(dataSource, value)
      return
    }
    catch (NoSuchMethodException ignored) {
      clazz = clazz.getSuperclass()
    }
  }

  throw new NoSuchMethodException(dataSource.getClass() + "." + methodName + "(" + value.getClass() + ")")
}

