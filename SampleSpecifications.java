import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.Predicate;

@Component
public class UserSpecifications {

   public Specification<User> withUserCode(String userCode) {
      return (root, query, criteriaBuilder) -> {
         if (userCode != null) {
            return criteriaBuilder.equal(root.get("userCode"), userCode);
         }
         return null;
      };
   }

   public Specification<User> withName(String name) {
      return (root, query, criteriaBuilder) -> {
         if (name != null && !name.isEmpty()) {
            String[] names = name.toLowerCase().split("\\s+");
            List<Predicate> predicates = Arrays.stream(names)
                     .map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + n + "%")).toList();
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
         }
         return null;
      };
   }

   public Specification<User> withTCode(String tCode) {
      return (root, query, criteriaBuilder) -> {
         if (tCode != null) {
            return criteriaBuilder.equal(root.join("test").get("tCode"), tCode);
         }
         return null;
      };
   }

   public Specification<User> withUserVersion(Byte version) {
      return (root, query, criteriaBuilder) -> {
         if (version != null) {
            return criteriaBuilder.equal(root.get("userVersion"), version);
         }
         return null;
      };
   }

   public Specification<User> withActiveId(Byte activeId) {
      return (root, query, criteriaBuilder) -> {
         if (activeId != null) {
            return criteriaBuilder.equal(root.get("activeStatus"), activeId);
         }
         return null;
      };
   }
}


-------how to use----------------

   private final UserSpecifications userSpecifications;

   public List<User> findAll(UserSearchParam userSearchParam) {
      Specification<User> spec = Specification.where(null);

      String userCode = userSearchParam.getUserCode();
      if (Objects.nonNull(userCode)) {
         spec = spec.and(userSpecifications.withUserCode(userCode));
      }

      String name = userSearchParam.getName();
      if (Objects.nonNull(name)) {
         spec = spec.and(userSpecifications.withName(name));
      }

      String tCode = userSearchParam.getTCode();
      if (Objects.nonNull(tCode)) {
         spec = spec.and(userSpecifications.withTCode(tCode));
      }

      Byte userVersion = userSearchParam.getUserVersion();
      if (Objects.nonNull(userVersion)) {
         spec = spec.and(userSpecifications.withUserVersion(rdrVersion));
      }


      Byte activeId = userSearchParam.getActiveId();
      if (Objects.nonNull(activeId)) {
         spec = spec.and(userSpecifications.withActiveId(cstId));
      }

      return userJpaRepository.findAll(spec);
   }


  
