import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

@Autowired
	@PersistenceContext(unitName = "employeePU")
	private EntityManager entityManager;

public void refresEmployee(String p_last_run_date, int batch_size) {

		StoredProcedureQuery query = entityManager
				.createStoredProcedureQuery("Emp_Refresh_Procedure.refresh_employee")
				.registerStoredProcedureParameter("p_last_run_date", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("batch_size", Integer.class, ParameterMode.IN)
				.setParameter("p_last_run_date", p_last_run_date).setParameter("batch_size", batch_size);

		query.execute();
	}
