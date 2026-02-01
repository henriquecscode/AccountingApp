# Steps
## Must
Simplify hasRead and hasAdmin access by sharing code and passing the right function
    Also, allow to take as an input a vetted read/admin access from upper in the hierarchy. FOr example, for platform read it needs domain read. But if I pass domainRead access object correctly then that one passes.
    Every access result object should be the same (because they just different by the process, not by what they represent)
        But if I do that, then how do I implement higher access vetting?
            Maybe pass the role too?
DomainCreate and platform create must return owner/sug or domain/slug and redirect to it
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
Analyse if I should change authorization to inside the service.
    What if authorization is more complex than the simple roles? (not a case yet ig)
Create a different navbar just for the domain. The main user navbar is kind of useless since most operations are going to be inside a domain in any case.   
Backend: Create entry path to allow for debugging with postman without authentication
Half success states. For example, fetching domain information is success, but fetching platform information is failure.
    I can still show some information to the user while giving a warning (not necessarily a failure)
Make /platform/ redirect to a page dedicated to just the platforms
Hierarchy controllers [](./notes/subpathControllers.md)
Auth.service in frontend should have our name stored somewhere so that we can compare to assign permissions and whatnot
Logging in the backend
Output.failure with data objects for better keeping track of what exactly failed
Domain page
    Datbase Indexes
Disable multiple clicks for create buttons
Start partitioning everything into domains. Every table inside of a domain could have a domain and that is partitioned.
    Worst case I can always do that after i have "the full" application
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

# We will get there
Save and manipulate device information
How to do proper forms validation on the backend controller level?
## QOL
Start making components to make frontned consistent while using Claude AI?
Serve different paths for multiple localizations at the same time
Containerize backend