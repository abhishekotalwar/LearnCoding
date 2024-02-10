
import java.util.List;
import java.util.stream.Collectors;

public interface EntityModelMapper<ENTITY, MODEL> {

   MODEL addAllMappings(ENTITY entity);

   default MODEL map(ENTITY entity) {
      return addAllMappings(entity);
   }

   default List<MODEL> mapAll(List<ENTITY> entities) {

      return entities.stream().map(this::map).collect(Collectors.toList());
   }
}
