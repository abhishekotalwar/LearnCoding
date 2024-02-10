"role": {
    "Admin": [
      "469G",
      "7039"
    ],
    "Support": [
      "4001",
      "5220"
    ],
    "Root": [
      "ALL"
    ]
  }
      Map<String, List<String>> role = roles.stream().collect(Collectors.groupingBy(itm -> itm.getRole().getRoleCode(),
               Collectors.mapping(UserRole::getRole, Collectors.toList())));

--------------------------


  
