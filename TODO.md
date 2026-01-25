# Steps
## Must

https://www.geeksforgeeks.org/springboot/request-body-and-parameter-validation-with-spring-boot/

Use message with the error codes that I have been returning instead?
Backend Base controler:
    @RestController
    @RequestMapping("/domain/{owner}/{slug}/files")
    public class DomainFilesController extends BaseDomainController {

        public DomainFilesController(DomainService domainService) {
            super(domainService);
        }

        @GetMapping
        public List<FileDto> listFiles(@PathVariable String owner, @PathVariable String slug) {
            Domain domain = getDomain(owner, slug);
            return domainService.listFiles(domain);
        }
    }
## Nice to have

Logging in the backend
Output.failure with data objects for better keeping track of what exactly failed
Domain page
    Datbase Indexes
Disable multiple clicks
Finish @Valid @RequestBody logic
    See https://claude.ai/share/fac57841-b73a-4f6f-8265-c4c5d92cfc4f
    // dto/CreateDomainRequest.java
    public class CreateDomainRequest {
    @NotBlank(message = "Domain name is required")
    @Size(min = 3, max = 100)
    private String name;
    
    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Invalid slug format")
    @Size(min = 3, max = 100)
    private String slug;
    
    @Size(max = 500)
    private String description;
    
    // Getters, setters
    }
Generalize error handling for every component in the frontend that can be reused easily
     Introduce default errors for status 0 (for example the server being offline)
Generalize error handling to do a response for every component in the frontend
when failing with error undefined should also have an error code -> Test by killing application while running request
Lombok 
    Lombok with mapstruct integration
        https://www.baeldung.com/java-mapstruct-lombok
Save and manipulate device information
# We will get there
How to do proper forms validation on the backend controller level?
## QOL
Start making components to make frontned consistent while using Claude AI?
Serve different paths for multiple localizations at the same time
Containerize backend