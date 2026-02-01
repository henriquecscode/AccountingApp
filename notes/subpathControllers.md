Option 1: Base Controller with Template Method
java@RestController
public abstract class DomainBaseController {
    
    @Autowired
    protected DomainService domainService;
    
    protected record DomainContext(String owner, String slug, String username, Long domainId) {}
    
    protected <T> ResponseEntity<BasicResponse<T>> executeDomainOperation(
            String owner,
            String slug,
            HttpServletRequest httpRequest,
            Function<DomainContext, Output<T>> operation,
            Function<T, ?> responseMapper
    ) {
        // Get username
        String username = SecurityUtil.GetRequestAppUserUsername();
        
        // Check access
        Output<HasDomainReadAccessResult> accessOutput = 
            domainService.assertDomainReadAccess(owner, slug, username);
        
        if (accessOutput.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, accessOutput);
        }
        
        Long domainId = accessOutput.getData().get().domainId();
        DomainContext context = new DomainContext(owner, slug, username, domainId);
        
        // Execute operation
        Output<T> output = operation.apply(context);
        
        if (output.isFailure()) {
            return OutputFailureHandler.handleOutputFailure(httpRequest, output);
        }
        
        return ResponseEntity.ok(BasicResponse.success(
            responseMapper.apply(output.getData().get())
        ));
    }
}
Then your controllers become:
java@RestController
@RequestMapping("api/domain")
public class DomainController extends DomainBaseController {
    
    @GetMapping("/{owner}/{slug}")
    public ResponseEntity<BasicResponse<DomainDetailResponse>> detail(
            @PathVariable String owner,
            @PathVariable String slug,
            HttpServletRequest httpRequest
    ) {
        return executeDomainOperation(
            owner, slug, httpRequest,
            ctx -> domainService.getDomainDetail(ctx.domainId()),
            result -> new DomainDetailResponse(
                ((DomainDetailResult) result).domain(),
                ((DomainDetailResult) result).domainAppUsers()
            )
        );
    }
}

@RestController
@RequestMapping("api/domain/{owner}/{slug}/platform")
public class PlatformController extends DomainBaseController {
    
    @Autowired
    private PlatformService platformService;
    
    @GetMapping("/{platformId}")
    public ResponseEntity<BasicResponse<PlatformResponse>> getPlatform(
            @PathVariable String owner,
            @PathVariable String slug,
            @PathVariable Long platformId,
            HttpServletRequest httpRequest
    ) {
        return executeDomainOperation(
            owner, slug, httpRequest,
            ctx -> platformService.getPlatform(ctx.domainId(), platformId),
            result -> new PlatformResponse((Platform) result)
        );
    }
}