   private <T> void setValueIfNotNull(Supplier<T> source, Consumer<T> target) {
      T value = source.get();
      if (value != null) {
         target.accept(value);
      }
   }


   private Employee createOrUpdate(EmployeeApiModel employeeApiModel, Employee employee) {
      if (Objects.isNull(employee)) {
         employee = new Employee();
      }

      employee.setLastModifiedOn(instantSupplier.get());
      employee.setCreated(instantSupplier.get());

      setValueIfNotNull(employeeApiModel::name, employee::setName);
      setValueIfNotNull(employeeApiModel::address, employee::setAddress);
      setValueIfNotNull(employeeApiModel::role, employee::setRole);

      return employee;
   }
