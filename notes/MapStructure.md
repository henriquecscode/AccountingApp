# Custom fields in DTO
@Mapping(target = "fullName", source = ".", qualifiedByName = "getFullName")
AppUserDTO toDTO(AppUser user);
target = "fullName" → Set the fullName field in AppUserDTO
source = "." → Use the entire AppUser object as input (not just one field)
qualifiedByName = "getFullName" → Use the method marked with @Named("getFullName")

# Mapper ignores
@BeanMapping(ignoreByDefault = true)